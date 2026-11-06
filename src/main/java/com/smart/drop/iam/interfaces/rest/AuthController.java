package com.smart.drop.iam.interfaces.rest;

import com.smart.drop.iam.application.services.AuthService;
import com.smart.drop.iam.domain.exceptions.TwoFactorRequiredException;
import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.infrastructure.security.jwt.JwtTokenService;
import com.smart.drop.iam.interfaces.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de autenticación, emisión de JWT y administración de identidades.
 */
@Tag(name = "Authentication & Identity Management", description = "Endpoints para registro, login con emisión de JWT HMAC-SHA256, 2FA, gestión de credenciales e introspección de tokens")
@RestController
@RequestMapping({"/api/v1/iam/auth", "/api/iam/auth", "/api/identity/auth"})
public class AuthController {

    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthService authService, JwtTokenService jwtTokenService) {
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema con contraseña cifrada en BCrypt.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o correo ya registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterUserRequestDto request) {
        User user = authService.registerUser(request.fullName(), request.email(), request.password());
        UserResponseDto response = toUserResponseDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar sesión y obtener token JWT", description = "Valida credenciales y genera un token JWT firmado HMAC-SHA256 con los claims del usuario y roles asignados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa y token emitido", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "202", description = "Doble factor requerido (código 2FA enviado al correo)"),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o usuario inexistente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserRequestDto request) {
        try {
            User user = authService.loginUser(request.email(), request.password());
            String token = jwtTokenService.generateToken(user);
            UserResponseDto response = new UserResponseDto(user.getId(), user.getFullName(), user.getEmail(), user.getCreatedAt(), token);
            return ResponseEntity.ok(response);
        } catch (TwoFactorRequiredException e) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "requires2FA", true,
                    "simulatedCode", e.getSimulatedCode(),
                    "message", "A 6-digit code has been sent to your email."
            ));
        }
    }

    @Operation(summary = "Validar e introspeccionar token JWT", description = "Verifica la validez y firma HMAC-SHA256 del token y retorna los claims descifrados (usuario, roles, userId).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de introspección del token", content = @Content(schema = @Schema(implementation = ValidateTokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Formato de token no proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@Valid @RequestBody ValidateTokenRequestDto request) {
        ValidateTokenResponseDto response = jwtTokenService.validateToken(request.token());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Solicitar recuperación de contraseña", description = "Genera un token de reseteo seguro y simula el envío del enlace de recuperación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token de recuperación generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        String token = authService.generatePasswordResetToken(request.email());
        return ResponseEntity.ok(Map.of("success", true, "token", token, "message", "Recovery link simulated in console."));
    }

    @Operation(summary = "Restablecer contraseña con token", description = "Actualiza la contraseña del usuario utilizando el token de reseteo previamente emitido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña restablecida correctamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(Map.of("success", true, "message", "Password successfully updated."));
    }

    @Operation(summary = "Habilitar autenticación de doble factor (2FA)", description = "Activa el requerimiento de código OTP por correo electrónico al iniciar sesión.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA activado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/2fa/enable")
    public ResponseEntity<?> enable2FA(@Valid @RequestBody Enable2FARequestDto request) {
        authService.enable2FA(request.email());
        return ResponseEntity.ok(Map.of("success", true, "message", "2FA has been enabled successfully via Email."));
    }

    @Operation(summary = "Consultar estado de 2FA", description = "Retorna si el doble factor de autenticación se encuentra activo para el usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de 2FA obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/2fa/status/{email}")
    public ResponseEntity<?> get2FAStatus(@PathVariable String email) {
        return authService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of("enabled", Boolean.TRUE.equals(user.getIsTwoFactorEnabled()))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Verificar código 2FA y completar login", description = "Valida el código OTP de 6 dígitos y emite el token JWT de acceso correspondiente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Código verificado y token emitido", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Código incorrecto o expirado")
    })
    @PostMapping("/2fa/verify")
    public ResponseEntity<UserResponseDto> verify2FA(@Valid @RequestBody Verify2FARequestDto request) {
        User user = authService.verify2FA(request.email(), request.code());
        String token = jwtTokenService.generateToken(user);
        UserResponseDto response = new UserResponseDto(user.getId(), user.getFullName(), user.getEmail(), user.getCreatedAt(), token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar contraseña de usuario", description = "Permite cambiar la clave confirmando la contraseña actual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta")
    })
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        authService.changePassword(request.email(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully."));
    }

    @Operation(summary = "Actualizar nombre del usuario", description = "Modifica el nombre completo registrado en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nombre actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/change-name")
    public ResponseEntity<?> changeName(@Valid @RequestBody ChangeNameRequestDto request) {
        authService.changeName(request.email(), request.newName());
        return ResponseEntity.ok(Map.of("success", true, "message", "Name changed successfully."));
    }

    @Operation(summary = "Deshabilitar autenticación 2FA", description = "Desactiva la verificación de dos pasos para el usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "2FA deshabilitado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/2fa/disable")
    public ResponseEntity<?> disable2FA(@Valid @RequestBody Disable2FARequestDto request) {
        authService.disable2FA(request.email());
        return ResponseEntity.ok(Map.of("success", true, "message", "2FA has been disabled."));
    }

    @Operation(summary = "Obtener usuario por ID", description = "Recupera la información básica del usuario a partir de su ID único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Integer userId) {
        return authService.getUser(userId)
                .map(user -> ResponseEntity.ok(toUserResponseDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna la colección completa de cuentas registradas en SmartDrop.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente")
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = authService.getAllUsers().stream()
                .map(this::toUserResponseDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Buscar usuario por email", description = "Recupera el perfil del usuario mediante su dirección de correo electrónico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/users/by-email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return authService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(toUserResponseDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Asignar rol a usuario", description = "Agrega un rol RBAC existente al conjunto de permisos del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol asignado correctamente", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "El usuario ya posee el rol o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado")
    })
    @PostMapping("/assign-role")
    public ResponseEntity<UserResponseDto> assignRoleToUser(@Valid @RequestBody AssignRoleRequestDto request) {
        User user = authService.assignRoleToUser(request.userId(), request.roleName());
        UserResponseDto response = toUserResponseDto(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Revocar rol a usuario", description = "Remueve un rol RBAC del perfil del usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol revocado correctamente", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "El usuario no posee dicho rol"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado")
    })
    @PostMapping("/revoke-role")
    public ResponseEntity<UserResponseDto> revokeRoleFromUser(@Valid @RequestBody AssignRoleRequestDto request) {
        User user = authService.revokeRoleFromUser(request.userId(), request.roleName());
        UserResponseDto response = toUserResponseDto(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar pertenencia de rol", description = "Comprueba si un usuario cuenta con un rol específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado booleano de pertenencia al rol")
    })
    @GetMapping("/users/{userId}/has-role/{roleName}")
    public ResponseEntity<Boolean> userHasRole(@PathVariable Integer userId, @PathVariable String roleName) {
        boolean hasRole = authService.userHasRole(userId, roleName);
        return ResponseEntity.ok(hasRole);
    }

    @Operation(summary = "Total de usuarios registrados", description = "Retorna el contador global de cuentas creadas en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad total de usuarios")
    })
    @GetMapping("/total-users")
    public ResponseEntity<Long> getTotalUsers() {
        long totalUsers = authService.getTotalUsers();
        return ResponseEntity.ok(totalUsers);
    }

    private UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt(),
                null
        );
    }
}
