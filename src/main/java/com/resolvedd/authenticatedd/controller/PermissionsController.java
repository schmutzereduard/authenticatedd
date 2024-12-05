package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.Permission;
import com.resolvedd.authenticatedd.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionsController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
