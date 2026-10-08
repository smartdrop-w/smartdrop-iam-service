package com.smart.drop.iam.interfaces.rest;

import com.smart.drop.iam.application.services.RoleService;
import com.smart.drop.iam.interfaces.rest.dto.RoleRequestDto;
import com.smart.drop.iam.interfaces.rest.dto.RoleResponseDto;
import com.smart.drop.iam.domain.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de roles.
 * Mapea requests HTTP a casos de uso del dominio.
 */
@RestController
@RequestMapping({"/api/v1/iam/roles", "/api/v1/identity/roles"})
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Crea un nuevo rol.
     * POST /api/identity/roles
     *
     * @param request datos del rol a crear
     * @return ResponseEntity con el rol creado
     */
    @PostMapping
    public ResponseEntity<RoleResponseDto> createRole(@RequestBody RoleRequestDto request) {
        Role role = roleService.createRole(request.name(), request.description());
        RoleResponseDto response = toRoleResponseDto(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene un rol por su ID.
     * GET /api/identity/roles/{roleId}
     *
     * @param roleId el ID del rol
     * @return ResponseEntity con el rol, o 404 si no existe
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable Integer roleId) {
        return roleService.getRole(roleId)
                .map(role -> ResponseEntity.ok(toRoleResponseDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene un rol por su nombre.
     * GET /api/identity/roles/by-name/{name}
     *
     * @param name el nombre del rol
     * @return ResponseEntity con el rol, o 404 si no existe
     */
    @GetMapping("/by-name/{name}")
    public ResponseEntity<RoleResponseDto> getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name)
                .map(role -> ResponseEntity.ok(toRoleResponseDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todos los roles.
     * GET /api/identity/roles
     *
     * @return ResponseEntity con lista de todos los roles
     */
    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = roleService.getAllRoles()
                .stream()
                .map(this::toRoleResponseDto)
                .toList();
        return ResponseEntity.ok(roles);
    }

    /**
     * Actualiza un rol existente.
     * PUT /api/identity/roles/{roleId}
     *
     * @param roleId el ID del rol a actualizar
     * @param request datos actualizados del rol
     * @return ResponseEntity con el rol actualizado
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Integer roleId, @RequestBody RoleRequestDto request) {
        Role role = roleService.updateRole(roleId, request.name(), request.description());
        RoleResponseDto response = toRoleResponseDto(role);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un rol por su ID.
     * DELETE /api/identity/roles/{roleId}
     *
     * @param roleId el ID del rol a eliminar
     * @return ResponseEntity sin contenido (204) si fue eliminado, o 404 si no existe
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer roleId) {
        boolean deleted = roleService.deleteRole(roleId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Obtiene el número total de roles.
     * GET /api/identity/roles/total-roles
     *
     * @return ResponseEntity con el total
     */
    @GetMapping("/total-roles")
    public ResponseEntity<Long> getTotalRoles() {
        long totalRoles = roleService.getTotalRoles();
        return ResponseEntity.ok(totalRoles);
    }

    /**
     * Convierte una entidad de dominio Role a DTO de respuesta.
     *
     * @param role la entidad de dominio
     * @return el DTO de respuesta
     */
    private RoleResponseDto toRoleResponseDto(Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}

