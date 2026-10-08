package com.smart.drop.iam.config;

import com.smart.drop.iam.infrastructure.persistence.jpa.entities.RoleEntity;
import com.smart.drop.iam.infrastructure.persistence.jpa.entities.UserEntity;
import com.smart.drop.iam.infrastructure.persistence.jpa.repositories.RoleJpaRepository;
import com.smart.drop.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IamDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IamDataInitializer.class);

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;

    public IamDataInitializer(UserJpaRepository userRepository, RoleJpaRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("[IAM Service] Semillero omitido: Ya existen registros de usuarios.");
            return;
        }

        log.info("[IAM Service] Sembrando usuarios y roles iniciales...");

        // Roles
        RoleEntity roleUser = roleRepository.save(new RoleEntity("ROLE_USER", "Usuario Estándar"));
        RoleEntity roleAdmin = roleRepository.save(new RoleEntity("ROLE_ADMIN", "Administrador Global"));
        RoleEntity roleBrewer = roleRepository.save(new RoleEntity("ROLE_BREWER", "Operador Cervecero"));

        // Usuarios (Contraseña de prueba: 'SmartDrop2026!')
        UserEntity userDemo = new UserEntity("Juan Pérez (Residencial)", "demo@smartdrop.io", "$2a$10$wTqKx7MfvT6K.8JjF0Yykeu3W1eY8M3K8Qc9h2eL8y6v2W0z9oZ9K");
        userDemo.getRoles().add(roleUser);
        userRepository.save(userDemo);

        UserEntity userBrewer = new UserEntity("Carlos Bohórquez (Cerveza Bohórquez)", "brewer@bohorquez.pe", "$2a$10$wTqKx7MfvT6K.8JjF0Yykeu3W1eY8M3K8Qc9h2eL8y6v2W0z9oZ9K");
        userBrewer.getRoles().add(roleBrewer);
        userRepository.save(userBrewer);

        UserEntity userAdmin = new UserEntity("Marco Ochante (Admin)", "admin@smartdrop.io", "$2a$10$wTqKx7MfvT6K.8JjF0Yykeu3W1eY8M3K8Qc9h2eL8y6v2W0z9oZ9K");
        userAdmin.getRoles().add(roleAdmin);
        userRepository.save(userAdmin);

        log.info("[IAM Service] Sembrado completado: 3 roles y 3 usuarios creados.");
    }
}
