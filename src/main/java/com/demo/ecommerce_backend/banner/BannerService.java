package com.demo.ecommerce_backend.banner;

import com.demo.ecommerce_backend.exception.ResourceNotFoundException;
import com.demo.ecommerce_backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerService.class);
    private final BannerRepository bannerRepository;
    private final FileUploadUtil fileUploadUtil;

    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BannerResponse addBanner(BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setActive(request.isActive());
        banner.setSortOrder(request.getSortOrder());
        banner.setRedirectUrl(request.getRedirectUrl());

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                String imageUrl = fileUploadUtil.saveImage(request.getImageFile(), "banners");
                banner.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload banner image", e);
            }
        }

        Banner saved = bannerRepository.save(banner);
        return mapToResponse(saved);
    }

    public BannerResponse updateBanner(Integer id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with ID: " + id));

        banner.setTitle(request.getTitle());
        banner.setActive(request.isActive());
        banner.setSortOrder(request.getSortOrder());
        banner.setRedirectUrl(request.getRedirectUrl());

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                log.info("Updating banner image");
                String imageUrl = fileUploadUtil.saveImage(request.getImageFile(), "banners");
                banner.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload banner image", e);
            }
        }

        Banner updated = bannerRepository.save(banner);
        return mapToResponse(updated);
    }

    public void deleteBanner(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with ID: " + id));
        bannerRepository.delete(banner);
    }

    private BannerResponse mapToResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .active(banner.isActive())
                .sortOrder(banner.getSortOrder())
                .redirectUrl(banner.getRedirectUrl())
                .imageUrl(banner.getImageUrl())
                .build();
    }
}
