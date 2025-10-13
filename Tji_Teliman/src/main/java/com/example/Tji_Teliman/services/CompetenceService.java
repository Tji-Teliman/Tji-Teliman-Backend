package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.entites.Administrateur;
import com.example.Tji_Teliman.repository.CompetenceRepository;
import com.example.Tji_Teliman.repository.AdministrateurRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompetenceService {

    private final CompetenceRepository competenceRepository;
    private final AdministrateurRepository administrateurRepository;

    public CompetenceService(CompetenceRepository competenceRepository, AdministrateurRepository administrateurRepository) {
        this.competenceRepository = competenceRepository;
        this.administrateurRepository = administrateurRepository;
    }

    @Transactional
    public Competence create(String nom, Long administrateurId) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom de la compétence requis");
        }
        if (administrateurId == null) {
            throw new IllegalArgumentException("AdministrateurId requis");
        }
        Administrateur admin = administrateurRepository.findById(administrateurId)
            .orElseThrow(() -> new IllegalArgumentException("Administrateur introuvable"));
        Competence c = new Competence();
        c.setNom(nom.trim());
        c.setAdministrateur(admin);
        return competenceRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<Competence> listAll() {
        return competenceRepository.findAll();
    }

    @Transactional
    public Competence update(Long id, String nom) {
        Competence c = competenceRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Compétence introuvable"));
        if (nom != null && !nom.trim().isEmpty()) c.setNom(nom.trim());
        return competenceRepository.save(c);
    }

    @Transactional
    public void delete(Long id) {
        if (!competenceRepository.existsById(id)) throw new IllegalArgumentException("Compétence introuvable");
        competenceRepository.deleteById(id);
    }
}
