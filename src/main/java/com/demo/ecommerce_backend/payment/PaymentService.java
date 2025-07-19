package com.demo.ecommerce_backend.payment;
import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.gateway.GatewayStatusResponse;
import com.demo.ecommerce_backend.gateway.mobalegends.MobalegendsGatewayClient;
import com.demo.ecommerce_backend.gateway.mobalegends.MobalegendsGatewayRequest;
import com.demo.ecommerce_backend.gateway.upigateway.UpiGatewayClient;
import com.demo.ecommerce_backend.gateway.upigateway.UpiGatewayRequest;
import com.demo.ecommerce_backend.merchant.Merchant;
import com.demo.ecommerce_backend.merchant.MerchantRepository;
import com.demo.ecommerce_backend.order.Order;
import com.demo.ecommerce_backend.order.OrderRepository;
import com.demo.ecommerce_backend.order.OrderStatus;
import com.demo.ecommerce_backend.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import static com.demo.ecommerce_backend.util.JsonUtil.toJson;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserReposirtory userRepository;
    private final OrderRepository orderRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentRepository paymentRepository;
    private final MobalegendsGatewayClient mobalegendsGatewayClient;
    private final UpiGatewayClient upiGatewayClient;
    private final ObjectMapper objectMapper;
    public ApiResponse<PaymentResponse> createPayment(PaymentRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Order order = null;
            if (request.getOrderId() != null) {
                order = orderRepository.findById(request.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            }

            Merchant merchant = merchantRepository.findByActiveTrue()
                    .orElseThrow(() -> new ResourceNotFoundException("No active merchant configured"));

            if ("mobalegends".equalsIgnoreCase(merchant.getName())) {
                return processMobalegendsPayment(user, order, request, merchant);
            } else if ("upigateway".equalsIgnoreCase(merchant.getName())) {
                return processUpiGatewayPayment(user,order,request,merchant);
            }

            throw new UnsupportedOperationException("Unsupported merchant: " + merchant.getName());

        } catch (ResourceNotFoundException | UnsupportedOperationException ex) {
            // Expected, business-related exceptions
            return new ApiResponse<>(false, ex.getMessage(), null);
        } catch (Exception e) {
            // Unexpected issues (e.g., HTTP failure, conversion error)
            return new ApiResponse<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    public ApiResponse<GatewayStatusResponse> checkAndProcessPaymentStatus(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        Merchant merchant = payment.getMerchant();

        // 1. Call appropriate client to fetch status
        GatewayStatusResponse statusResponse;
        if ("mobalegends".equalsIgnoreCase(merchant.getName())) {
            statusResponse = mobalegendsGatewayClient.checkStatus(merchant,transactionId);
        } else if ("upigateway".equalsIgnoreCase(merchant.getName())) {
            statusResponse = upiGatewayClient.checkStatus(merchant,transactionId);
        } else {
            throw new UnsupportedOperationException("Unsupported merchant: " + merchant.getName());
        }

        // 2. Update payment status if changed
        String updatedStatus = statusResponse.getStatus();
        if (!payment.getStatus().equals(updatedStatus)) {
            payment.setStatus(updatedStatus);
            paymentRepository.save(payment);
        }

        // 3. If status is SUCCESS, trigger fulfillment logic (ONCE ONLY)
        if ("SUCCESS".equalsIgnoreCase(updatedStatus) && payment.getPaymentType() == PaymentType.ORDER) {
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            if (!order.isFulfilled()) {
//                fulfillmentService.processOrder(order); // 🟢 Your product API logic
//                order.setFulfilled(true); // Prevent re-trigger
//                orderRepository.save(order);
            }
            orderRepository.save(order);
        }
        return new ApiResponse<>(true, "Payment status checked", statusResponse);
    }

    private ApiResponse<PaymentResponse> processMobalegendsPayment(User user, Order order,
                                                                   PaymentRequest request,
                                                                   Merchant merchant) {

        String txnId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        MobalegendsGatewayRequest gatewayRequest = MobalegendsGatewayRequest.builder()
                .amount(request.getAmount())
                .merchantName("GameHub") // or get from env
                .upiId(merchant.getUpiId())
                .customerName(user.getFirstName() + " " + user.getLastName())
                .customerEmail(user.getEmail())
                .customerMobile(user.getPhoneNo())
                .redirectUrl(merchant.getRedirectUrl())
                .pInfo(request.getPaymentType() + " #" + txnId)
                .udf1("userId:" + user.getId())
                .udf2(request.getPaymentType().name())
                .udf3(String.valueOf(request.getOrderId()))
                .build();
        Map<String, Object> responseMap = mobalegendsGatewayClient.initiatePayment(merchant, gatewayRequest);
        System.out.println("here is your response"+responseMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

        String paymentUrl = (String) data.get("paymentUrl");
        String status = (String) data.get("status");
        String transactionId = (String) data.get("transactionId");


        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .merchant(merchant)
                .transactionId(transactionId)
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .paymentMode(request.getPaymentMode())
                .paymentUrl(paymentUrl)
                .status(status)
                .requestPayload(toJson(gatewayRequest))
                .responsePayload(toJson(responseMap))
                .build();


        paymentRepository.save(payment);
        System.out.println("paymentUrl: " + paymentUrl);
        System.out.println("transactionId: " + transactionId);
        System.out.println("status: " + status);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .transactionId(transactionId)
                .paymentUrl(paymentUrl)
                .amount(request.getAmount())
                .status(status)
                .paymentType(request.getPaymentType())
                .paymentMode(PaymentMode.UPI)
                .build();

        return new ApiResponse<>(true, "Payment initiated with Mobalegends", paymentResponse);
    }
    private ApiResponse<PaymentResponse> processUpiGatewayPayment(User user, Order order,
                                                                   PaymentRequest request,
                                                                   Merchant merchant) {

        String txnId = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        UpiGatewayRequest gatewayRequest = UpiGatewayRequest.builder()
                .amount(request.getAmount().toPlainString())
                .clientTxnId(txnId)
                .customerName(user.getFirstName() + " " + user.getLastName())
                .customerEmail(user.getEmail())
                .customerMobile(user.getPhoneNo())
                .redirectUrl(merchant.getRedirectUrl())
                .pInfo(request.getPaymentType() + " #" + txnId)
                .udf1("userId:" + user.getId())
                .udf2(request.getPaymentType().name())
                .udf3(String.valueOf(request.getOrderId()))
                .build();
        System.out.println("Payload: " + gatewayRequest);
        Map<String, Object> responseMap = upiGatewayClient.initiatePayment(merchant, gatewayRequest);
        System.out.println("here is your response"+responseMap);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

        String paymentUrl = (String) data.get("payment_url");
        String status = (String) data.get("status");


        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .merchant(merchant)
                .transactionId(txnId)
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .paymentMode(request.getPaymentMode())
                .paymentUrl(paymentUrl)
                .status(status)
                .requestPayload(toJson(gatewayRequest))
                .responsePayload(toJson(responseMap))
                .build();


        paymentRepository.save(payment);
        System.out.println("paymentUrl: " + paymentUrl);
        System.out.println("transactionId: " + txnId);
        System.out.println("status: " + status);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .transactionId(txnId)
                .paymentUrl(paymentUrl)
                .amount(request.getAmount())
                .status(status)
                .paymentType(request.getPaymentType())
                .paymentMode(PaymentMode.UPI)
                .build();

        return new ApiResponse<>(true, "Payment initiated with Mobalegends", paymentResponse);
    }
    public ApiResponse<List<PaymentResponse>> getAllPayments(int page, int size) {
        int safePage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(safePage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentRepository.findAll(pageable);

        // Only take the list of responses
        List<PaymentResponse> responseList = payments.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(true, "All payments fetched", responseList);
    }

    public ApiResponse<List<PaymentResponse>> getPaymentsByUserId(Long userId, int page, int size) {
        int safePage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(safePage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);

        List<PaymentResponse> responseList = payments.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new ApiResponse<>(true, "Payments fetched for user ID: " + userId, responseList);
    }




    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentUrl(payment.getPaymentUrl())
                .paymentMode(payment.getPaymentMode())
                .paymentType(payment.getPaymentType())
                .userId(payment.getUser().getId())
                .build();
    }

}
