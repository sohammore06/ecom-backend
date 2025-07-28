package com.demo.ecommerce_backend.banner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerResponse {
    private Integer id;
    private String title;
    private String imageUrl;
    private boolean active;
    private int sortOrder;
    private String redirectUrl;
}
