package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    Optional<Categorie> findByNomIgnoreCase(String nom);
}
