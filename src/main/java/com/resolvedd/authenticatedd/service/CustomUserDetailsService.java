package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final ApplicationService applicationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Initialize roles and permissions for the user
        user.getRoles().stream()
                .map(UserRole::getRole) // Map from UserRole to Role
                .forEach(role -> Hibernate.initialize(role.getPermissions())); // Initialize Role -> Permissions

        // Convert roles and permissions to Spring Security authorities
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(UserRole::getRole) // Map again to Role
                .flatMap(role -> role.getPermissions().stream()
                        .map(RolePermission::getPermission) // Map to Permission
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))) // Map to GrantedAuthority
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

}
