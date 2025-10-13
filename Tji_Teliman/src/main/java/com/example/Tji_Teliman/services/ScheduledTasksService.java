package com.example.Tji_Teliman.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasksService {

    private final MissionService missionService;

    public ScheduledTasksService(MissionService missionService) {
        this.missionService = missionService;
    }

    // Exécuter tous les jours à minuit
    @Scheduled(cron = "0 0 0 * * ?")
    public void verifierMissionsTerminees() {
        missionService.verifierMissionsTerminees();
    }
}