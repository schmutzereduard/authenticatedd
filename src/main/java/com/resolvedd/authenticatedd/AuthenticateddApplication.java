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
			PlanService planService,
			PermissionService permissionService,
			UserService userService,
			UserApplicationPlanService userApplicationPlanService,
			PasswordEncoder passwordEncoder) {

		return args -> {
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

			List<Application> applications = new ArrayList<>();
			Application application_macro = new Application("macrocalculator");
			applicationService.saveApplication(application_macro);
			applications.add(application_macro);

			Plan admin_plan = new Plan("admin");
			Plan guest_plan = new Plan("guest");
			Plan standard_plan = new Plan("standard");
			Plan premium_plan = new Plan("premium");

			admin_plan.setPermissions(Arrays.asList(
					new PlanPermission(admin_plan, addFood, 999),
					new PlanPermission(admin_plan, addRecipe, 999),
					new PlanPermission(admin_plan, addJournal, 999),
					new PlanPermission(admin_plan, deleteFood, 1),
					new PlanPermission(admin_plan, deleteRecipe, 1),
					new PlanPermission(admin_plan, deleteJournal, 1),
					new PlanPermission(admin_plan, editFood, 1),
					new PlanPermission(admin_plan, editRecipe, 1),
					new PlanPermission(admin_plan, editJournal, 1)
			));

			guest_plan.setPermissions(List.of(
                    new PlanPermission(guest_plan, addFood, 10),
                    new PlanPermission(guest_plan, addRecipe, 5),
                    new PlanPermission(guest_plan, addJournal, 1),
                    new PlanPermission(guest_plan, deleteFood, 1),
                    new PlanPermission(guest_plan, deleteRecipe, 1),
                    new PlanPermission(guest_plan, deleteJournal, 1),
                    new PlanPermission(guest_plan, editFood, 0),
                    new PlanPermission(guest_plan, editRecipe, 0),
                    new PlanPermission(guest_plan, editJournal, 0)
            ));

			standard_plan.setPermissions(Arrays.asList(
					new PlanPermission(standard_plan, addFood, 50),
					new PlanPermission(standard_plan, addRecipe, 50),
					new PlanPermission(standard_plan, addJournal, 30),
					new PlanPermission(standard_plan, deleteFood, 1),
					new PlanPermission(standard_plan, deleteRecipe, 1),
					new PlanPermission(standard_plan, deleteJournal, 1),
					new PlanPermission(standard_plan, editFood, 1),
					new PlanPermission(standard_plan, editRecipe, 1),
					new PlanPermission(standard_plan, editJournal, 1)
			));

			premium_plan.setPermissions(Arrays.asList(
					new PlanPermission(premium_plan, addFood, 500),
					new PlanPermission(premium_plan, addRecipe, 500),
					new PlanPermission(premium_plan, addJournal, 500),
					new PlanPermission(premium_plan, deleteFood, 1),
					new PlanPermission(premium_plan, deleteRecipe, 1),
					new PlanPermission(premium_plan, deleteJournal, 1),
					new PlanPermission(premium_plan, editFood, 1),
					new PlanPermission(premium_plan, editRecipe, 1),
					new PlanPermission(premium_plan, editJournal, 1)
			));

			planService.save(admin_plan);
			planService.save(guest_plan);
			planService.save(standard_plan);
			planService.save(premium_plan);

			User admin = new User("admin", passwordEncoder.encode("adminpassword"));
			admin.setEmail("admin@example.com");
			userService.saveUser(admin);

			applications.forEach(application -> {
				UserApplicationPlan adminPlan = new UserApplicationPlan(admin, application, admin_plan);
				adminPlan.setUser(admin);
				admin.getApplicationPlans().add(adminPlan);
				userApplicationPlanService.save(adminPlan);
			});

			User guest = new User("guest", passwordEncoder.encode("guestpassword"));
			guest.setEmail("guest@example.com");
			userService.saveUser(guest);

			applications.forEach(application -> {
				UserApplicationPlan guestPlan = new UserApplicationPlan(guest, application, guest_plan);
				guestPlan.setUser(guest);
				guest.getApplicationPlans().add(guestPlan);
				userApplicationPlanService.save(guestPlan);
			});

			User user = new User("user", passwordEncoder.encode("userpassword"));
			user.setEmail("user@example.com");
			userService.saveUser(user);

			applications.forEach(application -> {
				UserApplicationPlan standardPlan = new UserApplicationPlan(user, application, standard_plan);
				standardPlan.setUser(user);
				user.getApplicationPlans().add(standardPlan);
				userApplicationPlanService.save(standardPlan);
			});

			User premium = new User("premium", passwordEncoder.encode("premiumpassword"));
			premium.setEmail("premium@example.com");
			userService.saveUser(premium);

			applications.forEach(application -> {
				UserApplicationPlan premiumPlan = new UserApplicationPlan(premium, application, premium_plan);
				premiumPlan.setUser(premium);
				premium.getApplicationPlans().add(premiumPlan);
				userApplicationPlanService.save(premiumPlan);
			});
		};
	}

}
