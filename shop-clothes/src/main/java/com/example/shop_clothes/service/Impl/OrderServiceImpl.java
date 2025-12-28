package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.config.VnpayConfig;
import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;
import com.example.shop_clothes.dto.vnpay.VnpayPaymentResponse;
import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.enums.PaymentMethod;
import com.example.shop_clothes.enums.ShippingMethod;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.model.*;
import com.example.shop_clothes.repository.*;
import com.example.shop_clothes.service.OrderService;
import com.example.shop_clothes.service.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductDetailRepository productDetailRepository;
    private final OrderPaymentSnapshotRepository snapshotRepository;
    private final VnpayPaymentService vnpayPaymentService;

    @Value("${vnpay.timeout-minutes}")
    private Integer paymentTimeoutMinutes;

    @Transactional
    @Override
    public OrderResponse orderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest, HttpServletRequest httpRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ApplicationException(ErrorCode.USER_NOT_EXISTED);
        }
        String name = user.getFullname();
        PaymentMethod paymentMethod = PaymentMethod.fromDisplayName(orderFromShopingCartRequest.getPaymentMethod());
        ShippingMethod shippingMethod = ShippingMethod.fromDisplayName(orderFromShopingCartRequest.getShippingMethod());
        Float shippingFee = shippingMethod.getFee();
        String address = user.getAddress();
        if (address == null || address.isEmpty()) {
            throw new ApplicationException(ErrorCode.ORDER_ADDRESS_REQUIRED);
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new ApplicationException(ErrorCode.PHONE_NUMBER_REQUIRED);
        }
        List<CartItem> cartItems = cartItemRepository.findByIdIn(orderFromShopingCartRequest.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new ApplicationException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        Order order = new Order();
        order.setUser(user);
        order.setNote(orderFromShopingCartRequest.getNote());
        order.setShippingFee(shippingFee);
        order.setPaymentMethod(paymentMethod);
        order.setAddress(address);
        order.setShippingMethod(shippingMethod);
        order.setFullname(name);
        order.setEmail(email);
        order.setPhoneNumber(user.getPhoneNumber());
        order.setShippingAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingDate(LocalDate.now().plusDays(1));
        order.setStatus(OrderStatus.PAYMENT_WAITING);
        order.setActive(true);
        order.setPaymentExpireTime(LocalDateTime.now().plusMinutes(paymentTimeoutMinutes));
        Order saveOrder = orderRepository.save(order);
        Float totalMoney = 0f;
        List<OrderResponse.OrderItemDetailResponse> orderItemDetailResponseList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Float itemTotalMoney = cartItem.getPrice() * cartItem.getQuantity();
            ProductDetail productDetail = productDetailRepository.findById(cartItem.getProductDetail().getId())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.PRODUCT_DETAIL_NOT_EXISTED));
            if (productDetail.getQuantity() < cartItem.getQuantity()) {
                throw new ApplicationException(ErrorCode.INSUFFICIENT_STOCK);
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(saveOrder)
                    .product(cartItem.getProduct())
                    .productDetail(cartItem.getProductDetail())
                    .size(productDetail.getSize())
                    .color(productDetail.getColor())
                    .price(cartItem.getPrice())
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(itemTotalMoney)
                    .build();

            orderDetailRepository.save(orderDetail);
            totalMoney += itemTotalMoney;

            OrderPaymentSnapshot snapshot = OrderPaymentSnapshot.builder()
                    .order(saveOrder)
                    .productDetail(productDetail)
                    .originalQuantity(productDetail.getQuantity())
                    .quantityReserved(cartItem.getQuantity())
                    .build();
            snapshotRepository.save(snapshot);

            //
            Integer updateQuantity = productDetail.getQuantity() - cartItem.getQuantity();
            productDetail.setQuantity(updateQuantity);
            productDetailRepository.save(productDetail);

            OrderResponse.OrderItemDetailResponse orderItemDetailResponse = OrderResponse.OrderItemDetailResponse
                    .builder()
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .color(productDetail.getColor())
                    .size(productDetail.getSize())
                    .price(cartItem.getPrice())
                    .totalMoney(itemTotalMoney)
                    .build();
            orderItemDetailResponseList.add(orderItemDetailResponse);
        }
        //
        Float finalTotal = totalMoney + (shippingFee != null ? shippingFee : 0f);
        saveOrder.setTotalMoney(finalTotal);
        orderRepository.save(saveOrder);
        //
        //cartItemRepository.deleteAll(cartItems);
        //
        return handlePayment(saveOrder, paymentMethod, httpRequest, orderItemDetailResponseList);
    }

    private OrderResponse handlePayment(Order order, PaymentMethod paymentMethod,
                                        HttpServletRequest httpRequest,
                                        List<OrderResponse.OrderItemDetailResponse> items) {
        OrderResponse.OrderResponseBuilder responseBuilder = OrderResponse.builder()
                .fullname(order.getFullname())
                .phoneNumber(order.getPhoneNumber())
                .deliveryAddress(order.getShippingAddress())
                .orderId(order.getId())
                .shippingFee(order.getShippingFee())
                .totalMoney(order.getTotalMoney())
                .status(order.getStatus().getDisplayName())
                .paymentExpireTime(order.getPaymentExpireTime())
                .items(items);

        if (paymentMethod == PaymentMethod.VNPAY) {
            VnpayPaymentResponse vnpayResponse = vnpayPaymentService.createPaymentUrl(order, httpRequest);
            return responseBuilder
                    .message("Vui lòng hoàn tất thanh toán trong " + paymentTimeoutMinutes + " phút")
                    .paymentUrl(vnpayResponse.getPaymentUrl())
                    .build();
        } else if (paymentMethod == PaymentMethod.COD) {
            return responseBuilder
                    .message("Đặt hàng thành công. Vui lòng thanh toán khi nhận hàng")
                    .build();
        } else {
            return responseBuilder
                    .message("Đặt hàng thành công")
                    .build();
        }
    }
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.ORDER_NOT_EXISTED));
        return OrderResponse.builder()
                .orderId(order.getId())
                .fullname(order.getFullname())
                .phoneNumber(order.getPhoneNumber())
                .deliveryAddress(order.getShippingAddress())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus().getDisplayName())
                .build();
    }
//    @Transactional
//    @Override
//    public OrderResponse OrderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email);
//        String name = user.getFullname();
//        PaymentMethod paymentMethod = PaymentMethod.fromDisplayName(orderFromShopingCartRequest.getPaymentMethod());
//        ShippingMethod shippingMethod = ShippingMethod.fromDisplayName(orderFromShopingCartRequest.getShippingMethod());
//        Float shippingFee = shippingMethod.getFee();
//        String address = user.getAddress();
//        if(address == null || address.isEmpty()){
//            throw new RuntimeException("Vui lòng cập nhật địa chỉ giao hàng");
//        }
//        if(user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()){
//            throw new RuntimeException("Vui lòng cập nhật số điện  thoại");
//        }
//        String note = orderFromShopingCartRequest.getNote();
//        List<CartItem> cartItems = cartItemRepository.findByIdIn(orderFromShopingCartRequest.getCartItemIds());
//        if (cartItems.isEmpty()) {
//            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
//        }
//        Order order = new Order();
//        order.setUser(user);
//        order.setNote(note);
//        order.setShippingFee(shippingFee);
//        order.setPaymentMethod(paymentMethod);
//        order.setAddress(address);
//        order.setShippingMethod(shippingMethod);
//        order.setFullname(name);
//        order.setEmail(email);
//        order.setPhoneNumber(user.getPhoneNumber());
//        order.setShippingAddress(address);
//        order.setOrderDate(LocalDateTime.now());
//        order.setShippingDate(LocalDate.now().plusDays(1));
//        order.setStatus(OrderStatus.PAYMENT_WAITING);
//        order.setActive(true);
//        //order.setTrackingNumber("");
//
//        Order saveOrder = orderRepository.save(order);
//        Float totalMoney = 0f;
//        List<OrderResponse.OrderItemDetailResponse> orderItemDetailResponseList = new ArrayList<>();
//        for (CartItem cartItem : cartItems) {
//            Float itemTotalMoney = cartItem.getPrice() * cartItem.getQuantity();
//            ProductDetail productDetail = productDetailRepository.findById(cartItem.getProductDetail().getId())
//            .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));
//            if(productDetail.getQuantity() < cartItem.getQuantity()) {
//                throw new RuntimeException("Không đủ hàng trong kho");
//            }
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .order(saveOrder)
//                    .product(cartItem.getProduct())
//                    .productDetail(cartItem.getProductDetail())
//                    .size(productDetail.getSize())
//                    .color(productDetail.getColor())
//                    .price(cartItem.getPrice())
//                    .numberOfProducts(cartItem.getQuantity())
//                    .totalMoney(itemTotalMoney)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//            totalMoney += itemTotalMoney;
//            // update quantity for productDetail;
//            Integer updateQuantity = productDetail.getQuantity() - cartItem.getQuantity();
//            productDetail.setQuantity(updateQuantity);
//            productDetailRepository.save(productDetail);
//            //
//            OrderResponse.OrderItemDetailResponse orderItemDetailResponse = OrderResponse.OrderItemDetailResponse
//                    .builder()
//                    .productName(cartItem.getProduct().getName())
//                    .quantity(cartItem.getQuantity())
//                    .color(productDetail.getColor())
//                    .size(productDetail.getSize())
//                    .price(cartItem.getPrice())
//                    .totalMoney(itemTotalMoney)
//                    .build();
//            orderItemDetailResponseList.add(orderItemDetailResponse);
//        }
//        Float finalTotal = totalMoney + (shippingFee != null ? shippingFee : 0f);
//        saveOrder.setTotalMoney(finalTotal);
//        orderRepository.save(saveOrder);
//        //cartItemRepository.deleteAll(cartItems);
//
//        return OrderResponse.builder()
//                .fullname(saveOrder.getFullname())
//                .phoneNumber(saveOrder.getPhoneNumber())
//                .deliveryAddress(saveOrder.getShippingAddress())
//                .orderId(order.getId())
//                .shippingFee(saveOrder.getShippingFee())
//                .totalMoney(saveOrder.getTotalMoney())
//                .status(saveOrder.getStatus().getDisplayName())
//                .message("Đặt hàng thành công")
//                .items(orderItemDetailResponseList)
//                .build();
//    }

}
