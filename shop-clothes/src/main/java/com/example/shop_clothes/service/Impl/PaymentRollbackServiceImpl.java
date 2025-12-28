package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.model.Order;
import com.example.shop_clothes.model.OrderPaymentSnapshot;
import com.example.shop_clothes.model.ProductDetail;
import com.example.shop_clothes.repository.CartItemRepository;
import com.example.shop_clothes.repository.OrderPaymentSnapshotRepository;
import com.example.shop_clothes.repository.OrderRepository;
import com.example.shop_clothes.repository.ProductDetailRepository;
import com.example.shop_clothes.service.PaymentRollbackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRollbackServiceImpl implements PaymentRollbackService {
    private final OrderPaymentSnapshotRepository snapshotRepository;
    private final ProductDetailRepository productDetailRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void rollbackPayment(Order order, String reason) {
        try {
            List<OrderPaymentSnapshot> snapshots = snapshotRepository.findByOrderId(order.getId());
            for (OrderPaymentSnapshot snapshot : snapshots) {
                ProductDetail productDetail = snapshot.getProductDetail();
                // update the initial quantity
                Integer restoredQuantity = productDetail.getQuantity() + snapshot.getQuantityReserved();
                productDetail.setQuantity(restoredQuantity);
                productDetailRepository.save(productDetail);
            }
            // delete snapshots
            snapshotRepository.deleteAll(snapshots);
            // update order status
            order.setStatus(com.example.shop_clothes.enums.OrderStatus.PAYMENT_FAILED);
            order.setRollbackReason(reason);
            orderRepository.save(order);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.ORDER_ROLLBACK_FAILED);
        }
    }
    @Transactional
    @Override
    public void cancelOrderByTimeout(Order order) {
        rollbackPayment(order, "Quá hạn thanh toán");
    }
}
