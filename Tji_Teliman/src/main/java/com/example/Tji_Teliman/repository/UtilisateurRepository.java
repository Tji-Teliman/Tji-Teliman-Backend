package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    boolean existsByEmail(String email);
    boolean existsByTelephone(String telephone);
    java.util.Optional<Utilisateur> findByTelephone(String telephone);
    java.util.Optional<Utilisateur> findByEmail(String email);
}
