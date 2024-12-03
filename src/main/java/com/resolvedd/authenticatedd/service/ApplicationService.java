package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public Optional<Application> findByName(String name) {
        return applicationRepository.findByName(name);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }
}
