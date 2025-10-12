package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.CompetenceRepository;
import java.io.IOException;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileService {

    private final JeunePrestateurRepository jeuneRepo;
    private final RecruteurRepository recruteurRepo;
    private final FileStorageService storageService;
    private final CompetenceRepository competenceRepository;

    public ProfileService(JeunePrestateurRepository jeuneRepo, RecruteurRepository recruteurRepo, FileStorageService storageService, CompetenceRepository competenceRepository) {
        this.jeuneRepo = jeuneRepo;
        this.recruteurRepo = recruteurRepo;
        this.storageService = storageService;
        this.competenceRepository = competenceRepository;
    }

    @Transactional
    public JeunePrestateur updateJeune(Long id, Date dateNaissance, String localisation, MultipartFile photo, MultipartFile carteIdentiteFile) throws IOException {
        JeunePrestateur j = jeuneRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("JeunePrestateur introuvable"));
        if (dateNaissance != null) j.setDateNaissance(dateNaissance);
        if (localisation != null) j.setLocalisation(localisation);
        if (carteIdentiteFile != null && !carteIdentiteFile.isEmpty()) {
            String pathCI = storageService.store(carteIdentiteFile, "jeunes");
            j.setCarteIdentite(pathCI);
        }
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "jeunes");
            j.setUrlPhoto(path);
        }
        return jeuneRepo.save(j);
    }

    @Transactional
    public JeunePrestateur setCompetencesJeune(Long id, java.util.List<Long> competencesIds) {
        JeunePrestateur j = jeuneRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("JeunePrestateur introuvable"));
        if (competencesIds == null || competencesIds.isEmpty()) {
            j.setCompetences(java.util.List.of());
        } else {
            java.util.List<Competence> comps = competenceRepository.findAllById(competencesIds);
            j.setCompetences(comps);
        }
        return jeuneRepo.save(j);
    }

    @Transactional
    public Recruteur updateRecruteurParticulier(Long id, Date dateNaissance, String profession, String adresse, MultipartFile photo) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        if (r.getTypeRecruteur() != TypeRecruteur.PARTICULIER) {
            throw new IllegalArgumentException("Type du recruteur non PARTICULIER");
        }
        if (dateNaissance != null) r.setDateNaissance(dateNaissance);
        if (profession != null) r.setProfession(profession);
        if (adresse != null) r.setAdresse(adresse);
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        return recruteurRepo.save(r);
    }

    @Transactional
    public Recruteur updateRecruteurEntreprise(Long id, String nomEntreprise, String secteurActivite, String emailEntreprise, String siteWeb, MultipartFile photo) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        if (r.getTypeRecruteur() != TypeRecruteur.ENTREPRISE) {
            throw new IllegalArgumentException("Type du recruteur non ENTREPRISE");
        }
        if (nomEntreprise != null) r.setNomEntreprise(nomEntreprise);
        if (secteurActivite != null) r.setSecteurActivite(secteurActivite);
        if (emailEntreprise != null) r.setEmailEntreprise(emailEntreprise);
        if (siteWeb != null) r.setSiteWeb(siteWeb);
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        return recruteurRepo.save(r);
    }
}


