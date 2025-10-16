package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 🔍 Liste des messages entre un recruteur et un jeune prestataire
    List<Message> findAllByRecruteurIdAndJeunePrestateurIdOrderByDateMessageAsc(Long recruteurId, Long jeunePrestateurId);

    // 🔍 Trouver toutes les conversations d'un utilisateur (recruteur ou jeune)
    @Query("SELECT m FROM Message m WHERE m.recruteur.id = :userId OR m.jeunePrestateur.id = :userId ORDER BY m.dateMessage DESC")
    List<Message> findConversationsByUserId(@Param("userId") Long userId);
}
