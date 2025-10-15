package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 🔍 Liste des messages entre un recruteur et un jeune prestataire
    List<Message> findAllByRecruteurIdAndJeunePrestateurIdOrderByDateMessageAsc(Long recruteurId, Long jeunePrestateurId);
}
