package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> { }