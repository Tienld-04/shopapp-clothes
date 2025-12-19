package com.example.shop_clothes.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false, length = 20, unique = true)
    private String name;

    @Column(name = "description", nullable = false, length = 20)
    private String description;

}