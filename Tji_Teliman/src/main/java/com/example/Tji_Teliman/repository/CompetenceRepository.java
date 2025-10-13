package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompetenceRepository extends JpaRepository<Competence, Long> {
  Optional<Competence> findByNomIgnoreCase(String nom);
}
