package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {}