package com.smart.drop.iam.infrastructure.security.jwt;

import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.interfaces.rest.dto.ValidateTokenResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Servicio de infraestructura para generación, firma HMAC-SHA256 y validación/introspección de tokens JWT.
 */
@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtTokenService(
            @Value("${authorization.jwt.secret:WriteHereYourSecretStringForTokenSigningCredentialsSecureKey2026SmartDrop}") String secret,
            @Value("${authorization.jwt.expiration.days:7}") int expirationDays
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = (long) expirationDays * 24L * 60 * 60 * 1000;
    }

    /**
     * Genera un token JWT firmado para el usuario con claims de roles y datos de perfil.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        List<String> roleNames = user.getRoles() == null ? List.of() :
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("fullName", user.getFullName())
                .claim("roles", roleNames)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Valida e introspecciona un token JWT extrayendo claims del usuario.
     */
    public ValidateTokenResponseDto validateToken(String token) {
        if (token == null || token.isBlank()) {
            return new ValidateTokenResponseDto(false, null, List.of(), null, "Token cannot be empty");
        }

        try {
            // Eliminar prefijo Bearer si viene incluido
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();

            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(cleanToken)
                    .getPayload();

            String username = claims.getSubject();
            Number userIdNum = claims.get("userId", Number.class);
            Integer userId = userIdNum != null ? userIdNum.intValue() : null;

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                roles = List.of();
            }

            return new ValidateTokenResponseDto(true, username, roles, userId, "Token is valid");
        } catch (JwtException | IllegalArgumentException e) {
            return new ValidateTokenResponseDto(false, null, List.of(), null, "Invalid or expired token: " + e.getMessage());
        }
    }
}
