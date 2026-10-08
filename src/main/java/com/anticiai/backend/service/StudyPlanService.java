package com.anticiai.backend.service;

import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.entity.Exam;
import com.anticiai.backend.entity.Task;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.ExamRepository;
import com.anticiai.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StudyPlanService {

    private final ExamRepository examRepository;
    private final TaskRepository taskRepository;

    public StudyPlanService(
            ExamRepository examRepository,
            TaskRepository taskRepository
    ) {
        this.examRepository = examRepository;
        this.taskRepository = taskRepository;
    }

    public StudyPlanResponse generatePlan(User user) {

        List<Exam> exams = examRepository.findByUser(user);
        List<Task> tasks = taskRepository.findByUser(user);

        final List<StudyPlanResponse.StudyPlanItem> sessions =
                new ArrayList<>();

        /*
         * Priority 1:
         * Upcoming exam.
         */
        exams.stream()
                .sorted(
                        Comparator.comparing(
                                Exam::getExamDate,
                                Comparator.nullsLast(
                                        String::compareTo
                                )
                        )
                )
                .limit(1)
                .forEach(exam -> {

                    String examSubject = exam.getSubject();

                    String finalSubject =
                            examSubject == null
                                    || examSubject.isBlank()
                                    ? "Exam Preparation"
                                    : examSubject;

                    String examTitle = exam.getTitle();

                    String finalTopic =
                            examTitle == null
                                    || examTitle.isBlank()
                                    ? "Exam Preparation"
                                    : examTitle;

                    sessions.add(
                            new StudyPlanResponse.StudyPlanItem(
                                    "07:00 PM",
                                    45,
                                    finalSubject,
                                    finalTopic,
                                    "HIGH"
                            )
                    );
                });

        /*
         * Priority 2:
         * Pending tasks.
         */
        tasks.stream()
                .filter(task ->
                        task.getStatus() == null
                                || !task.getStatus()
                                .equalsIgnoreCase("COMPLETED")
                )
                .sorted(
                        Comparator.comparing(
                                Task::getPriority,
                                Comparator.nullsLast(
                                        String::compareTo
                                )
                        )
                )
                .limit(2)
                .forEach(task -> {

                    String taskTitle = task.getTitle();

                    String finalTopic =
                            taskTitle == null
                                    || taskTitle.isBlank()
                                    ? "Pending Task"
                                    : taskTitle;

                    String taskDescription =
                            task.getDescription();

                    String finalDescription =
                            taskDescription == null
                                    || taskDescription.isBlank()
                                    ? "Complete this pending task."
                                    : taskDescription;

                    String taskPriority =
                            task.getPriority();

                    String finalPriority =
                            taskPriority == null
                                    || taskPriority.isBlank()
                                    ? "MEDIUM"
                                    : taskPriority.toUpperCase();

                    sessions.add(
                            new StudyPlanResponse.StudyPlanItem(
                                    "08:00 PM",
                                    30,
                                    "Task",
                                    finalTopic
                                            + " — "
                                            + finalDescription,
                                    finalPriority
                            )
                    );
                });

        /*
         * Add revision session when needed.
         */
        if (sessions.size() < 3) {

            sessions.add(
                    new StudyPlanResponse.StudyPlanItem(
                            "08:40 PM",
                            20,
                            "Revision",
                            "Review today's concepts and notes",
                            "MEDIUM"
                    )
            );
        }

        /*
         * Limit to three sessions without
         * reassigning the original list.
         */
        List<StudyPlanResponse.StudyPlanItem> finalSessions;

        if (sessions.size() > 3) {
            finalSessions = new ArrayList<>(
                    sessions.subList(0, 3)
            );
        } else {
            finalSessions = new ArrayList<>(sessions);
        }

        String title = "Personalized Study Plan";

        String description =
                "A focused plan generated from your "
                        + "exams and pending tasks.";

        /*
         * Find the nearest exam.
         */
        Exam nearestExam = exams.stream()
                .filter(exam ->
                        exam.getTitle() != null
                                && !exam.getTitle().isBlank()
                )
                .min(
                        Comparator.comparing(
                                Exam::getExamDate,
                                Comparator.nullsLast(
                                        String::compareTo
                                )
                        )
                )
                .orElse(null);

        if (nearestExam != null) {

            title = nearestExam.getTitle()
                    + " Preparation";

            description =
                    "A focused preparation plan based "
                            + "on your upcoming exam.";
        }

        return new StudyPlanResponse(
                title,
                description,
                finalSessions
        );
    }
}

