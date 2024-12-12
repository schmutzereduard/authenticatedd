package com.resolvedd.authenticatedd.repository;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApplicationProfileRepository extends JpaRepository<UserApplicationProfile, Long> {

    Optional<UserApplicationProfile> findByUserAndApplication(User user, Application application);
}