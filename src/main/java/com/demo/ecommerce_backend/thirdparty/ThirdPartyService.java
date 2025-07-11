package com.demo.ecommerce_backend.thirdparty;

import com.demo.ecommerce_backend.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThirdPartyService {

    private final ThirdPartyRepository thirdPartyRepository;
    private final ObjectMapper objectMapper; // for converting Map to JSON

    public ApiResponse<ThirdPartyResponse> addThirdParty(ThirdPartyRequest request) {
        if (thirdPartyRepository.existsByNameIgnoreCase(request.getName())) {
            return new ApiResponse<>(false, "Third party with this name already exists", null);
        }

        String metadataJson;
        try {
            metadataJson = objectMapper.writeValueAsString(request.getMetadata());
        } catch (Exception e) {
            return new ApiResponse<>(false, "Invalid metadata format", null);
        }

        ThirdParty thirdParty = ThirdParty.builder()
                .name(request.getName())
                .active(request.isActive())
                .metadata(metadataJson)
                .build();

        ThirdParty saved = thirdPartyRepository.save(thirdParty);

        ThirdPartyResponse response = ThirdPartyResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .active(saved.isActive())
                .metadata(saved.getMetadata())
                .build();

        return new ApiResponse<>(true, "Third party added", response);
    }
}
