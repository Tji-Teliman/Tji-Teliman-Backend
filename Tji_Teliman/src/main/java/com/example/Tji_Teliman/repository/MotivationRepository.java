package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Motivation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotivationRepository extends JpaRepository<Motivation, Long> {
    List<Motivation> findByCandidature(Candidature candidature);
}
