package com.example.shop_clothes.Components;

import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.model.Order;
import com.example.shop_clothes.repository.OrderRepository;
import com.example.shop_clothes.service.PaymentRollbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentTimeoutScheduler {
    private final OrderRepository orderRepository;
    private final PaymentRollbackService paymentRollbackService;

    /**
     * Run every 5 minutes to check for order timeouts.
     */
    @Scheduled(fixedDelay = 300000)
    public void cancelExpiredOrders() {
        try {
            log.info("Checking for expired payment orders...");
            List<Order> expiredOrders = orderRepository.findExpiredOrders(
                    OrderStatus.PAYMENT_WAITING,
                    LocalDateTime.now()
            );
            for (Order order : expiredOrders) {
                log.warn("Order {} payment expired", order.getId());
                paymentRollbackService.cancelOrderByTimeout(order);
            }
            if (!expiredOrders.isEmpty()) {
                log.info("Cancelled {} expired orders", expiredOrders.size());
            }
        } catch (Exception e) {
            log.error("Error in payment timeout scheduler", e);
        }
    }
}
