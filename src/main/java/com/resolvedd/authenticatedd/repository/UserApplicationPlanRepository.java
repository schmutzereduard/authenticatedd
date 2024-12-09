package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApplicationPlanRepository extends JpaRepository<UserApplicationPlan, Long> {

    Optional<UserApplicationPlan> findByUserAndApplication(User user, Application application);

}