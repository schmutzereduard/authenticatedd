package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.RolePermission;
import com.resolvedd.authenticatedd.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermission save(RolePermission permission) {
        return rolePermissionRepository.save(permission);
    }
}
