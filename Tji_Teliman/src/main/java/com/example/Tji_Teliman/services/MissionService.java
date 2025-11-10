package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Categorie;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.StatutMission;
import com.example.Tji_Teliman.dto.MissionDTO;
import com.example.Tji_Teliman.config.FilePathConverter;
import com.example.Tji_Teliman.repository.CandidatureRepository;
import com.example.Tji_Teliman.repository.CategorieRepository;
import com.example.Tji_Teliman.repository.MissionRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final RecruteurRepository recruteurRepository;
    private final CategorieRepository categorieRepository;
    private final CandidatureRepository candidatureRepository;
    private final NotificationService notificationService;
    private final PaiementService paiementService;
    private final GoogleMapsService googleMapsService;
    private final NotationService notationService;
    private final FilePathConverter filePathConverter;

    public MissionService(MissionRepository missionRepository, RecruteurRepository recruteurRepository, CategorieRepository categorieRepository, CandidatureRepository candidatureRepository, NotificationService notificationService, PaiementService paiementService, GoogleMapsService googleMapsService, NotationService notationService, FilePathConverter filePathConverter) {
        this.missionRepository = missionRepository;
        this.recruteurRepository = recruteurRepository;
        this.categorieRepository = categorieRepository;
        this.candidatureRepository = candidatureRepository;
        this.notificationService = notificationService;
        this.paiementService = paiementService;
        this.googleMapsService = googleMapsService;
        this.notationService = notationService;
        this.filePathConverter = filePathConverter;
    }

    @Transactional
    public Mission create(Long recruteurId, String titre, String description, String exigence, Date dateDebut, Date dateFin, Double remuneration, String categorieNom, String heureDebut, String heureFin, Double latitude, Double longitude, String adresse, String placeId) {
        Recruteur r = recruteurRepository.findById(recruteurId).orElseThrow(() -> new IllegalArgumentException("Recruteur introuvable"));
        Categorie c = categorieRepository.findByNomIgnoreCase(categorieNom).orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        Mission m = new Mission();
        m.setTitre(titre);
        m.setDescription(description);
        m.setExigence(exigence);
        m.setDateDebut(dateDebut);
        m.setDateFin(dateFin);
        // Gestion de la géolocalisation : deux cas possibles
        if (placeId != null && !placeId.isBlank()) {
            // Cas 1: placeId fourni -> enrichir avec les détails Google Maps
            var details = googleMapsService.fetchPlaceDetails(placeId);
            if (details != null) {
                latitude = details.lat();
                longitude = details.lng();
                adresse = details.formattedAddress();
            }
        } else if (latitude != null && longitude != null) {
            // Cas 2: seulement lat/lng fournis -> géocodage inverse pour obtenir placeId et adresse
            var reverseResult = googleMapsService.reverseGeocode(latitude, longitude);
            if (reverseResult != null) {
                placeId = reverseResult.placeId();
                adresse = reverseResult.formattedAddress();
            }
        }
        m.setLatitude(latitude);
        m.setLongitude(longitude);
        m.setAdresse(adresse);
        m.setPlaceId(placeId);
        m.setRemuneration(remuneration);
        m.setDatePublication(new Date());
        if (heureDebut != null && !heureDebut.isEmpty()) m.setHeureDebut(java.time.LocalTime.parse(heureDebut));
        if (heureFin != null && !heureFin.isEmpty()) m.setHeureFin(java.time.LocalTime.parse(heureFin));
        m.setStatut(StatutMission.EN_ATTENTE);
        m.setRecruteur(r);
        m.setCategorie(c);
        return missionRepository.save(m);
    }

    @Transactional(readOnly = true)
    public List<Mission> listAll() {
        return missionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Mission> listEnAttente() {
        return missionRepository.findByStatut(StatutMission.EN_ATTENTE);
    }

    @Transactional(readOnly = true)
    public List<Mission> listByRecruteur(Long recruteurId) {
        return missionRepository.findAll().stream().filter(m -> m.getRecruteur() != null && m.getRecruteur().getId().equals(recruteurId)).toList();
    }

    @Transactional
    public Mission update(Long id, String titre, String description, String exigence, Date dateDebut, Date dateFin, Double remuneration, String categorieNom, StatutMission statut, String heureDebut, String heureFin, Double latitude, Double longitude, String adresse, String placeId) {
        Mission m = missionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        if (titre != null && !titre.trim().isEmpty()) m.setTitre(titre);
        if (description != null && !description.trim().isEmpty()) m.setDescription(description);
        if (exigence != null && !exigence.trim().isEmpty()) m.setExigence(exigence);
        if (dateDebut != null) m.setDateDebut(dateDebut);
        if (dateFin != null) m.setDateFin(dateFin);
        // Gestion de la géolocalisation : deux cas possibles
        if (placeId != null && !placeId.trim().isEmpty()) {
            // Cas 1: placeId fourni -> enrichir avec les détails Google Maps
            m.setPlaceId(placeId);
            var details = googleMapsService.fetchPlaceDetails(placeId);
            if (details != null) {
                latitude = details.lat();
                longitude = details.lng();
                adresse = details.formattedAddress();
            }
        } else if (latitude != null && longitude != null) {
            // Cas 2: seulement lat/lng fournis -> géocodage inverse pour obtenir placeId et adresse
            var reverseResult = googleMapsService.reverseGeocode(latitude, longitude);
            if (reverseResult != null) {
                placeId = reverseResult.placeId();
                adresse = reverseResult.formattedAddress();
                m.setPlaceId(placeId);
            }
        }
        if (latitude != null) m.setLatitude(latitude);
        if (longitude != null) m.setLongitude(longitude);
        if (adresse != null && !adresse.trim().isEmpty()) m.setAdresse(adresse);
        if (remuneration != null) m.setRemuneration(remuneration);
        if (heureDebut != null && !heureDebut.isEmpty()) m.setHeureDebut(java.time.LocalTime.parse(heureDebut));
        if (heureFin != null && !heureFin.isEmpty()) m.setHeureFin(java.time.LocalTime.parse(heureFin));
        if (statut != null) m.setStatut(statut);
        if (categorieNom != null && !categorieNom.trim().isEmpty()) {
            Categorie c = categorieRepository.findByNomIgnoreCase(categorieNom).orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
            m.setCategorie(c);
        }
        return missionRepository.save(m);
    }

    @Transactional(readOnly = true)
    public MissionDTO toDTO(Mission m) {
        MissionDTO dto = new MissionDTO();
        dto.setId(m.getId());
        dto.setTitre(m.getTitre());
        dto.setDescription(m.getDescription());
        dto.setExigence(m.getExigence());
        dto.setDateDebut(m.getDateDebut());
        dto.setDateFin(m.getDateFin());
        dto.setLatitude(m.getLatitude());
        dto.setLongitude(m.getLongitude());
        dto.setAdresse(m.getAdresse());
        dto.setPlaceId(m.getPlaceId());
        dto.setRemuneration(m.getRemuneration());
        dto.setDatePublication(m.getDatePublication());
        dto.setStatut(m.getStatut() == null ? null : m.getStatut().name());
        if (m.getHeureDebut() != null) dto.setHeureDebut(m.getHeureDebut().toString());
        if (m.getHeureFin() != null) dto.setHeureFin(m.getHeureFin().toString());
        dto.setDureJours(m.getDureJours());
        dto.setDureHeures(m.getDureHeures());
        if (m.getCategorie() != null) {
            dto.setCategorieNom(m.getCategorie().getNom());
            dto.setCategorieUrlPhoto(m.getCategorie().getUrlPhoto());
        }
        // Compter le nombre de candidatures pour cette mission
        dto.setNombreCandidatures((long) candidatureRepository.findByMission(m).size());
        
        // Remplir les informations du recruteur
        if (m.getRecruteur() != null) {
            Recruteur recruteur = m.getRecruteur();
            dto.setRecruteurNom(recruteur.getNom());
            dto.setRecruteurPrenom(recruteur.getPrenom());
            dto.setRecruteurUrlPhoto(recruteur.getUrlPhoto());
            // Obtenir la note moyenne du recruteur
            Double moyenneNote = notationService.getMoyenneNotesRecruteur(recruteur.getId());
            dto.setRecruteurNote(moyenneNote);
        }
        
        return dto;
    }

    @Transactional(readOnly = true)
    public MissionDTO getById(Long id) {
        Mission m = missionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        return toDTO(m);
    }

    @Transactional(readOnly = true)
    public Long countCandidaturesByMission(Long missionId) {
        return (long) candidatureRepository.findByMission(missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"))).size();
    }

    @Transactional
    public void delete(Long id) {
        if (!missionRepository.existsById(id)) throw new IllegalArgumentException("Mission introuvable");
        missionRepository.deleteById(id);
    }

    @Transactional
    public void verifierMissionsTerminees() {
        Date now = new Date();
        List<Mission> missions = missionRepository.findAll();
        
        for (Mission mission : missions) {
            if (mission.getStatut() == StatutMission.EN_COURS && mission.getDateFin() != null) {
                // Comparaison à la date (ignorer l'heure) : si dateFin <= aujourd'hui => TERMINEE
                Date fin = truncateToDate(mission.getDateFin());
                Date today = truncateToDate(now);
                if (!fin.after(today)) { // fin <= today
                    mission.setStatut(StatutMission.TERMINEE);
                    missionRepository.save(mission);
                    notificationService.notifyMissionTerminee(mission.getRecruteur(), mission);
                    paiementService.createPendingPaiementsForMission(mission);
                }
            }
        }
    }

    private Date truncateToDate(Date d) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(d);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Terminer une mission (changer le statut à TERMINEE) - Version simple pour test
     */
    @Transactional
    public Mission terminerMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        
        mission.setStatut(StatutMission.TERMINEE);
        Mission savedMission = missionRepository.save(mission);
        
        // Notifier le recruteur que la mission est terminée et qu'il doit effectuer le paiement
        notificationService.notifyMissionTerminee(mission.getRecruteur(), savedMission);
        paiementService.createPendingPaiementsForMission(savedMission);
        
        return savedMission;
    }
}
