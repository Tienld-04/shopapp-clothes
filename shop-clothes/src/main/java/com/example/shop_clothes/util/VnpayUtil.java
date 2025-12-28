package com.example.shop_clothes.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
public class VnpayUtil {
    /**
     * Tạo SecureHash cho VNPAY sử dụng HmacSHA512
     * @param params Map chứa tất cả parameters (đã sorted)
     * @param secretKey Secret key từ Vnpay
     * @return Secure hash string (hex format)
     */
    public static String generateSecureHash(Map<String, String> params, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        StringBuilder hashData = new StringBuilder();
        Set<String> keys = params.keySet();
        Iterator<String> itr = keys.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = params.get(key);
            if ((value != null) && (value.length() > 0)) {
                hashData.append(key);
                hashData.append("=");
                hashData.append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }
        log.debug("Hash data string: {}", hashData.toString());
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                0,
                secretKey.length(),
                "HmacSHA512"
        );
        hmac512.init(secretKeySpec);
        byte[] message = hmac512.doFinal(hashData.toString().getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : message) {
            result.append(String.format("%02x", b));
        }
        log.debug("Generated secure hash: {}", result.toString());
        return result.toString();
    }

    /**
     * Xây dựng payment URL hoàn chỉnh
     */
    public static String buildPaymentUrl(String payUrl, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder(payUrl);
        url.append("?");
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!isFirst) {
                url.append("&");
            }
            url.append(entry.getKey());
            url.append("=");
            url.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            isFirst = false;
        }
        log.debug("Built payment URL with {} parameters", params.size());
        return url.toString();
    }

    /**
     * Lấy IP address của client từ HTTP request
     * Có xử lý proxy (X-Forwarded-For header)
     */
    public static String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
