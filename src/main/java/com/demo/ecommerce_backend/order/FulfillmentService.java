package com.demo.ecommerce_backend.order;

import com.demo.ecommerce_backend.moogold.MoogoldTpClient;
import com.demo.ecommerce_backend.orderItem.OrderItem;
import com.demo.ecommerce_backend.orderItem.OrderItemRepository;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductSource;
import com.demo.ecommerce_backend.smileone.SmileOneOrderRequest;
import com.demo.ecommerce_backend.smileone.SmileOneOrderResponse;
import com.demo.ecommerce_backend.smileone.SmileOneTpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FulfillmentService {
    private static final Logger log = LoggerFactory.getLogger(FulfillmentService.class);
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final SmileOneTpClient smileOneTpClient;
    private final MoogoldTpClient moogoldTpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Transactional
    public void processOrder(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        boolean allDelivered = true;

        for (OrderItem item : items) {
            Product product = item.getProduct();
            ProductSource source = product.getSource();

            try {
                switch (source) {
                    case INTERNAL -> {
                        item.setDelivered(true);
                        item.setFulfillmentStatus("SUCCESS");
                    }
                    case SMILEONE -> {
                        String gameUserId = item.getGameUserId();  // get from OrderItem, not user
                        String zoneId = item.getZoneId();
                        SmileOneOrderRequest request = SmileOneOrderRequest.builder()
                                .userId(gameUserId) // replace with correct user field
                                .zoneId(zoneId)     // optional if needed
                                .productId(product.getExternalProductId())
                                .productName(product.getName())
                                .build();

                        SmileOneOrderResponse response = smileOneTpClient.createOrder(request);
                        log.info("here is your response from smile one===>",response);
                        if (response != null && response.getStatus() == 200 && "success".equalsIgnoreCase(response.getMessage())) {
                            item.setDelivered(true);
                            String metadataJson = objectMapper.writeValueAsString(response);
                            item.setDeliveryMetadata(metadataJson);
                            item.setFulfillmentStatus("SUCCESS");
                            item.setFulfillmentError(null);
                        } else {
                            allDelivered = false;
                            item.setFulfillmentStatus("FAILED");
                            item.setFulfillmentError("SmileOne API failed: " + response);
                        }

                    }
                    case MOOGOLD -> {
                        System.out.println("we are inside create order of moogold");
                        String gameUserId = item.getGameUserId();
                        String zoneId = item.getZoneId(); // optional
                        String productId = product.getExternalProductId();
                        String categoryId = "1"; // mapped to MooGold's "category"

                        Map<String, String> data = Map.of(
                                "category", categoryId,
                                "product-id", productId,
                                "quantity", String.valueOf(item.getQuantity()),
                                "User ID", gameUserId,
                                "Server", zoneId // Only if required
                        );

                        String partnerOrderId = "MG-" + order.getId() + "-" + item.getId(); // optional but good for tracking
                        JsonNode moogoldResponse = moogoldTpClient.createOrder(data, partnerOrderId);

                        if (moogoldResponse.has("status") && "success".equalsIgnoreCase(moogoldResponse.get("status").asText())) {
                            item.setDelivered(true);
                            item.setFulfillmentStatus("SUCCESS");
                            item.setDeliveryMetadata(moogoldResponse.toString());
                        }
                        else if(moogoldResponse.has("status") && "processing".equalsIgnoreCase(moogoldResponse.get("status").asText())){
                            item.setDelivered(true);
                            item.setFulfillmentStatus("PROCESSING");
                            item.setDeliveryMetadata(moogoldResponse.toString());
                        }else {
                            allDelivered = false;
                            item.setFulfillmentStatus("FAILED");
                            item.setFulfillmentError("MooGold API failure: " + moogoldResponse.toString());
                        }
                        item.setExternalOrderId(partnerOrderId);
                    }
                    default -> {
                        allDelivered = false;
                        item.setFulfillmentStatus("UNKNOWN_SOURCE");
                        item.setFulfillmentError("Unknown product source");
                    }
                }

                orderItemRepository.save(item);

            } catch (Exception e) {
                allDelivered = false;
                item.setFulfillmentStatus("EXCEPTION");
                item.setFulfillmentError(e.getMessage());
                orderItemRepository.save(item);
                log.error("Fulfillment error for item {}: {}", item.getId(), e.getMessage());
            }
        }

        order.setFulfilled(allDelivered);
        orderRepository.save(order);
    }
}
