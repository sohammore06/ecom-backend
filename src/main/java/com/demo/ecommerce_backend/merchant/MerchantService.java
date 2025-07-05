package com.demo.ecommerce_backend.merchant;

import com.demo.ecommerce_backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public ApiResponse<MerchantResponse> getActiveMerchant() {
        Merchant merchant = merchantRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active merchant configured"));

        MerchantResponse response = MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .upiId(merchant.getUpiId())
                .paymentCreateUrl(merchant.getPaymentCreateUrl())
                .paymentStatusUrl(merchant.getPaymentStatusUrl())
                .redirectUrl(merchant.getRedirectUrl())
                .active(merchant.isActive())
                .build();

        return new ApiResponse<>(true, "Active merchant fetched successfully", response);
    }
    public void activateMerchant(Integer merchantId) {
        // Deactivate all merchants
        merchantRepository.deactivateAll();

        // Activate selected merchant
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.setActive(true);
        merchantRepository.save(merchant);
    }

    public ApiResponse<MerchantResponse> createMerchant(MerchantRequest request) {
        Merchant merchant = Merchant.builder()
                .name(request.getName())
                .upiId(request.getUpiId())
                .paymentCreateUrl(request.getPaymentCreateUrl())
                .paymentStatusUrl(request.getPaymentStatusUrl())
                .redirectUrl(request.getRedirectUrl())
                .apiKey(request.getApiKey())
                .apiSecret(request.getApiSecret())
                .active(false) // default to inactive; admin can activate separately
                .build();

        merchant = merchantRepository.save(merchant);

        MerchantResponse response = MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .upiId(merchant.getUpiId())
                .paymentCreateUrl(merchant.getPaymentCreateUrl())
                .paymentStatusUrl(merchant.getPaymentStatusUrl())
                .redirectUrl(merchant.getRedirectUrl())
                .active(merchant.isActive())
                .build();

        return new ApiResponse<>(true, "Merchant created successfully", response);
    }
    public ApiResponse<List<MerchantResponse>> getAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();

        List<MerchantResponse> responseList = merchants.stream()
                .map(merchant -> MerchantResponse.builder()
                        .id(merchant.getId())
                        .name(merchant.getName())
                        .upiId(merchant.getUpiId())
                        .paymentCreateUrl(merchant.getPaymentCreateUrl())
                        .paymentStatusUrl(merchant.getPaymentStatusUrl())
                        .redirectUrl(merchant.getRedirectUrl())
                        .active(merchant.isActive())
                        .build()
                ).toList();

        return new ApiResponse<>(true, "Merchants fetched successfully", responseList);
    }

}
