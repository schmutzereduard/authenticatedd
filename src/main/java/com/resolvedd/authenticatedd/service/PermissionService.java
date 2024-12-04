package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Permission;
import com.resolvedd.authenticatedd.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public void savePermission(Permission permission) {
        permissionRepository.save(permission);
    }
}
