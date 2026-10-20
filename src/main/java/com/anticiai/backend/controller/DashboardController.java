package com.anticiai.backend.controller;

import com.anticiai.backend.service.DashboardService;

import org.springframework.stereotype.Controller;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
}
