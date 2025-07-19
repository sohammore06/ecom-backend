package com.demo.ecommerce_backend.gateway.mobalegends;
import com.demo.ecommerce_backend.payment.PaymentService;
import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/response/mobalegends")
@RequiredArgsConstructor
public class MobalegendsController {
    private final MobalegendsService mobalegendsService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> receiveWebhook(@RequestBody MobalegendsWebhookRequest request) {
       mobalegendsService.processWebhook(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Webhook received", null));
    }
}
