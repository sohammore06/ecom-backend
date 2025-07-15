// MobalegendsService.java
package com.demo.ecommerce_backend.gateway.mobalegends;

import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.order.FulfillmentService;
import com.demo.ecommerce_backend.order.Order;
import com.demo.ecommerce_backend.order.OrderRepository;
import com.demo.ecommerce_backend.order.OrderStatus;
import com.demo.ecommerce_backend.payment.Payment;
import com.demo.ecommerce_backend.payment.PaymentRepository;
import com.demo.ecommerce_backend.payment.PaymentType;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobalegendsService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final FulfillmentService fulfillmentService;

    @Async
    public void processWebhook(MobalegendsWebhookRequest request) {
        try {
            log.info("Input for webhook | ",request);
            String txnId = request.getTransactionId();
            String status = request.getStatus();

            Payment payment = paymentRepository.findByTransactionId(txnId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found for txnId: " + txnId));

            if (payment.getStatus().equalsIgnoreCase(status)) {
                log.info("🟡 Webhook duplicate - already in status: {}", status);
                return ;
            }

            payment.setStatus(status);
            paymentRepository.save(payment);

            if ("SUCCESS".equalsIgnoreCase(status) && payment.getPaymentType() == PaymentType.ORDER) {
                Order order = payment.getOrder();
                order.setStatus(OrderStatus.PAID);

                if (!order.isFulfilled()) {
                    fulfillmentService.processOrder(order);
                }

                orderRepository.save(order);
            }

            log.info("✅ Mobalegends webhook processed: txnId={}, status={}", txnId, status);


        } catch (Exception e) {
            log.error("❌ Mobalegends webhook error: {}", e.getMessage(), e);
        }
    }
}
