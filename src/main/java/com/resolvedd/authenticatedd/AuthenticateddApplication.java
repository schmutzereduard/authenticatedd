package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.ApplicationService;
import com.resolvedd.authenticatedd.service.PermissionService;
import com.resolvedd.authenticatedd.service.RoleService;
import com.resolvedd.authenticatedd.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class AuthenticateddApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticateddApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(
			ApplicationService applicationService,
			RoleService roleService,
			PermissionService permissionService,
			UserService userService,
			PasswordEncoder passwordEncoder) {

		return args -> {
			// Create Permissions
			Permission permission_view = new Permission();
			permission_view.setName("view");

			Permission permission_edit = new Permission();
			permission_edit.setName("edit");

			Permission permission_delete = new Permission();
			permission_delete.setName("delete");

			permissionService.savePermission(permission_view);
			permissionService.savePermission(permission_edit);
			permissionService.savePermission(permission_delete);

			// Create Roles
			Role role_user = new Role();
			role_user.setName("user");

			Role role_admin = new Role();
			role_admin.setName("admin");

			roleService.saveRole(role_user);
			roleService.saveRole(role_admin);

			// Assign Permissions to Roles
			role_admin.setPermissions(Arrays.asList(
					new RolePermission(role_admin, permission_view, null),
					new RolePermission(role_admin, permission_edit, null),
					new RolePermission(role_admin, permission_delete, null)
			));

			role_user.setPermissions(Arrays.asList(
					new RolePermission(role_user, permission_view, null),
					new RolePermission(role_user, permission_edit, null)
			));

			// Create Applications
			Application application_macro = new Application();
			application_macro.setName("macrocalculator");

			Application application_project = new Application();
			application_project.setName("projectmanager");

			applicationService.saveApplication(application_macro);
			applicationService.saveApplication(application_project);

			// Create Users
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("adminpassword"));
			admin.setEmail("admin@example.com");
			userService.saveUser(admin);

			UserRole adminRoleMacro = new UserRole();
			adminRoleMacro.setApplication(application_macro);
			adminRoleMacro.setRole(role_admin);
			adminRoleMacro.setUser(admin);

			UserRole adminRoleProject = new UserRole();
			adminRoleProject.setApplication(application_project);
			adminRoleProject.setRole(role_admin);
			adminRoleProject.setUser(admin);

			userService.assignRoleToUser(admin, role_admin, application_macro);
			userService.assignRoleToUser(admin, role_admin, application_project);

			User user1 = new User();
			user1.setUsername("john_doe");
			user1.setPassword(passwordEncoder.encode("password123"));
			user1.setEmail("john.doe@example.com");
			userService.saveUser(user1);

			UserRole userRoleMacro = new UserRole();
			userRoleMacro.setApplication(application_macro);
			userRoleMacro.setRole(role_user);
			userRoleMacro.setUser(user1);

			UserRole userRoleProject = new UserRole();
			userRoleProject.setApplication(application_project);
			userRoleProject.setRole(role_user);
			userRoleProject.setUser(user1);

			userService.assignRoleToUser(user1, role_user, application_macro);
			userService.assignRoleToUser(user1, role_user, application_project);

			User user2 = new User();
			user2.setUsername("jane_doe");
			user2.setPassword(passwordEncoder.encode("securepassword"));
			user2.setEmail("jane.doe@example.com");
			userService.saveUser(user2);

			UserRole janeRoleMacro = new UserRole();
			janeRoleMacro.setApplication(application_macro);
			janeRoleMacro.setRole(role_user);
			janeRoleMacro.setUser(user2);

			userService.assignRoleToUser(user2, role_user, application_macro);

			// Log the created data
			System.out.println("Dummy data initialized successfully!");
		};
	}

}
