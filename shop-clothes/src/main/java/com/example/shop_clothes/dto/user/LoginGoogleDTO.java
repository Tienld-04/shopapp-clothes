package com.example.shop_clothes.dto.user;


import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginGoogleDTO {
    private String fullName;
    private String email;
    private String avatarUrl;
    private String providerId;
}
