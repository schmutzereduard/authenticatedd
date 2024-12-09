package com.resolvedd.authenticatedd.service;

import com.resolvedd.authenticatedd.repository.PlanPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanPermissionService {

    private final PlanPermissionRepository planPermissionRepository;
}
