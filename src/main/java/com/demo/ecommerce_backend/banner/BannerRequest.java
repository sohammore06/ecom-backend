package com.demo.ecommerce_backend.banner;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerRequest {
    private String title;
    private MultipartFile imageFile;
    private boolean active;
    private int sortOrder;
    private String redirectUrl;
}
