package com.example.shop_clothes.security;

import com.example.shop_clothes.dto.auth.AuthenticationResponse;
import com.example.shop_clothes.dto.auth.RefreshTokenRequest;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.mapper.UserMapper;
import com.example.shop_clothes.model.InvalidatedToken;
import com.example.shop_clothes.model.Role;
import com.example.shop_clothes.model.User;
import com.example.shop_clothes.repository.InvalidatedTokenRepository;
import com.example.shop_clothes.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String Signer_Key;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long Valid_Duration;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long Refresh_Duration;

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final UserRepository userRepository;
    private final UserMapper  userMapper;
    public String genToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("Tienld.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + Valid_Duration * 1000L)) // 1 ngày
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(Signer_Key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWS object", e);
            throw new RuntimeException(e);
        }
    }
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Set<Role>  roles = user.getRoles();
        if(!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }
        return stringJoiner.toString();
    }
    public SignedJWT verifyToken(String token, Boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(Signer_Key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime;
        if (isRefresh) {  // Nếu là Refresh Token
            expiryTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                    .plus(Refresh_Duration, ChronoUnit.SECONDS).toEpochMilli());
        } else {  // Access Token
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        }
        boolean verified = signedJWT.verify(verifier); // check chữ ký
        if (!(verified && expiryTime.after(new Date()))) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        if (invalidatedTokenRepository.existsByJti(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws ParseException, JOSEException {
        var signJWT = verifyToken(refreshTokenRequest.getToken(), true);
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiTime = signJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .jti(jit)
                .expiryTime(expiTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var email = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByEmail(email);
        if(user == null) {
            throw new ApplicationException(ErrorCode.USER_NOT_EXISTED);
        }
        var token = genToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .user(userMapper.toUserResponse(user))
                .build();
    }
}
