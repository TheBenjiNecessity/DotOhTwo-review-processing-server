package com.dotohtwo.review_processor.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

@Component
public class ServiceTokenProvider {

    private final JwtEncoder encoder;
    private volatile String cachedToken;
    private volatile Instant tokenExpiry = Instant.EPOCH;

    public ServiceTokenProvider(@Value("${jwt.secret}") String secret) {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    public synchronized String getToken() {
        if (Instant.now().isAfter(tokenExpiry.minusSeconds(30))) {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(300);
            JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject("review-processor")
                    .issuedAt(now)
                    .expiresAt(exp)
                    .claim("role", "service")
                    .build();
            cachedToken = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
            tokenExpiry = exp;
        }
        return cachedToken;
    }
}
