package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.order.OrderFromShopingCartRequest;
import com.example.shop_clothes.dto.order.OrderResponse;
import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.enums.PaymentMethod;
import com.example.shop_clothes.enums.ShippingMethod;
import com.example.shop_clothes.model.*;
import com.example.shop_clothes.repository.*;
import com.example.shop_clothes.service.OrderService;
import lombok.RequiredArgsConstructor;
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
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    @Transactional
    @Override
    public OrderResponse OrderFromShopingcart(OrderFromShopingCartRequest orderFromShopingCartRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        String name = user.getFullname();
        PaymentMethod paymentMethod = PaymentMethod.fromDisplayName(orderFromShopingCartRequest.getPaymentMethod());
        ShippingMethod shippingMethod = ShippingMethod.fromDisplayName(orderFromShopingCartRequest.getShippingMethod());
        Float shippingFee = shippingMethod.getFee();

        String address = user.getAddress();
        String note = orderFromShopingCartRequest.getNote();
        List<CartItem> cartItems = cartItemRepository.findByIdIn(orderFromShopingCartRequest.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
//        Order order = Order.builder()
//                .user(user)
//                .email(email)
//                .fullname(name)
//                .shippingAddress(address)
//                .orderDate(LocalDateTime.now())
//                .shippingDate(LocalDate.now())
//                .shippingMethod(shippingMethod)
//                .shippingFee(shippingFee)
//                .note(note)
//                .active(true)
//                .paymentMethod(payment)
//                .status(OrderStatus.PENDING)
//                .build();
        Order order = new Order();
        order.setUser(user);
        order.setNote(note);
        order.setShippingFee(shippingFee);
        order.setPaymentMethod(paymentMethod);
        order.setAddress(address);
        order.setShippingMethod(shippingMethod);
        order.setFullname(name);
        order.setEmail(email);
        order.setPhoneNumber(user.getPhoneNumber());
        order.setShippingAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);
        order.setTrackingNumber("123");

        Order saveOrder = orderRepository.save(order);
        Float totalMoney = 0f;
        List<OrderResponse.OrderItemDetailResponse> orderItemDetailResponseList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Float itemTotalMoney = cartItem.getPrice() * cartItem.getQuantity();
            ProductDetail productDetail = productDetailRepository.findById(cartItem.getProductDetail().getId()).orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(saveOrder)
                    .product(cartItem.getProduct())
                    .productDetail(cartItem.getProductDetail())
                    .color(productDetail.getColor())
                    .price(cartItem.getPrice())
                    .numberOfProducts(cartItem.getQuantity())
                    .totalMoney(itemTotalMoney)
                    .build();
            orderDetailRepository.save(orderDetail);
            totalMoney += itemTotalMoney;
            // update quantity for productDetail;
            Integer updateQuantity = productDetail.getQuantity() - cartItem.getQuantity();
            productDetail.setQuantity(updateQuantity);
            productDetailRepository.save(productDetail);
            //
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
        Float finalTotal = totalMoney + (shippingFee != null ? shippingFee : 0f);
        saveOrder.setTotalMoney(finalTotal);
        orderRepository.save(saveOrder);
        //cartItemRepository.deleteAll(cartItems);

        return OrderResponse.builder()
                .fullname(saveOrder.getFullname())
                .phoneNumber(saveOrder.getPhoneNumber())
                .deliveryAddress(saveOrder.getShippingAddress())
                .orderId(order.getId())
                .shippingFee(saveOrder.getShippingFee())
                .totalMoney(saveOrder.getTotalMoney())
                .status(saveOrder.getStatus())
                .message("Đặt hàng thành công")
                .items(orderItemDetailResponseList)
                .build();
    }

}
