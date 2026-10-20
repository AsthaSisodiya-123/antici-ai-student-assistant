package com.anticiai.backend.service;

import com.anticiai.backend.entity.OnboardingStatus;
import com.anticiai.backend.repository.OnboardingStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {

    private final OnboardingStatusRepository repository;

    public OnboardingService(OnboardingStatusRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public OnboardingStatus getStatus(String deviceId) {
        return repository.findByDeviceId(deviceId)
                .orElseGet(() -> {
                    OnboardingStatus status = new OnboardingStatus();
                    status.setDeviceId(deviceId);
                    return status;
                });
    }

    @Transactional
    public OnboardingStatus complete(String deviceId) {
        OnboardingStatus status = repository.findByDeviceId(deviceId)
                .orElseGet(() -> {
                    OnboardingStatus newStatus = new OnboardingStatus();
                    newStatus.setDeviceId(deviceId);
                    return newStatus;
                });

        status.setCompleted(true);
        return repository.save(status);
    }

    @Transactional
    public OnboardingStatus reset(String deviceId) {
        OnboardingStatus status = repository.findByDeviceId(deviceId)
                .orElseGet(() -> {
                    OnboardingStatus newStatus = new OnboardingStatus();
                    newStatus.setDeviceId(deviceId);
                    return newStatus;
                });

        status.setCompleted(false);
        return repository.save(status);
    }
}