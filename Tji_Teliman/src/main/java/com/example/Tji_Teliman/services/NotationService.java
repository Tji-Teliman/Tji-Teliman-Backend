package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Candidature;
import com.example.Tji_Teliman.entites.Notation;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.dto.NotationDTO;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.NotationRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotationService {

    private final NotationRepository notationRepository;
    private final CandidatureRepository candidatureRepository;
    private final RecruteurRepository recruteurRepository;
    private final JeunePrestateurRepository jeunePrestateurRepository;
    private final NotificationService notificationService;

    public NotationService(NotationRepository notationRepository, CandidatureRepository candidatureRepository, 
                          RecruteurRepository recruteurRepository, JeunePrestateurRepository jeunePrestateurRepository,
                          NotificationService notificationService) {
        this.notationRepository = notationRepository;
        this.candidatureRepository = candidatureRepository;
        this.recruteurRepository = recruteurRepository;
        this.jeunePrestateurRepository = jeunePrestateurRepository;
        this.notificationService = notificationService;
    }

    /**
     * Créer une notation du recruteur vers le jeune prestataire
     * Se déclenche automatiquement après un paiement
     */
    @Transactional
    public Notation noterJeuneParRecruteur(Long candidatureId, Integer note, String commentaire) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        // Vérifier qu'il n'y a pas déjà une notation du recruteur pour cette candidature
        boolean notationRecruteurExiste = candidature.getNotations() != null && 
            candidature.getNotations().stream()
                .anyMatch(n -> n.isInitieParRecruteur());
        
        if (notationRecruteurExiste) {
            throw new IllegalArgumentException("Une notation du recruteur a déjà été effectuée pour cette candidature");
        }

        // Vérifier que la candidature est acceptée et la mission terminée
        if (candidature.getStatut() != com.example.Tji_Teliman.entites.enums.StatutCandidature.ACCEPTEE) {
            throw new IllegalArgumentException("La notation n'est possible que pour une candidature acceptée");
        }

        if (candidature.getMission().getStatut() != com.example.Tji_Teliman.entites.enums.StatutMission.TERMINEE) {
            throw new IllegalArgumentException("La notation n'est possible que pour une mission terminée");
        }

        // Vérifier qu'un paiement a été effectué
        if (candidature.getPaiement() == null) {
            throw new IllegalArgumentException("La notation n'est possible qu'après un paiement effectué");
        }

        Notation notation = new Notation();
        notation.setRecruteur(candidature.getRecruteurValidateur());
        notation.setJeunePrestateur(candidature.getJeunePrestateur());
        notation.setInitieParRecruteur(true);
        notation.setNote(note);
        notation.setCommentaire(commentaire);
        notation.setDateNotation(new Date());
        notation.setCandidature(candidature);

        Notation saved = notationRepository.save(notation);

        // Notifier le jeune prestataire qu'il a été noté
        notificationService.notifyNotationRecue(candidature.getJeunePrestateur(), saved);

        // Déclencher automatiquement la demande de notation du jeune vers le recruteur
        notificationService.notifyDemandeNotationJeune(candidature.getJeunePrestateur(), candidature);

        return saved;
    }

    /**
     * Créer une notation du jeune prestataire vers le recruteur
     * Peut être effectuée après que le recruteur ait noté le jeune
     */
    @Transactional
    public Notation noterRecruteurParJeune(Long candidatureId, Integer note, String commentaire) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));

        // Vérifier qu'il n'y a pas déjà une notation du jeune pour cette candidature
        boolean notationJeuneExiste = candidature.getNotations() != null && 
            candidature.getNotations().stream()
                .anyMatch(n -> !n.isInitieParRecruteur());
        
        if (notationJeuneExiste) {
            throw new IllegalArgumentException("Une notation du jeune a déjà été effectuée pour cette candidature");
        }

        // Vérifier que la candidature est acceptée et la mission terminée
        if (candidature.getStatut() != com.example.Tji_Teliman.entites.enums.StatutCandidature.ACCEPTEE) {
            throw new IllegalArgumentException("La notation n'est possible que pour une candidature acceptée");
        }

        if (candidature.getMission().getStatut() != com.example.Tji_Teliman.entites.enums.StatutMission.TERMINEE) {
            throw new IllegalArgumentException("La notation n'est possible que pour une mission terminée");
        }

        // Vérifier qu'un paiement a été effectué
        if (candidature.getPaiement() == null) {
            throw new IllegalArgumentException("La notation n'est possible qu'après un paiement effectué");
        }

        Notation notation = new Notation();
        notation.setRecruteur(candidature.getRecruteurValidateur());
        notation.setJeunePrestateur(candidature.getJeunePrestateur());
        notation.setInitieParRecruteur(false);
        notation.setNote(note);
        notation.setCommentaire(commentaire);
        notation.setDateNotation(new Date());
        notation.setCandidature(candidature);

        Notation saved = notationRepository.save(notation);

        // Notifier le recruteur qu'il a été noté
        notificationService.notifyNotationRecue(candidature.getRecruteurValidateur(), saved);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notation> getAllNotations() {
        return notationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Notation> getNotationById(Long id) {
        return notationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Notation> getNotationsByCandidature(Long candidatureId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getCandidature() != null && n.getCandidature().getId().equals(candidatureId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Notation> getNotationRecruteurByCandidature(Long candidatureId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getCandidature() != null && n.getCandidature().getId().equals(candidatureId) && n.isInitieParRecruteur())
                .findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<Notation> getNotationJeuneByCandidature(Long candidatureId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getCandidature() != null && n.getCandidature().getId().equals(candidatureId) && !n.isInitieParRecruteur())
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Notation> getNotationsByRecruteur(Long recruteurId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getRecruteur() != null && n.getRecruteur().getId().equals(recruteurId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Notation> getNotationsByJeunePrestateur(Long jeuneId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getJeunePrestateur() != null && n.getJeunePrestateur().getId().equals(jeuneId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Notation> getNotationsRecuesParRecruteur(Long recruteurId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getRecruteur() != null && n.getRecruteur().getId().equals(recruteurId) && !n.isInitieParRecruteur())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Notation> getNotationsRecuesParJeune(Long jeuneId) {
        return notationRepository.findAll().stream()
                .filter(n -> n.getJeunePrestateur() != null && n.getJeunePrestateur().getId().equals(jeuneId) && n.isInitieParRecruteur())
                .toList();
    }

    @Transactional(readOnly = true)
    public Double getMoyenneNotesRecruteur(Long recruteurId) {
        List<Notation> notations = getNotationsRecuesParRecruteur(recruteurId);
        if (notations.isEmpty()) return null;
        
        double somme = notations.stream().mapToInt(Notation::getNote).sum();
        return somme / notations.size();
    }

    @Transactional(readOnly = true)
    public Double getMoyenneNotesJeune(Long jeuneId) {
        List<Notation> notations = getNotationsRecuesParJeune(jeuneId);
        if (notations.isEmpty()) return null;
        
        double somme = notations.stream().mapToInt(Notation::getNote).sum();
        return somme / notations.size();
    }

    @Transactional(readOnly = true)
    public NotationDTO toDTO(Notation n) {
        NotationDTO dto = new NotationDTO();
        dto.setId(n.getId());
        dto.setNote(n.getNote());
        dto.setCommentaire(n.getCommentaire());
        dto.setDateNotation(n.getDateNotation());
        dto.setInitieParRecruteur(n.isInitieParRecruteur());
        
        if (n.getCandidature() != null) {
            dto.setCandidatureId(n.getCandidature().getId());
            
            if (n.getCandidature().getMission() != null) {
                dto.setMissionId(n.getCandidature().getMission().getId());
                dto.setMissionTitre(n.getCandidature().getMission().getTitre());
            }
        }
        
        if (n.getRecruteur() != null) {
            dto.setRecruteurId(n.getRecruteur().getId());
            dto.setRecruteurNom(n.getRecruteur().getNom());
            dto.setRecruteurPrenom(n.getRecruteur().getPrenom());
        }
        
        if (n.getJeunePrestateur() != null) {
            dto.setJeunePrestateurId(n.getJeunePrestateur().getId());
            dto.setJeunePrestateurNom(n.getJeunePrestateur().getNom());
            dto.setJeunePrestateurPrenom(n.getJeunePrestateur().getPrenom());
        }
        
        return dto;
    }

    /**
     * Méthode unifiée pour obtenir les notations reçues par un utilisateur (jeune ou recruteur)
     */
    @Transactional(readOnly = true)
    public List<Notation> getNotationsRecuesParUtilisateur(Long userId) {
        // Vérifier si c'est un jeune prestataire
        Optional<JeunePrestateur> jeuneOpt = jeunePrestateurRepository.findById(userId);
        if (jeuneOpt.isPresent()) {
            return getNotationsRecuesParJeune(userId);
        }
        
        // Vérifier si c'est un recruteur
        Optional<Recruteur> recruteurOpt = recruteurRepository.findById(userId);
        if (recruteurOpt.isPresent()) {
            return getNotationsRecuesParRecruteur(userId);
        }
        
        throw new IllegalArgumentException("Utilisateur introuvable ou type non supporté");
    }

    /**
     * Méthode unifiée pour calculer la moyenne des notations reçues par un utilisateur (jeune ou recruteur)
     */
    @Transactional(readOnly = true)
    public Double getMoyenneNotesUtilisateur(Long userId) {
        // Vérifier si c'est un jeune prestataire
        Optional<JeunePrestateur> jeuneOpt = jeunePrestateurRepository.findById(userId);
        if (jeuneOpt.isPresent()) {
            return getMoyenneNotesJeune(userId);
        }
        
        // Vérifier si c'est un recruteur
        Optional<Recruteur> recruteurOpt = recruteurRepository.findById(userId);
        if (recruteurOpt.isPresent()) {
            return getMoyenneNotesRecruteur(userId);
        }
        
        throw new IllegalArgumentException("Utilisateur introuvable ou type non supporté");
    }
}
