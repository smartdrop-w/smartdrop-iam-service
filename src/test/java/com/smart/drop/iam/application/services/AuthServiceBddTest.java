package com.smart.drop.iam.application.services;

import com.smart.drop.iam.domain.exceptions.TwoFactorRequiredException;
import com.smart.drop.iam.domain.exceptions.UserAlreadyExistsException;
import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.domain.model.repositories.RoleRepository;
import com.smart.drop.iam.domain.model.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas BDD y Unitarias - Microservicio IAM: Autenticacion, Roles y 2FA")
class AuthServiceBddTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("US14 Escenario 1: Registro exitoso de un nuevo usuario con email unico")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        String fullName = "Carlos Mendoza";
        String email = "carlos.mendoza@smartdrop.pe";
        String passwordHash = "Password123!";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.createFromDatabase(101, u.getFullName(), u.getEmail(), u.getPasswordHash(),
                    u.getRoles(), LocalDateTime.now(), null, false, null);
        });

        // When
        User registered = authService.registerUser(fullName, email, passwordHash);

        // Then
        assertThat(registered.getId()).isEqualTo(101);
        assertThat(registered.getEmail()).isEqualTo(email);
        assertThat(registered.getFullName()).isEqualTo(fullName);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("US14 Escenario 2: Rechazo de registro cuando el correo electronico ya existe")
    void shouldRejectDuplicateEmailRegistration() {
        // Given
        String email = "valeria.brewer@artesanal.pe";
        User existing = User.create("Valeria Brewer", email, "BeerPro2026!");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existing));

        // When & Then
        assertThatThrownBy(() -> authService.registerUser("Valeria Duplicada", email, "Pass123!"))
                .isInstanceOf(UserAlreadyExistsException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("US14 Escenario 3: Login exitoso con credenciales validas sin 2FA")
    void shouldAuthenticateUserWithValidCredentials() {
        // Given
        String email = "valeria.brewer@artesanal.pe";
        String password = "BeerPro2026!";
        User activeUser = User.createFromDatabase(2, "Valeria Brewer", email, password,
                new HashSet<>(), LocalDateTime.now(), null, false, null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(activeUser));

        // When
        User loggedIn = authService.loginUser(email, password);

        // Then
        assertThat(loggedIn).isNotNull();
        assertThat(loggedIn.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("US14 Escenario 4: Rechazo de login ante contrasena invalida")
    void shouldRejectLoginWithInvalidPassword() {
        // Given
        String email = "valeria.brewer@artesanal.pe";
        User activeUser = User.createFromDatabase(2, "Valeria Brewer", email, "BeerPro2026!",
                new HashSet<>(), LocalDateTime.now(), null, false, null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(activeUser));

        // When & Then
        assertThatThrownBy(() -> authService.loginUser(email, "WrongPassword!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("US14 Escenario 5: Asignacion de rol RBAC a un usuario registrado")
    void shouldAssignRoleToUserSuccessfully() {
        // Given
        Integer userId = 101;
        String roleName = "ROLE_ADMIN";
        User user = User.createFromDatabase(userId, "Admin User", "admin@smartdrop.com", "Pass123!",
                new HashSet<>(), LocalDateTime.now(), null, false, null);
        Role adminRole = Role.createFromDatabase(1, roleName, "Administrator Role");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(adminRole));
        when(userRepository.update(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        User updated = authService.assignRoleToUser(userId, roleName);

        // Then
        assertThat(updated.hasRole(roleName)).isTrue();
        verify(userRepository, times(1)).update(user);
    }

    @Test
    @DisplayName("US14 Escenario 6: Solicitud de codigo 2FA cuando el usuario tiene doble factor activo")
    void shouldRequireTwoFactorWhenEnabled() {
        // Given
        String email = "secure@smartdrop.pe";
        String password = "SecurePass!";
        User userWith2FA = User.createFromDatabase(5, "Secure User", email, password,
                new HashSet<>(), LocalDateTime.now(), null, true, null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userWith2FA));

        // When & Then
        assertThatThrownBy(() -> authService.loginUser(email, password))
                .isInstanceOf(TwoFactorRequiredException.class);
        assertThat(userWith2FA.getTwoFactorCode()).isNotNull().hasSize(6);
        verify(userRepository, times(1)).update(userWith2FA);
    }
}
