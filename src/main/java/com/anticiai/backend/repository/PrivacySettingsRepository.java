package com.anticiai.backend.repository;

import com.anticiai.backend.entity.PrivacySettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivacySettingsRepository extends JpaRepository<PrivacySettings, Long> {
}