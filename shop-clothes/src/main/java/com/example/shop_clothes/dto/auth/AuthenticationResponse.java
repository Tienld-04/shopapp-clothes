package com.example.shop_clothes.dto.auth;


import com.example.shop_clothes.dto.user.UserResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    String token;
    boolean authenticated;
    UserResponse user;

}
