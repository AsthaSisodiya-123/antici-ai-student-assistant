
package com.anticiai.backend.repository;

import com.anticiai.backend.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository
        extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
