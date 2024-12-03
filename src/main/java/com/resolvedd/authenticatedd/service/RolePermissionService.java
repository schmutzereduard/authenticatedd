package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Permission;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.RolePermission;
import com.resolvedd.authenticatedd.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public List<Permission> findPermissionsByRoleAndApplication(Role role, Application application) {
        return rolePermissionRepository.findByRoleAndApplication(role, application)
                .stream()
                .map(RolePermission::getPermission)
                .toList();
    }

    public void assignPermissionToRole(Role role, Permission permission, Application application) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setApplication(application);
        rolePermissionRepository.save(rolePermission);
    }
}
