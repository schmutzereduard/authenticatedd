package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {}