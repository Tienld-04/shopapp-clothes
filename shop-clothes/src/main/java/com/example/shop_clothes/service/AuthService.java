package com.example.shop_clothes.service;

import com.example.shop_clothes.dto.auth.AuthenticationResponse;
import com.example.shop_clothes.dto.auth.LoginRequest;
import com.example.shop_clothes.dto.user.LoginGoogleDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthenticationResponse login(LoginRequest loginRequest);
    AuthenticationResponse loginWithGoogle(LoginGoogleDTO loginGoogleDTO);
    void logout(HttpServletRequest request);
}
