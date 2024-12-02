package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.Role;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserRole;
import com.resolvedd.authenticatedd.repository.UserRepository;
import com.resolvedd.authenticatedd.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void assignRoleToUser(User user, Role role, Application application) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setApplication(application);
        userRoleRepository.save(userRole);
    }

    public List<Role> getUserRolesForApplication(User user, Application application) {
        return userRoleRepository.findByUserAndApplication(user, application)
                .stream()
                .map(UserRole::getRole)
                .toList();
    }
}
