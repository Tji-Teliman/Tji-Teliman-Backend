package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByJeunePrestateur(JeunePrestateur jeunePrestateur);
    List<Candidature> findByMission(Mission mission);
    boolean existsByJeunePrestateurAndMission(JeunePrestateur jeunePrestateur, Mission mission);
    Optional<Candidature> findByJeunePrestateurAndMission(JeunePrestateur jeunePrestateur, Mission mission);
}
