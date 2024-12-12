package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.model.User;
import com.resolvedd.authenticatedd.model.UserApplicationProfile;
import com.resolvedd.authenticatedd.repository.UserApplicationProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationProfileService {

    private final UserApplicationProfileRepository userApplicationProfileRepository;

    public Optional<UserApplicationProfile> findByUserAndApplication(User user, Application application) {
        return userApplicationProfileRepository.findByUserAndApplication(user, application);
    }

    public void save(UserApplicationProfile userApplicationProfile) {
        userApplicationProfileRepository.save(userApplicationProfile);
    }

    public void saveAll(List<UserApplicationProfile> userApplicationProfiles) {
        userApplicationProfileRepository.saveAll(userApplicationProfiles);
    }
}
