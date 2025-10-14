package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Categorie;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.StatutMission;
import com.example.Tji_Teliman.dto.MissionDTO;
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
    private final NotificationService notificationService;
    private final GoogleMapsService googleMapsService;

    public MissionService(MissionRepository missionRepository, RecruteurRepository recruteurRepository, CategorieRepository categorieRepository, NotificationService notificationService, GoogleMapsService googleMapsService) {
        this.missionRepository = missionRepository;
        this.recruteurRepository = recruteurRepository;
        this.categorieRepository = categorieRepository;
        this.notificationService = notificationService;
        this.googleMapsService = googleMapsService;
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
        // Si placeId fourni et clé présente, enrichir via Google (écrase les valeurs fournies)
        if (placeId != null && !placeId.isBlank()) {
            var details = googleMapsService.fetchPlaceDetails(placeId);
            if (details != null) {
                // Toujours utiliser les valeurs de Google Maps si placeId fourni
                latitude = details.lat();
                longitude = details.lng();
                adresse = details.formattedAddress();
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
        if (placeId != null && !placeId.trim().isEmpty()) {
            m.setPlaceId(placeId);
            var details = googleMapsService.fetchPlaceDetails(placeId);
            if (details != null) {
                // Toujours utiliser les valeurs de Google Maps si placeId fourni
                latitude = details.lat();
                longitude = details.lng();
                adresse = details.formattedAddress();
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
        if (m.getRecruteur() != null) {
            dto.setRecruteurId(m.getRecruteur().getId());
            dto.setRecruteurNom(m.getRecruteur().getNom());
            dto.setRecruteurPrenom(m.getRecruteur().getPrenom());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public MissionDTO getById(Long id) {
        Mission m = missionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));
        return toDTO(m);
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
}
