package com.example.Tji_Teliman.repository;

import com.example.Tji_Teliman.entites.LitigeMessage;
import com.example.Tji_Teliman.entites.enums.LitigeMessageDestinataire;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LitigeMessageRepository extends JpaRepository<LitigeMessage, Long> {

    List<LitigeMessage> findByLitigeIdOrderByDateEnvoiAsc(Long litigeId);

    List<LitigeMessage> findByLitigeIdAndDestinataireTypeAndEstLuFalse(Long litigeId, LitigeMessageDestinataire destinataireType);

    List<LitigeMessage> findByJeunePrestateurIdOrderByDateEnvoiDesc(Long jeuneId);

    List<LitigeMessage> findByRecruteurIdOrderByDateEnvoiDesc(Long recruteurId);

    List<LitigeMessage> findByAdministrateurIdOrderByDateEnvoiDesc(Long administrateurId);

    @Query("SELECT m FROM LitigeMessage m WHERE m.jeunePrestateur.id = :userId OR m.recruteur.id = :userId OR m.administrateur.id = :userId ORDER BY m.dateEnvoi DESC")
    List<LitigeMessage> findAllConversationsForUser(@Param("userId") Long userId);
}

