package com.resolvedd.authenticatedd.controller;

import com.resolvedd.authenticatedd.model.Application;
import com.resolvedd.authenticatedd.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
@PreAuthorize("hasRole('admin')")
public class ApplicationsController {

    private final ApplicationService applicationService;

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }
}
