package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.User.User;
import com.demo.ecommerce_backend.User.UserReposirtory;
import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.orderItem.OrderItem;
import com.demo.ecommerce_backend.orderItem.OrderItemResponse;
import com.demo.ecommerce_backend.payment.*;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductRepository;
import com.demo.ecommerce_backend.util.ApiResponse;
import com.demo.ecommerce_backend.wallet.Wallet;
import com.demo.ecommerce_backend.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.demo.ecommerce_backend.orderItem.OrderItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserReposirtory userRepository;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public ApiResponse<OrderResponse> getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        OrderResponse response = OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .paidAmount(order.getPaidAmount())
                .status(order.getStatus())
                .paymentMode(order.getPaymentMode().name())
                .usedWallet(order.isUsedWallet())
                .createdAt(order.getCreatedAt())
                .items(items.stream().map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getPriceAtPurchase())
                        .quantity(item.getQuantity())
                        .delivered(item.isDelivered())
                        .build()).toList())
                .build();

        return new ApiResponse<>(true, "Order fetched successfully", response);
    }

    public ApiResponse<List<OrderResponse>> getOrdersByUser(Integer userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);

        List<OrderResponse> response = ordersPage.getContent().stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return OrderResponse.builder()
                    .orderId(order.getId())
                    .totalAmount(order.getTotalAmount())
                    .paidAmount(order.getPaidAmount())
                    .status(order.getStatus())
                    .paymentMode(order.getPaymentMode().name())
                    .usedWallet(order.isUsedWallet())
                    .createdAt(order.getCreatedAt())
                    .items(items.stream().map(item -> OrderItemResponse.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .price(item.getPriceAtPurchase())
                            .quantity(item.getQuantity())
                            .delivered(item.isDelivered())
                            .build()).toList())
                    .build();
        }).toList();

        return new ApiResponse<>(true, "User orders fetched (page " + page + ")", response);
    }
    public ApiResponse<List<OrderResponse>> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        List<OrderResponse> response = ordersPage.getContent().stream().map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return OrderResponse.builder()
                    .orderId(order.getId())
                    .userId(order.getUser().getId())
                    .totalAmount(order.getTotalAmount())
                    .paidAmount(order.getPaidAmount())
                    .status(order.getStatus())
                    .paymentMode(order.getPaymentMode().name())
                    .usedWallet(order.isUsedWallet())
                    .createdAt(order.getCreatedAt())
                    .items(items.stream().map(item -> OrderItemResponse.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .price(item.getPriceAtPurchase())
                            .quantity(item.getQuantity())
                            .delivered(item.isDelivered())
                            .build()).toList())
                    .build();
        }).toList();

        return new ApiResponse<>(true, "Orders fetched (page " + page + ")", response);
    }



    public ApiResponse<OrderResponse> placeOrder(Integer userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            return OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .priceAtPurchase(product.getDiscountedPrice())
                    .delivered(false)
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .paidAmount(total)
                .status(OrderStatus.PENDING)
                .paymentMode(request.getPaymentMode())
                .usedWallet(request.isUseWallet())
                .build();
        order = orderRepository.save(order);

        for (OrderItem item : items) item.setOrder(order);
        orderItemRepository.saveAll(items);

        ApiResponse<OrderResponse> response;

        if (request.getPaymentMode() == PaymentMode.WALLET) {
            response = handleWalletPayment(user, order, total, items);
        } else {
            response = handleOnlinePayment(user, order, total, items, request);
        }

        return response;
    }
    private ApiResponse<OrderResponse> handleWalletPayment(User user, Order order, BigDecimal amount, List<OrderItem> items) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            return new ApiResponse<>(false, "Not enough wallet balance", null);
        }

        // Deduct from wallet
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Record transaction
//        WalletTransaction txn = WalletTransaction.builder()
//                .user(user)
//                .amount(amount)
//                .type("DEBIT")
//                .purpose("ORDER")
//                .referenceId(order.getId().toString())
//                .build();
//        walletTransactionRepository.save(txn);

        // Create payment entry
        String txnId ="TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        Payment payment = Payment.builder()
                .user(user)
                .order(order)
                .merchant(null)
                .transactionId(txnId)
                .amount(amount)
                .paymentType(PaymentType.ORDER)
                .paymentMode(PaymentMode.WALLET)
                .status("SUCCESS")
                .requestPayload("WALLET_DEBIT")
                .responsePayload("WALLET_DEBIT_SUCCESS")
                .build();
        paymentRepository.save(payment);

        // Fulfill order immediately
        order.setStatus(OrderStatus.PAID);
//        order.setFulfilled(true); //need to call smile one & moogold to fulfil
        orderRepository.save(order);

        OrderResponse response = buildOrderResponse(order, items, null);
        return new ApiResponse<>(true, "Order placed using wallet", response);
    }
    private ApiResponse<OrderResponse> handleOnlinePayment(User user, Order order, BigDecimal amount, List<OrderItem> items, OrderRequest request) {
        String paymentUrl = null;
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setUserId(user.getId());
            paymentRequest.setOrderId(order.getId());
            paymentRequest.setAmount(amount);
            paymentRequest.setPaymentType(PaymentType.ORDER);
            paymentRequest.setPaymentMode(request.isUseWallet() ? PaymentMode.WALLET : PaymentMode.UPI);

            ApiResponse<PaymentResponse> paymentResponse = paymentService.createPayment(paymentRequest);
            if (paymentResponse.getData() != null) {
                paymentUrl = paymentResponse.getData().getPaymentUrl();
            }

        } catch (Exception e) {
            return new ApiResponse<>(false, "Payment initiation failed: " + e.getMessage(), null);
        }

        OrderResponse response = buildOrderResponse(order, items, paymentUrl);
        return new ApiResponse<>(true, "Order placed, complete payment", response);
    }
    private OrderResponse buildOrderResponse(Order order, List<OrderItem> items, String paymentUrl) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .paidAmount(order.getPaidAmount())
                .status(order.getStatus())
                .paymentMode(order.getPaymentMode().name())
                .usedWallet(order.isUsedWallet())
                .createdAt(order.getCreatedAt())
                .paymentUrl(paymentUrl)
                .items(items.stream().map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getPriceAtPurchase())
                        .quantity(item.getQuantity())
                        .delivered(item.isDelivered())
                        .build()).toList())
                .build();
    }

}
