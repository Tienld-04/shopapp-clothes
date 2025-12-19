package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.dto.auth.AuthenticationResponse;
import com.example.shop_clothes.dto.auth.LoginRequest;
import com.example.shop_clothes.dto.user.LoginGoogleDTO;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.mapper.UserMapper;
import com.example.shop_clothes.model.InvalidatedToken;
import com.example.shop_clothes.model.User;
import com.example.shop_clothes.repository.InvalidatedTokenRepository;
import com.example.shop_clothes.repository.UserRepository;
import com.example.shop_clothes.security.JwtService;
import com.example.shop_clothes.service.AuthService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserMapper userMapper;

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new ApplicationException(ErrorCode.EMAIL_NOT_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new ApplicationException(ErrorCode.PASSWORD_INVALID);
        }
        String token = jwtService.genToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    public AuthenticationResponse loginWithGoogle(LoginGoogleDTO loginGoogleDTO) {
        User user = userRepository.findByEmail(loginGoogleDTO.getEmail());
        if (user == null) {
            throw new ApplicationException(ErrorCode.EMAIL_NOT_EXISTED);
        }
        String token = jwtService.genToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        String token = authHeader.substring(7);
        try {
            SignedJWT signedJWT = jwtService.verifyToken(token, false);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .jti(jti)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
            log.info("Token with jti={} invalidated until {}", jti, expiryTime);
        } catch (ParseException | JOSEException e) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
    }

}
