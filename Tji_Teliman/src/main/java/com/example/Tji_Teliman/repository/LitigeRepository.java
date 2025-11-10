package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.Litige;
import com.example.Tji_Teliman.entites.enums.StatutLitige;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LitigeRepository extends JpaRepository<Litige, Long> {

    // Trouver les litiges par statut
    List<Litige> findByStatut(StatutLitige statut);

    // Trouver les litiges d'un jeune prestataire
    List<Litige> findByJeunePrestateurId(Long jeunePrestateurId);

    // Trouver les litiges d'un recruteur
    List<Litige> findByRecruteurId(Long recruteurId);

    // Trouver les litiges d'une mission
    List<Litige> findByMissionId(Long missionId);

    //Trouver les litiges non assignés (sans administrateur)
    List<Litige> findByAdministrateurIsNull();

    //Trouver les litiges assignés à un administrateur
    List<Litige> findByAdministrateurId(Long administrateurId);

    //Trouver les litiges par statut et triés par date de création
    List<Litige> findByStatutOrderByDateCreationDesc(StatutLitige statut);

    //Compter les litiges par statut
    long countByStatut(StatutLitige statut);

    //Compter les litiges non assignés
    long countByAdministrateurIsNull();

    List<Litige> findByMissionIdAndJeunePrestateurId(Long missionId, Long jeunePrestateurId);
}
