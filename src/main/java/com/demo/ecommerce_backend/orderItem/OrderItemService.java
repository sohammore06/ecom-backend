package com.demo.ecommerce_backend.orderItem;

import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.moogold.MoogoldTpClient;
import com.demo.ecommerce_backend.product.Product;
import com.demo.ecommerce_backend.product.ProductSource;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderItemService {
    // In OrderItemService.java
    private  OrderItemRepository orderItemRepository;
    private  MoogoldTpClient moogoldTpClient;
    public OrderItemStatusResponse checkOrderStatus(Integer orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        Product product = item.getProduct();
        ProductSource source = product.getSource();
        String partnerOrderId = "MG-" + item.getOrder() + "-" + item.getId();
        switch (source) {
            case MOOGOLD -> {
                return checkMoogoldOrderStatus(partnerOrderId,item);
            }
            // You can add more cases like SMILEONE later
            default -> throw new UnsupportedOperationException("Status check not supported for source: " + source);
        }
    }

    private OrderItemStatusResponse checkMoogoldOrderStatus(String partnerOrderId,OrderItem item) {
        if (partnerOrderId == null) {
            throw new RuntimeException("MooGold partnerOrderId not found in order item");
        }

        JsonNode response = moogoldTpClient.checkStatusByPartnerOrderId(partnerOrderId);

        if (response == null || !response.has("order_id")) {
            throw new RuntimeException("Invalid response from MooGold");
        }

        List<String> voucherCodes = new ArrayList<>();
        JsonNode itemArray = response.path("item");
        if (itemArray.isArray()) {
            for (JsonNode node : itemArray) {
                JsonNode codes = node.path("voucher_code");
                if (codes.isArray()) {
                    for (JsonNode code : codes) {
                        voucherCodes.add(code.asText());
                    }
                }
            }
        }

        // Update DB if order is completed
        String status = response.path("order_status").asText();
        if ("completed".equalsIgnoreCase(status)) {
            item.setFulfillmentStatus("SUCCESS");
            item.setDelivered(true);
            item.setDeliveryMetadata(response.toString());
            item.setFulfillmentError(null);
            orderItemRepository.save(item);
        }

        return OrderItemStatusResponse.builder()
                .orderId(response.path("order_id").asText())
                .partnerOrderId(partnerOrderId)
                .orderStatus(status)
                .dateCreated(response.path("date_created").path("date").asText(""))
                .voucherCodes(voucherCodes)
                .build();
    }

}
