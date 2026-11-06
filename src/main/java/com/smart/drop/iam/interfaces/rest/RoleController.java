package com.smart.drop.iam.interfaces.rest;

import com.smart.drop.iam.application.services.RoleService;
import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.interfaces.rest.dto.RoleRequestDto;
import com.smart.drop.iam.interfaces.rest.dto.RoleResponseDto;
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

/**
 * Controlador REST para gestión de roles del sistema de autorización (RBAC).
 */
@Tag(name = "Role Management (RBAC)", description = "Administración del catálogo de roles y permisos del sistema")
@RestController
@RequestMapping({"/api/v1/iam/roles", "/api/v1/identity/roles"})
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Crear nuevo rol", description = "Registra un nuevo rol de autorización RBAC en el catálogo del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rol creado exitosamente", content = @Content(schema = @Schema(implementation = RoleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Nombre de rol inválido o duplicado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto request) {
        Role role = roleService.createRole(request.name(), request.description());
        RoleResponseDto response = toRoleResponseDto(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener rol por ID", description = "Recupera un rol a partir de su identificador numérico único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = @Content(schema = @Schema(implementation = RoleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable Integer roleId) {
        return roleService.getRole(roleId)
                .map(role -> ResponseEntity.ok(toRoleResponseDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar rol por nombre", description = "Recupera un rol utilizando su código canónico (ej: ROLE_ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol encontrado", content = @Content(schema = @Schema(implementation = RoleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/by-name/{name}")
    public ResponseEntity<RoleResponseDto> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(role -> ResponseEntity.ok(toRoleResponseDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los roles", description = "Retorna el catálogo completo de roles disponibles en la plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles()
                .stream()
                .map(this::toRoleResponseDto)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Actualizar rol existente", description = "Modifica los atributos y descripción de un rol RBAC.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente", content = @Content(schema = @Schema(implementation = RoleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Integer roleId, @Valid @RequestBody RoleRequestDto request) {
        Role role = roleService.updateRole(roleId, request.name(), request.description());
        RoleResponseDto response = toRoleResponseDto(role);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar rol", description = "Elimina permanentemente un rol del catálogo si no posee dependencias activas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rol eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer roleId) {
        boolean deleted = roleService.deleteRole(roleId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Total de roles registrados", description = "Retorna el contador total de roles en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cantidad total de roles")
    })
    @GetMapping("/total-roles")
    public ResponseEntity<Long> getTotalRoles() {
        long totalRoles = roleService.getTotalRoles();
        return ResponseEntity.ok(totalRoles);
    }

    private RoleResponseDto toRoleResponseDto(Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}
