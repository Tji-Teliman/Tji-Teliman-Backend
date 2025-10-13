package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Notification;
import com.example.Tji_Teliman.entites.Utilisateur;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireOrderByDateCreationDesc(Utilisateur destinataire);
}
