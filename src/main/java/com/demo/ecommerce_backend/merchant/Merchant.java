package com.demo.ecommerce_backend.merchant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Mobalegends", "Razorpay"

    @Column(nullable = false)
    private String upiId;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String paymentCreateUrl;
    @Column
    private String paymentStatusUrl;

    @Column(nullable = false)
    private String redirectUrl;

    @Column // nullable = true by default
    private String apiKey;


    @Column
    private String apiSecret;
}
