package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Competence;
import com.example.Tji_Teliman.entites.JeuneCompetence;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.CompetenceRepository;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import com.example.Tji_Teliman.dto.JeunePrestateurProfileDTO;
import com.example.Tji_Teliman.dto.RecruteurProfileDTO;
import com.example.Tji_Teliman.dto.UserProfileDTO;
import com.example.Tji_Teliman.dto.NotationMoyenneDTO;
import com.example.Tji_Teliman.entites.Notation;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class
ProfileService {

    private final JeunePrestateurRepository jeuneRepo;
    private final RecruteurRepository recruteurRepo;
    private final FileStorageService storageService;
    private final CompetenceRepository competenceRepository;
    private final CandidatureService candidatureService;
    private final GoogleMapsService googleMapsService;
    private final NotationService notationService;
    private final UtilisateurRepository utilisateurRepository;

    public ProfileService(JeunePrestateurRepository jeuneRepo, RecruteurRepository recruteurRepo, FileStorageService storageService, CompetenceRepository competenceRepository, CandidatureService candidatureService, GoogleMapsService googleMapsService, NotationService notationService, UtilisateurRepository utilisateurRepository) {
        this.jeuneRepo = jeuneRepo;
        this.recruteurRepo = recruteurRepo;
        this.storageService = storageService;
        this.competenceRepository = competenceRepository;
        this.candidatureService = candidatureService;
        this.googleMapsService = googleMapsService;
        this.notationService = notationService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public JeunePrestateur updateJeune(Long id, String nom, String prenom, String telephone, String email, Date dateNaissance, String adresse, MultipartFile photo, MultipartFile carteIdentiteFile) throws IOException {
        JeunePrestateur j = jeuneRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("JeunePrestateur introuvable"));
        
        // Mise à jour du nom
        if (nom != null && !nom.trim().isEmpty()) {
            j.setNom(nom.trim());
        }
        
        // Mise à jour du prénom
        if (prenom != null && !prenom.trim().isEmpty()) {
            j.setPrenom(prenom.trim());
        }
        
        // Mise à jour du téléphone avec validation
        if (telephone != null && !telephone.trim().isEmpty()) {
            String tel = telephone.trim();
            if (!tel.matches("^\\d{8,}$")) {
                throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByTelephone(tel);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
            }
            j.setTelephone(tel);
        }
        
        // Mise à jour de l'email avec validation
        if (email != null && !email.trim().isEmpty()) {
            String emailTrimmed = email.trim();
            if (!emailTrimmed.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email doit se terminer par @gmail.com");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByEmail(emailTrimmed);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }
            j.setEmail(emailTrimmed);
        }
        
        if (dateNaissance != null) j.setDateNaissance(dateNaissance);
        if (adresse != null && !adresse.trim().isEmpty()) j.setAdresse(adresse);
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
    public JeunePrestateur updateJeuneAdresse(Long id, String adresse) {
        JeunePrestateur j = jeuneRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("JeunePrestateur introuvable"));
        if (adresse != null && !adresse.trim().isEmpty()) {
            j.setAdresse(adresse);
        }
        return jeuneRepo.save(j);
    }

    @Transactional
    public Recruteur updateRecruteurAdresse(Long id, String adresse) {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        if (adresse != null && !adresse.trim().isEmpty()) {
            r.setAdresse(adresse);
        }
        return recruteurRepo.save(r);
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
        dto.setStatut(j.getStatut().name());
        dto.setDateCreation(j.getDateCreation());
        dto.setDateNaissance(j.getDateNaissance());
        dto.setLocalisation(j.getAdresse()); // utiliser adresse au lieu de localisation
        dto.setAdresse(j.getAdresse());
        dto.setUrlPhoto(j.getUrlPhoto());
        dto.setCarteIdentite(j.getCarteIdentite());
        if (j.getCompetences() != null) {
            dto.setCompetences(j.getCompetences().stream()
                .filter(jc -> jc.getCompetence() != null)
                .map(jc -> jc.getCompetence().getNom())
                .collect(Collectors.toList()));
        }
        // Calculer la moyenne des notations reçues par le jeune
        Double moyenneNotes = notationService.getMoyenneNotesJeune(j.getId());
        if (moyenneNotes != null) {
            List<Notation> notationsRecues = notationService.getNotationsRecuesParJeune(j.getId());
            NotationMoyenneDTO notationMoyenne = new NotationMoyenneDTO(moyenneNotes, notationsRecues.size());
            dto.setNotationMoyenne(notationMoyenne);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public RecruteurProfileDTO toProfileDTO(Recruteur r) {
        RecruteurProfileDTO dto = new RecruteurProfileDTO();
        dto.setId(r.getId());
        dto.setNom(r.getNom());
        dto.setPrenom(r.getPrenom());
        dto.setEmail(r.getEmail());
        dto.setTelephone(r.getTelephone());
        dto.setRole(r.getRole().name());
        dto.setGenre(r.getGenre().name());
        dto.setStatut(r.getStatut().name());
        dto.setDateCreation(r.getDateCreation());
        dto.setTypeRecruteur(r.getTypeRecruteur() == null ? null : r.getTypeRecruteur().name());
        dto.setDateNaissance(r.getDateNaissance());
        dto.setProfession(r.getProfession());
        dto.setAdresse(r.getAdresse());
        dto.setUrlPhoto(r.getUrlPhoto());
        dto.setCarteIdentite(r.getCarteIdentite());
        dto.setNomEntreprise(r.getNomEntreprise());
        dto.setSecteurActivite(r.getSecteurActivite());
        dto.setEmailEntreprise(r.getEmailEntreprise());
        dto.setSiteWeb(r.getSiteWeb());
        return dto;
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long userId) {
        // Essayer JeunePrestateur
        var jeuneOpt = jeuneRepo.findById(userId);
        if (jeuneOpt.isPresent()) {
            JeunePrestateur j = jeuneOpt.get();
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(j.getId());
            dto.setNom(j.getNom());
            dto.setPrenom(j.getPrenom());
            dto.setRole(j.getRole().name());
            dto.setEmail(j.getEmail());
            dto.setTelephone(j.getTelephone());
            dto.setLocalisation(j.getAdresse()); // utiliser adresse au lieu de localisation
            if (j.getCompetences() != null) {
                dto.setCompetences(j.getCompetences().stream()
                    .filter(jc -> jc.getCompetence() != null)
                    .map(jc -> jc.getCompetence().getNom())
                    .toList());
            }
            dto.setNombreMissionsAccomplies(candidatureService.getNombreMissionsAccompliesByJeune(userId));
            
            // Calculer la moyenne des notations reçues par le jeune
            Double moyenneNotes = notationService.getMoyenneNotesJeune(userId);
            if (moyenneNotes != null) {
                List<Notation> notationsRecues = notationService.getNotationsRecuesParJeune(userId);
                NotationMoyenneDTO notationMoyenne = new NotationMoyenneDTO(moyenneNotes, notationsRecues.size());
                dto.setNotationMoyenne(notationMoyenne);
            }
            
            return dto;
        }
        // Essayer Recruteur
        var recOpt = recruteurRepo.findById(userId);
        if (recOpt.isPresent()) {
            var r = recOpt.get();
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(r.getId());
            dto.setNom(r.getNom());
            dto.setPrenom(r.getPrenom());
            dto.setRole(r.getRole().name());
            dto.setEmail(r.getEmail());
            dto.setTelephone(r.getTelephone());
            // Utiliser l'adresse du recruteur comme localisation
            dto.setLocalisation(r.getAdresse());
            dto.setCompetences(null);
            dto.setNombreMissionsPubliees(r.getMissions() == null ? 0L : (long) r.getMissions().size());
            dto.setNombreMissionsAccomplies(null);
            
            // Calculer la moyenne des notations reçues par le recruteur
            Double moyenneNotes = notationService.getMoyenneNotesRecruteur(userId);
            if (moyenneNotes != null) {
                List<Notation> notationsRecues = notationService.getNotationsRecuesParRecruteur(userId);
                NotationMoyenneDTO notationMoyenne = new NotationMoyenneDTO(moyenneNotes, notationsRecues.size());
                dto.setNotationMoyenne(notationMoyenne);
            }
            
            return dto;
        }
        throw new IllegalArgumentException("Utilisateur introuvable");
    }

    @Transactional
    public Recruteur updateRecruteurParticulier(Long id, Date dateNaissance, String profession, String adresse, MultipartFile photo) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        if (r.getTypeRecruteur() != TypeRecruteur.PARTICULIER) {
            throw new IllegalArgumentException("Type du recruteur non PARTICULIER");
        }
        if (dateNaissance != null) r.setDateNaissance(dateNaissance);
        if (profession != null && !profession.trim().isEmpty()) r.setProfession(profession);
        if (adresse != null && !adresse.trim().isEmpty()) r.setAdresse(adresse);
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

    /**
     * Mettre à jour un recruteur particulier en définissant le type si nécessaire
     */
    @Transactional
    public Recruteur updateRecruteurParticulierWithType(Long id, Date dateNaissance, String profession, String adresse, MultipartFile photo) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        
        // Définir le type si ce n'est pas déjà fait
        if (r.getTypeRecruteur() == null) {
            r.setTypeRecruteur(TypeRecruteur.PARTICULIER);
        }
        
        if (dateNaissance != null) r.setDateNaissance(dateNaissance);
        if (profession != null && !profession.trim().isEmpty()) r.setProfession(profession);
        if (adresse != null && !adresse.trim().isEmpty()) r.setAdresse(adresse);
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        return recruteurRepo.save(r);
    }

    /**
     * Mettre à jour un recruteur entreprise en définissant le type si nécessaire
     */
    @Transactional
    public Recruteur updateRecruteurEntrepriseWithType(Long id, String nomEntreprise, String secteurActivite, String emailEntreprise, String siteWeb, MultipartFile photo) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        
        // Définir le type si ce n'est pas déjà fait
        if (r.getTypeRecruteur() == null) {
            r.setTypeRecruteur(TypeRecruteur.ENTREPRISE);
        }
        
        System.out.println("DEBUG Service - Valeurs reçues:");
        System.out.println("nomEntreprise: " + nomEntreprise);
        System.out.println("secteurActivite: " + secteurActivite);
        System.out.println("emailEntreprise: " + emailEntreprise);
        System.out.println("siteWeb: " + siteWeb);
        
        if (nomEntreprise != null && !nomEntreprise.trim().isEmpty()) {
            r.setNomEntreprise(nomEntreprise);
            System.out.println("DEBUG - nomEntreprise défini: " + nomEntreprise);
        }
        if (secteurActivite != null && !secteurActivite.trim().isEmpty()) {
            r.setSecteurActivite(secteurActivite);
            System.out.println("DEBUG - secteurActivite défini: " + secteurActivite);
        }
        if (emailEntreprise != null && !emailEntreprise.trim().isEmpty()) {
            r.setEmailEntreprise(emailEntreprise);
            System.out.println("DEBUG - emailEntreprise défini: " + emailEntreprise);
        }
        if (siteWeb != null && !siteWeb.trim().isEmpty()) {
            r.setSiteWeb(siteWeb);
            System.out.println("DEBUG - siteWeb défini: " + siteWeb);
        }
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        return recruteurRepo.save(r);
    }

    /**
     * Mettre à jour un recruteur entreprise avec adresse en une seule opération
     */
    @Transactional
    public Recruteur updateRecruteurEntrepriseWithTypeAndAdresse(Long id, String nom, String prenom, String telephone, String email, String nomEntreprise, String secteurActivite, String emailEntreprise, String siteWeb, MultipartFile photo, MultipartFile carteIdentite, String adresse) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        
        // Définir le type si ce n'est pas déjà fait
        if (r.getTypeRecruteur() == null) {
            r.setTypeRecruteur(TypeRecruteur.ENTREPRISE);
        }
        
        // Mise à jour du nom
        if (nom != null && !nom.trim().isEmpty()) {
            r.setNom(nom.trim());
        }
        
        // Mise à jour du prénom
        if (prenom != null && !prenom.trim().isEmpty()) {
            r.setPrenom(prenom.trim());
        }
        
        // Mise à jour du téléphone avec validation
        if (telephone != null && !telephone.trim().isEmpty()) {
            String tel = telephone.trim();
            if (!tel.matches("^\\d{8,}$")) {
                throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByTelephone(tel);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
            }
            r.setTelephone(tel);
        }
        
        // Mise à jour de l'email avec validation
        if (email != null && !email.trim().isEmpty()) {
            String emailTrimmed = email.trim();
            if (!emailTrimmed.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email doit se terminer par @gmail.com");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByEmail(emailTrimmed);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }
            r.setEmail(emailTrimmed);
        }
        
        System.out.println("DEBUG Service - Valeurs reçues:");
        System.out.println("nomEntreprise: " + nomEntreprise);
        System.out.println("secteurActivite: " + secteurActivite);
        System.out.println("emailEntreprise: " + emailEntreprise);
        System.out.println("siteWeb: " + siteWeb);
        
        if (nomEntreprise != null && !nomEntreprise.trim().isEmpty()) {
            r.setNomEntreprise(nomEntreprise);
            System.out.println("DEBUG - nomEntreprise défini: " + nomEntreprise);
        }
        if (secteurActivite != null && !secteurActivite.trim().isEmpty()) {
            r.setSecteurActivite(secteurActivite);
            System.out.println("DEBUG - secteurActivite défini: " + secteurActivite);
        }
        if (emailEntreprise != null && !emailEntreprise.trim().isEmpty()) {
            r.setEmailEntreprise(emailEntreprise);
            System.out.println("DEBUG - emailEntreprise défini: " + emailEntreprise);
        }
        if (siteWeb != null && !siteWeb.trim().isEmpty()) {
            r.setSiteWeb(siteWeb);
            System.out.println("DEBUG - siteWeb défini: " + siteWeb);
        }
        if (adresse != null && !adresse.trim().isEmpty()) {
            r.setAdresse(adresse);
        }
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        if (carteIdentite != null && !carteIdentite.isEmpty()) {
            String pathCI = storageService.store(carteIdentite, "recruteurs");
            r.setCarteIdentite(pathCI);
        }
        
        return recruteurRepo.save(r);
    }

    /**
     * Mettre à jour un recruteur particulier avec adresse en une seule opération
     */
    @Transactional
    public Recruteur updateRecruteurParticulierWithTypeAndAdresse(Long id, String nom, String prenom, String telephone, String email, Date dateNaissance, String profession, String adresse, MultipartFile photo, MultipartFile carteIdentite) throws IOException {
        Recruteur r = recruteurRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        
        // Définir le type si ce n'est pas déjà fait
        if (r.getTypeRecruteur() == null) {
            r.setTypeRecruteur(TypeRecruteur.PARTICULIER);
        }
        
        // Mise à jour du nom
        if (nom != null && !nom.trim().isEmpty()) {
            r.setNom(nom.trim());
        }
        
        // Mise à jour du prénom
        if (prenom != null && !prenom.trim().isEmpty()) {
            r.setPrenom(prenom.trim());
        }
        
        // Mise à jour du téléphone avec validation
        if (telephone != null && !telephone.trim().isEmpty()) {
            String tel = telephone.trim();
            if (!tel.matches("^\\d{8,}$")) {
                throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByTelephone(tel);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
            }
            r.setTelephone(tel);
        }
        
        // Mise à jour de l'email avec validation
        if (email != null && !email.trim().isEmpty()) {
            String emailTrimmed = email.trim();
            if (!emailTrimmed.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email doit se terminer par @gmail.com");
            }
            // Vérifier l'unicité (sauf pour l'utilisateur actuel)
            var existingUser = utilisateurRepository.findByEmail(emailTrimmed);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }
            r.setEmail(emailTrimmed);
        }
        
        if (dateNaissance != null) r.setDateNaissance(dateNaissance);
        if (profession != null && !profession.trim().isEmpty()) r.setProfession(profession);
        if (adresse != null && !adresse.trim().isEmpty()) r.setAdresse(adresse);
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "recruteurs");
            r.setUrlPhoto(path);
        }
        if (carteIdentite != null && !carteIdentite.isEmpty()) {
            String pathCI = storageService.store(carteIdentite, "recruteurs");
            r.setCarteIdentite(pathCI);
        }
        
        return recruteurRepo.save(r);
    }
    /**
     * Retourne le profil complet selon le rôle (jeune ou recruteur)
     */
    @Transactional(readOnly = true)
    public Object getFullUserProfile(Long userId) {
        var jeuneOpt = jeuneRepo.findById(userId);
        if (jeuneOpt.isPresent()) {
            return toProfileDTO(jeuneOpt.get());
        }
        var recOpt = recruteurRepo.findById(userId);
        if (recOpt.isPresent()) {
            return toProfileDTO(recOpt.get());
        }
        throw new IllegalArgumentException("Utilisateur introuvable");
    }
}
