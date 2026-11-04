package com.smart.drop.iam.infrastructure.security.jwt;

import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.interfaces.rest.dto.ValidateTokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias - Generación y Validación de Tokens JWT (HMAC-SHA256)")
class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private static final String SECRET = "WriteHereYourSecretStringForTokenSigningCredentialsSecureKey2026SmartDrop";

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(SECRET, 7);
    }

    @Test
    @DisplayName("Debe generar token JWT válido con claims de usuario y roles RBAC")
    void shouldGenerateValidTokenWithClaims() {
        // Arrange
        Role userRole = Role.createFromDatabase(1, "ROLE_USER", "Standard user");
        Role adminRole = Role.createFromDatabase(2, "ROLE_ADMIN", "Administrator");
        User user = User.createFromDatabase(2, "Valeria Brewer", "valeria.brewer@artesanal.pe",
                "Hash", Set.of(userRole, adminRole), LocalDateTime.now(), null, false, null);

        // Act
        String token = jwtTokenService.generateToken(user);

        // Assert
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);

        ValidateTokenResponseDto validation = jwtTokenService.validateToken(token);
        assertThat(validation.valid()).isTrue();
        assertThat(validation.username()).isEqualTo("valeria.brewer@artesanal.pe");
        assertThat(validation.userId()).isEqualTo(2);
        assertThat(validation.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Debe aceptar y validar token con prefijo 'Bearer '")
    void shouldValidateTokenWithBearerPrefix() {
        // Arrange
        User user = User.createFromDatabase(10, "Carlos Mendoza", "carlos.mendoza@smartdrop.pe",
                "Hash", Set.of(), LocalDateTime.now(), null, false, null);
        String token = jwtTokenService.generateToken(user);

        // Act
        ValidateTokenResponseDto validation = jwtTokenService.validateToken("Bearer " + token);

        // Assert
        assertThat(validation.valid()).isTrue();
        assertThat(validation.username()).isEqualTo("carlos.mendoza@smartdrop.pe");
        assertThat(validation.userId()).isEqualTo(10);
    }

    @Test
    @DisplayName("Debe rechazar token inválido o manipulado")
    void shouldRejectInvalidToken() {
        // Act
        ValidateTokenResponseDto validation = jwtTokenService.validateToken("invalid.token.signature");

        // Assert
        assertThat(validation.valid()).isFalse();
        assertThat(validation.username()).isNull();
        assertThat(validation.message()).contains("Invalid or expired token");
    }

    @Test
    @DisplayName("Debe rechazar token nulo o en blanco")
    void shouldRejectNullOrBlankToken() {
        // Act & Assert
        assertThat(jwtTokenService.validateToken(null).valid()).isFalse();
        assertThat(jwtTokenService.validateToken("   ").valid()).isFalse();
    }
}
