package com.anticiai.backend.service;

import com.anticiai.backend.dto.DashboardResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DashboardService {

    public DashboardResponse getDashboard() {

        DashboardResponse response = new DashboardResponse();

        // Demo values until connected to real student data.
        response.setName("Student");
        response.setStudyProgress(0);
        response.setActiveTasks(0);

        response.setAiStatus("Your next study session starts here.");
        response.setAiDescription(
                "Start a study session to build your learning history "
                        + "and receive personalized study insights."
        );

        response.setPredictionConfidence(0);
        response.setPredictionTitle(
                "Your study recommendation will appear here."
        );
        response.setPredictionDetails(
                "Complete a study session to build your learning history."
        );

        response.setUpcomingSessions(
                Arrays.asList(
                        new DashboardResponse.UpcomingSession(
                                "No scheduled subject yet",
                                "Create a study plan to get started"
                        )
                )
        );

        return response;
    }
}
