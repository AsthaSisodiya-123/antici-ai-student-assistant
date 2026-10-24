
package com.anticiai.backend.repository;

import com.anticiai.backend.entity.StudentSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentSettingsRepository
        extends JpaRepository<StudentSettings, Long> {

    Optional<StudentSettings> findByUser_Id(Long userId);
}
