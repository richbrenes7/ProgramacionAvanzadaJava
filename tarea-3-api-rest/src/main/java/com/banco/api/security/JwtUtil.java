package com.banco.api.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final byte[] secretBytes;
    private final long expirationMinutes;

    public JwtUtil(String secret, long expirationMinutes) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String subject) {
        try {
            long now = Instant.now().getEpochSecond();
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("sub", subject);
            claims.put("iat", now);
            claims.put("exp", now + expirationMinutes * 60);

            String header = base64Url(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
            String payload = base64Url(objectMapper.writeValueAsBytes(claims));
            String signature = sign(header + "." + payload);
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el token", e);
        }
    }

    public String validateAndExtractSubject(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token vacío");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Formato de token inválido");
        }

        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Firma JWT inválida");
        }

        Map<String, Object> payload = decodePayload(parts[1]);
        Number exp = (Number) payload.get("exp");
        if (exp == null || Instant.now().getEpochSecond() >= exp.longValue()) {
            throw new IllegalArgumentException("Token expirado");
        }

        Object subject = payload.get("sub");
        if (subject == null) {
            throw new IllegalArgumentException("Subject ausente");
        }
        return subject.toString();
    }

    public String extractSubject(String token) {
        return validateAndExtractSubject(token);
    }

    private Map<String, Object> decodePayload(String base64Payload) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(base64Payload);
            return objectMapper.readValue(decoded, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload JWT inválido", e);
        }
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            return base64Url(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}