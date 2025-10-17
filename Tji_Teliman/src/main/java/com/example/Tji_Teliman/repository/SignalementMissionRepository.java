package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.SignalementMission;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignalementMissionRepository extends JpaRepository<SignalementMission, Long> {
    List<SignalementMission> findByMission(Mission mission);
    List<SignalementMission> findByMissionAndJeunePrestateur(Mission mission, JeunePrestateur jeune);
    long countByMission(Mission mission);
}


