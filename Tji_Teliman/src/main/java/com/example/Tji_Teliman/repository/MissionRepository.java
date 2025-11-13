package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.enums.StatutMission;
import java.util.List;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByStatut(StatutMission statut);
    long countByStatut(StatutMission statut);
    long countByDatePublicationBetween(Date start, Date end);
}