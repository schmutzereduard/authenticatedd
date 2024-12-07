package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationRole;
import com.resolvedd.authenticatedd.repository.UserApplicationRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationRoleService {

    private final UserApplicationRoleRepository userApplicationRoleRepository;

    public Optional<UserApplicationRole> findByUserAndApplication(User user, Application application) {
        return userApplicationRoleRepository.findByUserAndApplication(user, application);
    }

    public void save(UserApplicationRole userRole) {
        userApplicationRoleRepository.save(userRole);
    }

    public void saveAll(List<UserApplicationRole> userRoles) {
        userApplicationRoleRepository.saveAll(userRoles);
    }
}
