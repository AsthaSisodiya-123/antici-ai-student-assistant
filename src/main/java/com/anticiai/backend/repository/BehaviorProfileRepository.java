package com.anticiai.backend.repository;

import com.anticiai.backend.entity.BehaviorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BehaviorProfileRepository extends JpaRepository<BehaviorProfile, Long> {
}