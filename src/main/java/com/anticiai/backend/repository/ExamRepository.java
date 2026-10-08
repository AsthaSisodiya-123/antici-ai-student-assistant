package com.anticiai.backend.repository;

import com.anticiai.backend.entity.Exam;
import com.anticiai.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByUser(User user);
}

