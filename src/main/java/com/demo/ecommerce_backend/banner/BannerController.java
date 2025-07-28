package com.demo.ecommerce_backend.banner;

import com.demo.ecommerce_backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAll() {
        List<BannerResponse> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched all banners", banners));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BannerResponse>> create(@ModelAttribute @Valid BannerRequest request) {
        BannerResponse created = bannerService.addBanner(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Banner created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> update(
            @PathVariable Integer id,
            @ModelAttribute @Valid BannerRequest request) {
        BannerResponse updated = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Banner updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Banner deleted successfully", null));
    }
}
