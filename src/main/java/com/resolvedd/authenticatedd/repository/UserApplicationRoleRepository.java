package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApplicationRoleRepository extends JpaRepository<UserApplicationRole, Long> {

    Optional<UserApplicationRole> findByUserAndApplication(User user, Application application);

}