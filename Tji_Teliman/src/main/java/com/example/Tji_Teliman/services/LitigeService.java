package com.example.Tji_Teliman.services;



import com.example.Tji_Teliman.dto.*;
import com.example.Tji_Teliman.entites.*;
import com.example.Tji_Teliman.entites.enums.StatutLitige;
import com.example.Tji_Teliman.entites.enums.TypeLitige;
import com.example.Tji_Teliman.mapper.LitigeMapper;
import com.example.Tji_Teliman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LitigeService {

    @Autowired
    private LitigeRepository litigeRepository;

    @Autowired
    private JeunePrestateurRepository jeunePrestateurRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private LitigeMapper litigeMapper;

    //  CRÉER UN LITIGE
    public LitigeDTO creerLitige(CreationLitigeDTO dto) {
        JeunePrestateur jeune = jeunePrestateurRepository.findById(dto.getJeunePrestateurId())
                .orElseThrow(() -> new RuntimeException("Jeune prestataire non trouvé"));
        Recruteur recruteur = recruteurRepository.findById(dto.getRecruteurId())
                .orElseThrow(() -> new RuntimeException("Recruteur non trouvé"));
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        Litige litige = new Litige();
        litige.setTitre(dto.getTitre());
        litige.setDescription(dto.getDescription());
        litige.setType(TypeLitige.valueOf(dto.getType()));
        litige.setStatut(StatutLitige.OUVERT);
        litige.setJeunePrestateur(jeune);
        litige.setRecruteur(recruteur);
        litige.setMission(mission);
        litige.setDateCreation(LocalDateTime.now());

        Litige savedLitige = litigeRepository.save(litige);
        return litigeMapper.toDto(savedLitige);
    }

    // LISTER TOUS LES LITIGES
    public List<LitigeDTO> getTousLesLitiges() {
        return litigeRepository.findAll()
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    //  LISTER LES LITIGES PAR STATUT
    public List<LitigeDTO> getLitigesParStatut(String statut) {
        return litigeRepository.findByStatut(StatutLitige.valueOf(statut))
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // LISTER LES LITIGES NON ASSIGNÉS
    public List<LitigeDTO> getLitigesNonAssignes() {
        return litigeRepository.findByAdministrateurIsNull()
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // ASSIGNER UN LITIGE À UN ADMINISTRATEUR
    public LitigeDTO assignerLitige(Long litigeId, Long administrateurId) {
        Litige litige = litigeRepository.findById(litigeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));
        Administrateur administrateur = administrateurRepository.findById(administrateurId)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));

        litige.setAdministrateur(administrateur);
        litige.setStatut(StatutLitige.EN_COURS);

        Litige savedLitige = litigeRepository.save(litige);
        return litigeMapper.toDto(savedLitige);
    }

    //  RÉSOUDRE UN LITIGE
    public LitigeDTO resoudreLitige(Long litigeId, ResolutionLitigeDTO dto) {
        Litige litige = litigeRepository.findById(litigeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));

        litige.setDecisionAdministrateur(dto.getDecisionAdministrateur());
        litige.setStatut(StatutLitige.valueOf(dto.getStatut()));
        litige.setDateResolution(LocalDateTime.now());

        // Si un administrateur est spécifié dans la résolution
        if (dto.getAdministrateurId() != null) {
            Administrateur administrateur = administrateurRepository.findById(dto.getAdministrateurId())
                    .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));
            litige.setAdministrateur(administrateur);
        }

        Litige savedLitige = litigeRepository.save(litige);
        return litigeMapper.toDto(savedLitige);
    }

    //  LITIGES D'UN JEUNE PRESTATAIRE
    public List<LitigeDTO> getLitigesParJeunePrestateur(Long jeuneId) {
        return litigeRepository.findByJeunePrestateurId(jeuneId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    //  LITIGES D'UN RECRUTEUR
    public List<LitigeDTO> getLitigesParRecruteur(Long recruteurId) {
        return litigeRepository.findByRecruteurId(recruteurId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // LITIGES D'UNE MISSION
    public List<LitigeDTO> getLitigesParMission(Long missionId) {
        return litigeRepository.findByMissionId(missionId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    //  LITIGES D'UN ADMINISTRATEUR
    public List<LitigeDTO> getLitigesParAdministrateur(Long administrateurId) {
        return litigeRepository.findByAdministrateurId(administrateurId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // STATISTIQUES
    public StatistiquesLitigeDTO getStatistiques() {
        StatistiquesLitigeDTO stats = new StatistiquesLitigeDTO();
        stats.setTotalLitiges(litigeRepository.count());
        stats.setLitigesOuverts(litigeRepository.countByStatut(StatutLitige.OUVERT));
        stats.setLitigesEnCours(litigeRepository.countByStatut(StatutLitige.EN_COURS));
        stats.setLitigesResolus(litigeRepository.countByStatut(StatutLitige.RESOLU));
        stats.setLitigesNonAssignes(litigeRepository.countByAdministrateurIsNull());
        return stats;
    }


    // NOUVELLE MÉTHODE - Créer un litige avec userId (surcharge)
    public LitigeDTO creerLitige(CreationLitigeDTO dto, Long userId) {
        // Utilisez l'userId du token au lieu de celui du DTO
        JeunePrestateur jeune = jeunePrestateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Jeune prestataire non trouvé"));

        // Pour le recruteur et la mission, gardez la logique du DTO ou adaptez
        Recruteur recruteur = recruteurRepository.findById(dto.getRecruteurId())
                .orElseThrow(() -> new RuntimeException("Recruteur non trouvé"));
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        Litige litige = new Litige();
        litige.setTitre(dto.getTitre());
        litige.setDescription(dto.getDescription());
        litige.setType(TypeLitige.valueOf(dto.getType()));
        litige.setStatut(StatutLitige.OUVERT);
        litige.setJeunePrestateur(jeune);  // ← Utilise l'userId du token
        litige.setRecruteur(recruteur);
        litige.setMission(mission);
        litige.setDateCreation(LocalDateTime.now());

        Litige savedLitige = litigeRepository.save(litige);
        return litigeMapper.toDto(savedLitige);
    }

    // NOUVELLE MÉTHODE - Tous les litiges pour un utilisateur
    public List<LitigeDTO> getTousLesLitiges(Long userId) {
        // Retourne seulement les litiges de l'utilisateur connecté
        return litigeRepository.findByJeunePrestateurId(userId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // NOUVELLE MÉTHODE - Litiges par statut pour un utilisateur
    public List<LitigeDTO> getLitigesParStatut(String statut, Long userId) {
        return litigeRepository.findByJeunePrestateurId(userId)
                .stream()
                .filter(litige -> litige.getStatut().name().equals(statut))
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // NOUVELLE MÉTHODE - Litiges pour un utilisateur
    public List<LitigeDTO> getLitigesParUtilisateur(Long userId) {
        return litigeRepository.findByJeunePrestateurId(userId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // NOUVELLE MÉTHODE - Litiges d'une mission pour un utilisateur
    public List<LitigeDTO> getLitigesParMissionPourUtilisateur(Long missionId, Long userId) {
        return litigeRepository.findByMissionIdAndJeunePrestateurId(missionId, userId)
                .stream()
                .map(litigeMapper::toDto)
                .collect(Collectors.toList());
    }

    // NOUVELLE MÉTHODE - Résoudre un litige avec adminId
    public LitigeDTO resoudreLitige(Long litigeId, ResolutionLitigeDTO dto, Long adminId) {
        Litige litige = litigeRepository.findById(litigeId)
                .orElseThrow(() -> new RuntimeException("Litige non trouvé"));

        // Associe l'admin connecté
        Administrateur administrateur = administrateurRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));

        litige.setDecisionAdministrateur(dto.getDecisionAdministrateur());
        litige.setStatut(StatutLitige.valueOf(dto.getStatut()));
        litige.setDateResolution(LocalDateTime.now());
        litige.setAdministrateur(administrateur);  // ← Admin du token

        Litige savedLitige = litigeRepository.save(litige);
        return litigeMapper.toDto(savedLitige);
    }

    // NOUVELLE MÉTHODE - Statistiques pour un utilisateur
    public StatistiquesLitigeDTO getStatistiques(Long userId) {
        List<Litige> litiges = litigeRepository.findByJeunePrestateurId(userId);

        long total = litiges.size();
        long ouverts = litiges.stream().filter(l -> l.getStatut() == StatutLitige.OUVERT).count();
        long enCours = litiges.stream().filter(l -> l.getStatut() == StatutLitige.EN_COURS).count();
        long resolus = litiges.stream().filter(l -> l.getStatut() == StatutLitige.RESOLU).count();
        double tauxResolution = total > 0 ? (resolus * 100.0) / total : 0.0;

        // Créez un objet StatistiquesLitigeDTO avec ces valeurs
        StatistiquesLitigeDTO stats = new StatistiquesLitigeDTO();
        stats.setTotalLitiges(total);
        stats.setLitigesOuverts(ouverts);
        stats.setLitigesEnCours(enCours);
        stats.setLitigesResolus(resolus);

        return stats;
    }
}