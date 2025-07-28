package com.demo.ecommerce_backend.banner;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banners")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String imageUrl; // Relative or absolute image path

    private boolean active;

    private int sortOrder; // for controlling slideshow order

    private String redirectUrl; // optional: where it redirects on click

    // getters & setters
}

