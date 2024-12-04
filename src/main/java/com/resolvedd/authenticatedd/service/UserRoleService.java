package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.UserRole;
import com.resolvedd.authenticatedd.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public void save(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
