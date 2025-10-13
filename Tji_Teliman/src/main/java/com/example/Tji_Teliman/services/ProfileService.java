package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.entites.JeuneCompetence;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.CompetenceRepository;
import com.example.Tji_Teliman.dto.JeunePrestateurProfileDTO;
import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.dto.NotationDTO;
import com.example.Tji_Teliman.dto.CandidatureDTO;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;
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
    public JeunePrestateur setCompetencesJeune(Long id, java.util.List<?> competences) {
        JeunePrestateur j = jeuneRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("JeunePrestateur introuvable"));
        if (competences == null || competences.isEmpty()) {
            j.setCompetences(java.util.List.of());
            return jeuneRepo.save(j);
        }
        var list = new java.util.ArrayList<JeuneCompetence>();
        for (Object entry : competences) {
            Competence c = null;
            if (entry instanceof Number) {
                Long idComp = ((Number) entry).longValue();
                c = competenceRepository.findById(idComp).orElse(null);
            } else if (entry instanceof String s) {
                c = competenceRepository.findByNomIgnoreCase(s.trim()).orElse(null);
            }
            if (c != null) {
                JeuneCompetence jc = new JeuneCompetence();
                jc.setJeunePrestateur(j);
                jc.setCompetence(c);
                list.add(jc);
            }
        }
        // Réinitialiser proprement et réattacher
        j.getCompetences().clear();
        j.getCompetences().addAll(list);
        return jeuneRepo.save(j);
    }

    @Transactional(readOnly = true)
    public JeunePrestateurProfileDTO toProfileDTO(JeunePrestateur j) {
        JeunePrestateurProfileDTO dto = new JeunePrestateurProfileDTO();
        dto.setId(j.getId());
        dto.setNom(j.getNom());
        dto.setPrenom(j.getPrenom());
        dto.setEmail(j.getEmail());
        dto.setTelephone(j.getTelephone());
        dto.setRole(j.getRole().name());
        dto.setGenre(j.getGenre().name());
        dto.setDateCreation(j.getDateCreation());
        dto.setDateNaissance(j.getDateNaissance());
        dto.setLocalisation(j.getLocalisation());
        dto.setUrlPhoto(j.getUrlPhoto());
        dto.setCarteIdentite(j.getCarteIdentite());
        if (j.getCompetences() != null) {
            dto.setCompetences(j.getCompetences().stream()
                .filter(jc -> jc.getCompetence() != null)
                .map(jc -> jc.getCompetence().getNom())
                .collect(Collectors.toList()));
        }
        if (j.getMessages() != null) {
            dto.setMessages(j.getMessages().stream().map(m -> {
                MessageDTO md = new MessageDTO();
                md.setId(m.getId());
                md.setContenu(m.getContenu());
                md.setDateMessage(m.getDateMessage());
                md.setEnvoyeParRecruteur(m.isEnvoyeParRecruteur());
                return md;
            }).toList());
        }
        if (j.getNotations() != null) {
            dto.setNotations(j.getNotations().stream().map(n -> {
                NotationDTO nd = new NotationDTO();
                nd.setId(n.getId());
                nd.setNote(n.getNote());
                nd.setCommentaire(n.getCommentaire());
                nd.setDateNotation(n.getDateNotation());
                return nd;
            }).toList());
        }
        if (j.getCandidatures() != null) {
            dto.setCandidatures(j.getCandidatures().stream().map(c -> {
                CandidatureDTO cd = new CandidatureDTO();
                cd.setId(c.getId());
                cd.setStatut(c.getStatut().name());
                cd.setDateSoumission(c.getDateSoumission());
                return cd;
            }).toList());
        }
        return dto;
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


