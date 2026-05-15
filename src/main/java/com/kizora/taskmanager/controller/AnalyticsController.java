package com.kizora.taskmanager.controller;

import com.kizora.taskmanager.dto.LeaderboardResponse;
import com.kizora.taskmanager.dto.OverdueTaskResponse;
import com.kizora.taskmanager.dto.WeeklyVelocityResponse;
import com.kizora.taskmanager.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
	
	private final AnalyticsService analyticsService;

    @GetMapping("/projects/{id}/overdue")
    public List<OverdueTaskResponse> getOverdueTasks(@PathVariable Long id) {
        return analyticsService.getOverdueTasks(id);
    }

    @GetMapping("/projects/{id}/velocity")
    public List<WeeklyVelocityResponse> getProjectVelocity(@PathVariable Long id) {
        return analyticsService.getProjectVelocity(id);
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardResponse> getLeaderboard() {
        return analyticsService.getLeaderboard();
    }
	
	

}
