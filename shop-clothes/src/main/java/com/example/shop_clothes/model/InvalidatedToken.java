package com.example.shop_clothes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "invalidated_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidatedToken extends BaseEntity {

    @Column(name = "jti", unique = true, nullable = false)
    private String jti;

    private Date expiryTime;
}