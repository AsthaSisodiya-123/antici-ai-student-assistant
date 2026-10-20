package com.anticiai.backend.repository;

import com.anticiai.backend.entity.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingStatusRepository
        extends JpaRepository<OnboardingStatus, Long> {

    Optional<OnboardingStatus> findByDeviceId(String deviceId);
}