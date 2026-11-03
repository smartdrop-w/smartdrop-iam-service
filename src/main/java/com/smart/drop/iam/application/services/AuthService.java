package com.smart.drop.iam.application.services;

import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.repositories.UserRepository;
import com.smart.drop.iam.domain.model.repositories.RoleRepository;
import com.smart.drop.iam.domain.exceptions.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio de aplicación para autenticación utilizando inyección de dependencias.
 * Orquesta la lógica de negocio entre el dominio y la persistencia.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Registra un nuevo usuario sin roles.
     *
     * @param fullName nombre completo del usuario
     * @param email email del usuario
     * @param passwordHash hash o contraseña inicial del usuario
     * @return el usuario creado y persistido con hash BCrypt
     */
    public User registerUser(String fullName, String email, String passwordHash) {
        // Valida que el email no esté registrado
        if (userRepository.findByEmail(email).isPresent()) {
            throw UserAlreadyExistsException.withEmail(email);
        }

        String hashedPassword = (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$"))
                ? passwordHash
                : passwordEncoder.encode(passwordHash);

        // Crea la entidad de dominio
        User user = User.create(fullName, email, hashedPassword);

        // Persiste y retorna
        return userRepository.save(user);
    }

    /**
     * Valida las credenciales e inicia sesión para un usuario.
     *
     * @param email el email del usuario
     * @return el usuario validado
     */
    @Transactional(noRollbackFor = TwoFactorRequiredException.class)
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));

        boolean matches = (user.getPasswordHash().startsWith("$2a$") || user.getPasswordHash().startsWith("$2b$"))
                ? passwordEncoder.matches(password, user.getPasswordHash())
                : user.getPasswordHash().equals(password);

        if (!matches) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Actualización progresiva a BCrypt si la contraseña estaba en texto plano
        boolean upgradedHash = false;
        if (!user.getPasswordHash().startsWith("$2a$") && !user.getPasswordHash().startsWith("$2b$")) {
            user.setPasswordHash(passwordEncoder.encode(password));
            upgradedHash = true;
        }

        if (Boolean.TRUE.equals(user.getIsTwoFactorEnabled())) {
            String code = String.format("%06d", secureRandom.nextInt(1_000_000));
            user.setTwoFactorCode(code);
            userRepository.update(user);
            throw new TwoFactorRequiredException(email, code);
        }

        if (upgradedHash) {
            userRepository.update(user);
        }

        return user;
    }

    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
        
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.update(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(InvalidTokenException::new);
                
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.update(user);
    }

    public void enable2FA(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
        user.setIsTwoFactorEnabled(true);
        userRepository.update(user);
    }

    public User verify2FA(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
                
        if (code == null || !code.equals(user.getTwoFactorCode())) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        
        user.setTwoFactorCode(null);
        return userRepository.update(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));

        boolean matches = (user.getPasswordHash().startsWith("$2a$") || user.getPasswordHash().startsWith("$2b$"))
                ? passwordEncoder.matches(currentPassword, user.getPasswordHash())
                : user.getPasswordHash().equals(currentPassword);

        if (!matches) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.update(user);
    }

    public void disable2FA(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
        user.setIsTwoFactorEnabled(false);
        userRepository.update(user);
    }

    public void changeName(String email, String newName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));
        user.setFullName(newName);
        userRepository.update(user);
    }

    /**
     * Obtiene un usuario por ID.
     *
     * @param userId el ID del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<User> getUser(Integer userId) {
        return userRepository.findById(userId);
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return una lista con todos los usuarios
     */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por email.
     *
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Asigna un rol a un usuario.
     *
     * @param userId el ID del usuario
     * @param roleName el nombre del rol
     * @return el usuario actualizado con el rol asignado
     */
    public User assignRoleToUser(Integer userId, String roleName) {
        // Obtiene el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // Obtiene el rol
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> RoleNotFoundException.withName(roleName));

        // Valida que el usuario no tenga ya ese rol
        if (user.hasRole(roleName)) {
            throw UserAlreadyHasRoleException.forUserAndRole(userId, roleName);
        }

        // Añade el rol
        user.addRole(role);

        // Persiste
        return userRepository.update(user);
    }

    /**
     * Revoca un rol de un usuario.
     *
     * @param userId el ID del usuario
     * @param roleName el nombre del rol
     * @return el usuario actualizado sin el rol
     */
    public User revokeRoleFromUser(Integer userId, String roleName) {
        // Obtiene el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // Obtiene el rol
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> RoleNotFoundException.withName(roleName));

        // Valida que el usuario tenga ese rol
        if (!user.hasRole(roleName)) {
            throw new IllegalArgumentException("User does not have role: " + roleName);
        }

        // Revoca el rol
        user.removeRole(role);

        // Persiste
        return userRepository.update(user);
    }

    /**
     * Verifica si un usuario tiene un rol específico.
     *
     * @param userId el ID del usuario
     * @param roleName el nombre del rol
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public boolean userHasRole(Integer userId, String roleName) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> u.hasRole(roleName)).orElse(false);
    }

    /**
     * Obtiene el total de usuarios registrados.
     *
     * @return el número total de usuarios
     */
    public long getTotalUsers() {
        return userRepository.count();
    }
}
