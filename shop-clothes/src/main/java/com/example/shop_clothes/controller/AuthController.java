package com.example.shop_clothes.controller;

import com.example.shop_clothes.dto.auth.AuthenticationResponse;
import com.example.shop_clothes.dto.auth.LoginRequest;
import com.example.shop_clothes.dto.auth.RefreshTokenRequest;
import com.example.shop_clothes.dto.user.LoginGoogleDTO;
import com.example.shop_clothes.dto.user.UserCreateRequest;
import com.example.shop_clothes.dto.user.UserResponse;
import com.example.shop_clothes.security.JwtService;
import com.example.shop_clothes.service.AuthService;
import com.example.shop_clothes.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return ResponseEntity.ok(userService.createUser(userCreateRequest));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logout successful");
    }
    @GetMapping("/login/google")
    public ResponseEntity<AuthenticationResponse> loginGoogle(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String sub = (String) attributes.get("sub");
        LoginGoogleDTO loginGoogleDTO = new LoginGoogleDTO();
        loginGoogleDTO.setEmail(email);
        loginGoogleDTO.setFullName(name);
        loginGoogleDTO.setAvatarUrl(picture);
        loginGoogleDTO.setProviderId(sub);
        userService.createUserWithGoogle(loginGoogleDTO);
        return ResponseEntity.ok(authService.loginWithGoogle(loginGoogleDTO));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws ParseException, JOSEException {
        var res = jwtService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(res);
    }
}
