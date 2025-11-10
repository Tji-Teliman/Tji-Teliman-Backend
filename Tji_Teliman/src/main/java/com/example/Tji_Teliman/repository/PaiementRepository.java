package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    long countByStatutPaiement(StatutPaiement statut);
    long countByDatePaiementBetween(Date start, Date end);
    List<Paiement> findByStatutPaiement(StatutPaiement statut);
}
