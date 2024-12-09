package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.PlanPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanPermissionRepository extends JpaRepository<PlanPermission, Long> { }