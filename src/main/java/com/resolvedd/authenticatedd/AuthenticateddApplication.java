package com.resolvedd.authenticatedd;

import com.resolvedd.authenticatedd.model.*;
import com.resolvedd.authenticatedd.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
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
			UserApplicationRoleService userApplicationRoleService,
			PasswordEncoder passwordEncoder) {

		return args -> {
			// Create Permissions
			Permission addFood = new Permission("add_food");
			Permission editFood = new Permission("edit_food");
			Permission deleteFood = new Permission("delete_food");
			Permission addRecipe = new Permission("add_recipe");
			Permission editRecipe = new Permission("edit_recipe");
			Permission deleteRecipe = new Permission("delete_recipe");
			Permission addJournal = new Permission("add_journal");
			Permission editJournal = new Permission("edit_journal");
			Permission deleteJournal = new Permission("delete_journal");

			permissionService.savePermission(addFood);
			permissionService.savePermission(editFood);
			permissionService.savePermission(deleteFood);
			permissionService.savePermission(addRecipe);
			permissionService.savePermission(editRecipe);
			permissionService.savePermission(deleteRecipe);
			permissionService.savePermission(addJournal);
			permissionService.savePermission(editJournal);
			permissionService.savePermission(deleteJournal);

			// Create Apps
			List<Application> applications = new ArrayList<>();
			Application application_macro = new Application("macrocalculator");
			applicationService.saveApplication(application_macro);
			applications.add(application_macro);

			// Create Roles
			Role role_admin = new Role("admin");
			Role role_guest = new Role("guest");
			Role role_user = new Role("user");
			Role role_premium = new Role("premium");

			// Assign Permissions to Roles
			role_admin.setPermissions(Arrays.asList(
					new RolePermission(role_admin, addFood, 999),
					new RolePermission(role_admin, addRecipe, 999),
					new RolePermission(role_admin, addJournal, 999),
					new RolePermission(role_admin, deleteFood, 1),
					new RolePermission(role_admin, deleteRecipe, 1),
					new RolePermission(role_admin, deleteJournal, 1),
					new RolePermission(role_admin, editFood, 1),
					new RolePermission(role_admin, editRecipe, 1),
					new RolePermission(role_admin, editJournal, 1)
			));

			role_guest.setPermissions(List.of(
                    new RolePermission(role_guest, addFood, 10),
                    new RolePermission(role_guest, addRecipe, 5),
                    new RolePermission(role_guest, addJournal, 1),
                    new RolePermission(role_guest, deleteFood, 1),
                    new RolePermission(role_guest, deleteRecipe, 1),
                    new RolePermission(role_guest, deleteJournal, 1),
                    new RolePermission(role_guest, editFood, 0),
                    new RolePermission(role_guest, editRecipe, 0),
                    new RolePermission(role_guest, editJournal, 0)
            ));

			role_user.setPermissions(Arrays.asList(
					new RolePermission(role_user, addFood, 50),
					new RolePermission(role_user,	 addRecipe, 50),
					new RolePermission(role_user, addJournal, 30),
					new RolePermission(role_user, deleteFood, 1),
					new RolePermission(role_user, deleteRecipe, 1),
					new RolePermission(role_user, deleteJournal, 1),
					new RolePermission(role_user, editFood, 1),
					new RolePermission(role_user, editRecipe, 1),
					new RolePermission(role_user, editJournal, 1)
			));

			role_premium.setPermissions(Arrays.asList(
					new RolePermission(role_premium, addFood, 500),
					new RolePermission(role_premium, addRecipe, 500),
					new RolePermission(role_premium, addJournal, 500),
					new RolePermission(role_premium, deleteFood, 1),
					new RolePermission(role_premium, deleteRecipe, 1),
					new RolePermission(role_premium, deleteJournal, 1),
					new RolePermission(role_premium, editFood, 1),
					new RolePermission(role_premium, editRecipe, 1),
					new RolePermission(role_premium, editJournal, 1)
			));

			roleService.saveRole(role_admin);
			roleService.saveRole(role_guest);
			roleService.saveRole(role_user);
			roleService.saveRole(role_premium);

			// Create Users
			User admin = new User("admin", passwordEncoder.encode("adminpassword"));
			admin.setEmail("admin@example.com");
			userService.saveUser(admin);

			applications.forEach(application -> {
				UserApplicationRole adminRole = new UserApplicationRole(admin, application, role_admin);
				adminRole.setUser(admin);
				admin.getRoles().add(adminRole);
				userApplicationRoleService.save(adminRole);
			});

			User guest = new User("guest", passwordEncoder.encode("guestpassword"));
			guest.setEmail("guest@example.com");
			userService.saveUser(guest);

			applications.forEach(application -> {
				UserApplicationRole guestRole = new UserApplicationRole(guest, application, role_guest);
				guestRole.setUser(guest);
				guest.getRoles().add(guestRole);
				userApplicationRoleService.save(guestRole);
			});

			User user = new User("user", passwordEncoder.encode("userpassword"));
			user.setEmail("user@example.com");
			userService.saveUser(user);

			applications.forEach(application -> {
				UserApplicationRole userRole = new UserApplicationRole(user, application, role_user);
				userRole.setUser(user);
				user.getRoles().add(userRole);
				userApplicationRoleService.save(userRole);
			});

			User premium = new User("premium", passwordEncoder.encode("premiumpassword"));
			premium.setEmail("premium@example.com");
			userService.saveUser(premium);

			applications.forEach(application -> {
				UserApplicationRole premiumRole = new UserApplicationRole(premium, application, role_premium);
				premiumRole.setUser(premium);
				premium.getRoles().add(premiumRole);
				userApplicationRoleService.save(premiumRole);
			});



			// Log the created data
			System.out.println("Dummy data initialized successfully!");
		};
	}

}
