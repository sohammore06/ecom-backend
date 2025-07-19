package com.demo.ecommerce_backend.thirdparty;

import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/third-party")
@RequiredArgsConstructor
public class ThirdPartyController {

    private final ThirdPartyService thirdPartyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ThirdPartyResponse>> addThirdParty(@RequestBody ThirdPartyRequest request) {
        return ResponseEntity.ok(thirdPartyService.addThirdParty(request));
    }
}
