package com.demo.ecommerce_backend.orderItem;
import com.demo.ecommerce_backend.orderItem.OrderItemStatusResponse;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    // In OrderItemController.java
    @GetMapping("/status/{orderItemId}")
    public ResponseEntity<ApiResponse<OrderItemStatusResponse>> getStatus(
            @PathVariable Integer orderItemId) {
        OrderItemStatusResponse response = orderItemService.checkOrderStatus(orderItemId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Status fetched", response));
    }

}
