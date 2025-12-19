package com.example.shop_clothes.config;

import com.example.shop_clothes.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@RequiredArgsConstructor
public class InvalidatedJwtValidator implements OAuth2TokenValidator<Jwt> {
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String jti = jwt.getId();
        if (jti != null && invalidatedTokenRepository.existsByJti(jti)) {
            OAuth2Error error = new OAuth2Error("invalid_token", "Token has been invalidated", null);
            return OAuth2TokenValidatorResult.failure(error);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
