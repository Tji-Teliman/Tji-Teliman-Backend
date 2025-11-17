package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.dto.*;
import com.example.Tji_Teliman.entites.Utilisateur;
import com.example.Tji_Teliman.entites.enums.StatutUtilisateur;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import com.example.Tji_Teliman.repository.MissionRepository;
import com.example.Tji_Teliman.entites.enums.StatutMission;
import com.example.Tji_Teliman.entites.Mission;
import com.example.Tji_Teliman.entites.Paiement;
import com.example.Tji_Teliman.entites.enums.StatutPaiement;
import com.example.Tji_Teliman.repository.PaiementRepository;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class AdministrateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final JeunePrestateurRepository jeuneRepo;
    private final RecruteurRepository recruteurRepo;
    private final MissionRepository missionRepository;
    private final PaiementRepository paiementRepository;

    private final CandidatureService candidatureService;

    

    @Transactional(readOnly = true)
    public AdminUsersWithStatsResponse listUsersWithStats(Long adminId) {
        List<Utilisateur> all = utilisateurRepository.findAll();
        // Exclure l'administrateur courant de la liste et des totaux utilisateurs
        List<Utilisateur> filtered = all.stream()
            .filter(u -> !u.getId().equals(adminId))
            .toList();

        long totalJeunes = jeuneRepo.count();
        long totalRecruteurs = recruteurRepo.count();
        long total = filtered.size();

        List<AdminUserDTO> dtos = filtered.stream().map(this::toAdminUserDTO).collect(Collectors.toList());
        return new AdminUsersWithStatsResponse(dtos, total, totalJeunes, totalRecruteurs);
    }

    @Transactional(readOnly = true)
    public SystemStatsDTO getSystemStats(Long adminId) {
        long totalJeunes = jeuneRepo.count();
        long totalRecruteurs = recruteurRepo.count();
        long totalUtilisateurs = utilisateurRepository.count();
        // Compter toutes les missions publiées (tous les statuts)
        long totalMissionsPubliees = missionRepository.count();

        // Exclure l'admin courant du total utilisateurs si présent
        if (adminId != null) {
            // si l'admin existe, le total visible côté admin exclut son propre compte
            totalUtilisateurs = Math.max(0, totalUtilisateurs - 1);
        }

        return new SystemStatsDTO(totalUtilisateurs, totalRecruteurs, totalJeunes, totalMissionsPubliees);
    }

    @Transactional(readOnly = true)
    public AdminMissionsWithStatsResponse listMissionsWithStats() {
        List<Mission> all = missionRepository.findAll();
        // Compter toutes les missions publiées (tous les statuts)
        long totalPubliees = missionRepository.count();
        long totalTerminees = missionRepository.countByStatut(StatutMission.TERMINEE);
        long totalNonTerminees = missionRepository.countByStatut(StatutMission.EN_ATTENTE) + 
                                missionRepository.countByStatut(StatutMission.EN_COURS);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        Date debutMois = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date debutMoisSuivant = cal.getTime();
        long totalPublieesCeMoisCi = missionRepository.countByDatePublicationBetween(debutMois, debutMoisSuivant);

        List<MissionDTO> missionDTOs = all.stream().map(this::toMissionDTO).toList();
        return new AdminMissionsWithStatsResponse(missionDTOs, totalPubliees, totalTerminees, totalNonTerminees, totalPublieesCeMoisCi);
    }

    private final MissionService missionService;

    public AdministrateurService(UtilisateurRepository utilisateurRepository,
                                 JeunePrestateurRepository jeuneRepo,
                                 RecruteurRepository recruteurRepo,
                                 MissionRepository missionRepository,
                                 PaiementRepository paiementRepository, CandidatureService candidatureService,
                                 MissionService missionService,
                                 PaiementService paiementService) {
        this.utilisateurRepository = utilisateurRepository;
        this.jeuneRepo = jeuneRepo;
        this.recruteurRepo = recruteurRepo;
        this.missionRepository = missionRepository;
        this.paiementRepository = paiementRepository;
        this.candidatureService = candidatureService;
        this.missionService = missionService;
        this.paiementService = paiementService;
    }

    private MissionDTO toMissionDTO(Mission m) {
        return missionService.toDTO(m);
    }

    @Transactional(readOnly = true)
    public AdminPaiementsWithStatsResponse listPaiementsWithStats() {
        List<Paiement> all = paiementRepository.findAll();

        long totalEffectues = paiementRepository.countByStatutPaiement(StatutPaiement.REUSSIE);
        long totalEnAttente = paiementRepository.countByStatutPaiement(StatutPaiement.EN_ATTENTE);
        long totalEchoues = paiementRepository.countByStatutPaiement(StatutPaiement.ECHEC);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        Date debutMois = cal.getTime();
        cal.add(java.util.Calendar.MONTH, 1);
        Date debutMoisSuivant = cal.getTime();
        long totalCeMoisCi = paiementRepository.countByDatePaiementBetween(debutMois, debutMoisSuivant);

        List<PaiementDTO> dtoList = all.stream().map(this::toPaiementDTO).toList();
        return new AdminPaiementsWithStatsResponse(dtoList, totalEffectues, totalEnAttente, totalEchoues, totalCeMoisCi);
    }

    private final PaiementService paiementService;
    @Transactional
    public void setUserStatut(Long userId, StatutUtilisateur statut) {
        var user = utilisateurRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        user.setStatut(statut);
        utilisateurRepository.save(user);
    }

    private PaiementDTO toPaiementDTO(Paiement p) { return paiementService.toDTO(p); }

    private AdminUserDTO toAdminUserDTO(Utilisateur u) {
        return new AdminUserDTO(
            u.getId(),
            u.getNom(),
            u.getPrenom(),
            u.getRole() != null ? u.getRole().name() : null,
            u.getTelephone(),
            u.getEmail(),
            u.getGenre() != null ? u.getGenre().name() : null,
            u.getStatut() != null ? u.getStatut().name() : null,
            u.getDateCreation()
        );
    }



    // Lister les candidatures d'une mission donnée
    @GetMapping("/candidatures/mission/{missionId}")
    public ResponseEntity<List<CandidatureDTO>> getCandidaturesByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(candidatureService.getCandidaturesByMission(missionId));
    }
}
