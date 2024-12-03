package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserRole;
import com.resolvedd.authenticatedd.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public List<UserRole> findByUser(User user) {
        return userRoleRepository.findByUser(user);
    }

    public List<UserRole> findByUserAndApplication(User user, Application application) {
        return userRoleRepository.findByUserAndApplication(user, application);
    }

    public void assignRoleToUser(User user, Role role, Application application) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setApplication(application);
        userRoleRepository.save(userRole);
    }
}
