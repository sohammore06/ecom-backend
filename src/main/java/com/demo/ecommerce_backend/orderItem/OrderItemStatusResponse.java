package com.demo.ecommerce_backend.orderItem;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderItemStatusResponse {
    private String orderId;
    private String partnerOrderId;
    private String orderStatus;
    private String dateCreated;
    private List<String> voucherCodes;
}
