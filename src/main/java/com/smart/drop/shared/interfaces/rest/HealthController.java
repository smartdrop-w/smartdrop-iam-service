package com.smart.drop.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/health", "/health"})
@Tag(name = "Health & System", description = "Endpoints de verificación de estado y liveness probe para Render/Cloud")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check del backend para despliegue en Render y monitoreo")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "SmartDrop Backend (Java Spring Boot)",
                "timestamp", LocalDateTime.now().toString(),
                "cloudReady", true,
                "environment", "production-ready"
        ));
    }
}
