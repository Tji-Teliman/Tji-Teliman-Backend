package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.controllers.UtilisateurController.UserRegistrationRequest;
import com.example.Tji_Teliman.controllers.UtilisateurController.JeuneRegistrationRequest;
import com.example.Tji_Teliman.controllers.UtilisateurController.RecruteurRegistrationRequest;
import com.example.Tji_Teliman.entites.JeunePrestateur;
import com.example.Tji_Teliman.entites.Recruteur;
import com.example.Tji_Teliman.entites.enums.Role;
import com.example.Tji_Teliman.entites.enums.TypeGenre;
import com.example.Tji_Teliman.repository.JeunePrestateurRepository;
import com.example.Tji_Teliman.repository.RecruteurRepository;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Tji_Teliman.entites.enums.TypeRecruteur;
import com.example.Tji_Teliman.config.JwtService;
import com.example.Tji_Teliman.entites.enums.StatutUtilisateur;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final JeunePrestateurRepository jeuneRepo;
    private final RecruteurRepository recruteurRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              JeunePrestateurRepository jeuneRepo,
                              RecruteurRepository recruteurRepo,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.jeuneRepo = jeuneRepo;
        this.recruteurRepo = recruteurRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public Object register(UserRegistrationRequest req) {
        if (isBlank(req.nom) || isBlank(req.prenom) || isBlank(req.motDePasse)
            || isBlank(req.telephone) || isBlank(req.role) || isBlank(req.genre)) {
            throw new IllegalArgumentException("Champs requis manquants");
        }

        // Validation: téléphone au moins 8 chiffres
        if (req.telephone == null || !req.telephone.matches("^\\d{8,}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
        }

        // Validation: email doit contenir @gmail.com si fourni
        if (req.email != null && !req.email.trim().isEmpty()) {
            if (!req.email.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email incorrect");
            }
            if (utilisateurRepository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
            }
        }
        if (utilisateurRepository.existsByTelephone(req.telephone)) {
            throw new IllegalArgumentException("Téléphone déjà utilisé");
        }

        // Validation: mot de passe fort (min 8, 1 maj, 1 min, 1 chiffre, 1 spécial)
        if (!isStrongPassword(req.motDePasse)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au minimum 8 caractères, avec au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial");
        }

        Role roleEnum;
        try { roleEnum = Role.valueOf(req.role); } catch (Exception e) { throw new IllegalArgumentException("Role invalide"); }
        TypeGenre genreEnum;
        try { genreEnum = TypeGenre.valueOf(req.genre); } catch (Exception e) { throw new IllegalArgumentException("Genre invalide"); }

        if (roleEnum == Role.JEUNE_PRESTATEUR) {
            JeunePrestateur j = new JeunePrestateur();
            j.setNom(req.nom);
            j.setPrenom(req.prenom);
            j.setEmail(emptyToNull(req.email));
            j.setMotDePasse(passwordEncoder.encode(req.motDePasse));
            j.setTelephone(req.telephone);
            j.setGenre(genreEnum);
            j.setRole(Role.JEUNE_PRESTATEUR);
            var saved = jeuneRepo.save(j);
            String token = jwtService.generateToken(saved.getId(), saved.getTelephone(), saved.getRole().name());
            var user = new AuthResponse(saved.getId(), saved.getNom(), saved.getPrenom(), saved.getTelephone(), saved.getEmail(), saved.getRole().name(), saved.getGenre().name());
            return new LoginResponse(user, token);
        } else if (roleEnum == Role.RECRUTEUR) {
            if (isBlank(req.typeRecruteur)) {
                throw new IllegalArgumentException("typeRecruteur requis pour un recruteur");
            }
            TypeRecruteur tr;
            try { tr = TypeRecruteur.valueOf(req.typeRecruteur); } catch (Exception e) { throw new IllegalArgumentException("typeRecruteur invalide"); }
            Recruteur r = new Recruteur();
            r.setNom(req.nom);
            r.setPrenom(req.prenom);
            r.setEmail(emptyToNull(req.email));
            r.setMotDePasse(passwordEncoder.encode(req.motDePasse));
            r.setTelephone(req.telephone);
            r.setGenre(genreEnum);
            r.setRole(Role.RECRUTEUR);
            r.setTypeRecruteur(tr);
            var saved = recruteurRepo.save(r);
            String token = jwtService.generateToken(saved.getId(), saved.getTelephone(), saved.getRole().name());
            var user = new AuthResponse(saved.getId(), saved.getNom(), saved.getPrenom(), saved.getTelephone(), saved.getEmail(), saved.getRole().name(), saved.getGenre().name());
            return new LoginResponse(user, token);
        } else {
            throw new IllegalArgumentException("Role non supporté pour inscription");
        }
    }

    // Inscription JEUNE avec role implicite
    @Transactional
    public Object registerJeune(JeuneRegistrationRequest req) {
        if (isBlank(req.nom) || isBlank(req.prenom) || isBlank(req.motDePasse)
            || isBlank(req.confirmationMotDePasse) || isBlank(req.telephone) || isBlank(req.genre)) {
            throw new IllegalArgumentException("Champs requis manquants");
        }

        // Validation: confirmation du mot de passe
        if (!req.motDePasse.equals(req.confirmationMotDePasse)) {
            throw new IllegalArgumentException("Le mot de passe et la confirmation ne correspondent pas");
        }

        if (req.telephone == null || !req.telephone.matches("^\\d{8,}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
        }

        if (req.email != null && !req.email.trim().isEmpty()) {
            if (!req.email.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email incorrect");
            }
            if (utilisateurRepository.existsByEmail(req.email)) {
                throw new IllegalArgumentException("Email déjà utilisé");
            }
        }
        if (utilisateurRepository.existsByTelephone(req.telephone)) {
            throw new IllegalArgumentException("Téléphone déjà utilisé");
        }

        if (!isStrongPassword(req.motDePasse)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au minimum 8 caractères, avec au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial");
        }

        TypeGenre genreEnum;
        try { genreEnum = TypeGenre.valueOf(req.genre); } catch (Exception e) { throw new IllegalArgumentException("Genre invalide"); }

        JeunePrestateur j = new JeunePrestateur();
        j.setNom(req.nom);
        j.setPrenom(req.prenom);
        j.setEmail(emptyToNull(req.email));
        j.setMotDePasse(passwordEncoder.encode(req.motDePasse));
        j.setTelephone(req.telephone);
        j.setGenre(genreEnum);
        j.setRole(Role.JEUNE_PRESTATEUR);
        var saved = jeuneRepo.save(j);
        String token = jwtService.generateToken(saved.getId(), saved.getTelephone(), saved.getRole().name());
        var user = new AuthResponse(saved.getId(), saved.getNom(), saved.getPrenom(), saved.getTelephone(), saved.getEmail(), saved.getRole().name(), saved.getGenre().name());
        return new LoginResponse(user, token);
    }

    // Inscription RECRUTEUR avec role implicite et typeRecruteur requis
    @Transactional
    public Object registerRecruteur(RecruteurRegistrationRequest req) {
        if (isBlank(req.nom) || isBlank(req.prenom) || isBlank(req.motDePasse)
            || isBlank(req.confirmationMotDePasse) || isBlank(req.telephone) || isBlank(req.genre) || isBlank(req.typeRecruteur)) {
            throw new IllegalArgumentException("Champs requis manquants");
        }

        // Validation: confirmation du mot de passe
        if (!req.motDePasse.equals(req.confirmationMotDePasse)) {
            throw new IllegalArgumentException("Le mot de passe et la confirmation ne correspondent pas");
        }

        if (req.telephone == null || !req.telephone.matches("^\\d{8,}$")) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir au moins 8 chiffres");
        }

        if (req.email != null && !req.email.trim().isEmpty()) {
            if (!req.email.toLowerCase().endsWith("@gmail.com")) {
                throw new IllegalArgumentException("L'adresse email incorrect");
            }
            if (utilisateurRepository.existsByEmail(req.email)) {
                throw new IllegalArgumentException("Email déjà utilisé");
            }
        }
        if (utilisateurRepository.existsByTelephone(req.telephone)) {
            throw new IllegalArgumentException("Téléphone déjà utilisé");
        }

        if (!isStrongPassword(req.motDePasse)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au minimum 8 caractères, avec au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial");
        }

        TypeGenre genreEnum;
        try { genreEnum = TypeGenre.valueOf(req.genre); } catch (Exception e) { throw new IllegalArgumentException("Genre invalide"); }

        TypeRecruteur tr;
        try { tr = TypeRecruteur.valueOf(req.typeRecruteur); } catch (Exception e) { throw new IllegalArgumentException("typeRecruteur invalide"); }

        Recruteur r = new Recruteur();
        r.setNom(req.nom);
        r.setPrenom(req.prenom);
        r.setEmail(emptyToNull(req.email));
        r.setMotDePasse(passwordEncoder.encode(req.motDePasse));
        r.setTelephone(req.telephone);
        r.setGenre(genreEnum);
        r.setRole(Role.RECRUTEUR);
        r.setTypeRecruteur(tr);
        var saved = recruteurRepo.save(r);
        String token = jwtService.generateToken(saved.getId(), saved.getTelephone(), saved.getRole().name());
        var user = new AuthResponse(saved.getId(), saved.getNom(), saved.getPrenom(), saved.getTelephone(), saved.getEmail(), saved.getRole().name(), saved.getGenre().name());
        return new LoginResponse(user, token);
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String emptyToNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s; }
    private boolean isStrongPassword(String s) {
        if (s == null) return false;
        // At least 8 chars, 1 upper, 1 lower, 1 digit, 1 special
        return s.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    }

    @Transactional(readOnly = true)
    public Object authenticate(String telephone, String motDePasse) {
        if (isBlank(telephone) || isBlank(motDePasse)) {
            throw new IllegalArgumentException("Téléphone et motDePasse requis");
        }
        var userOpt = utilisateurRepository.findByTelephone(telephone);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Telephone ou mot de passe incorrect");
        }
        var user = userOpt.get();
        if (user.getStatut() == StatutUtilisateur.DESACTIVER) {
            throw new IllegalArgumentException("Compte désactivé");
        }
        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new IllegalArgumentException("Telephone ou Mot de Passe incorrect");
        }
        // Retourner un payload sans motDePasse + token
        String token = jwtService.generateToken(user.getId(), user.getTelephone(), user.getRole().name());
        return new LoginResponse(new AuthResponse(user.getId(), user.getNom(), user.getPrenom(), user.getTelephone(), user.getEmail(), user.getRole().name(), user.getGenre().name()), token);
    }

    @Transactional(readOnly = true)
    public Object authenticateAdminByEmail(String email, String motDePasse) {
        if (isBlank(email) || isBlank(motDePasse)) {
            throw new IllegalArgumentException("Email et motDePasse requis");
        }
        var userOpt = utilisateurRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Administrateur introuvable");
        }
        var user = userOpt.get();
        if (user.getRole() != Role.ADMINISTRATEUR) {
            throw new IllegalArgumentException("Accès refusé: non administrateur");
        }
        if (user.getStatut() == StatutUtilisateur.DESACTIVER) {
            throw new IllegalArgumentException("Compte désactivé");
        }
        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe invalide");
        }
        String token = jwtService.generateToken(user.getId(), user.getTelephone(), user.getRole().name());
        return new LoginResponse(new AuthResponse(user.getId(), user.getNom(), user.getPrenom(), user.getTelephone(), user.getEmail(), user.getRole().name(), user.getGenre().name()), token);
    }

    @Transactional
    public void changePassword(Long userId, String motDePasseActuel, String nouveauMotDePasse, String confirmationMotDePasse) {
        // Validation des paramètres
        if (isBlank(motDePasseActuel)) {
            throw new IllegalArgumentException("Le mot de passe actuel est requis");
        }
        if (isBlank(nouveauMotDePasse)) {
            throw new IllegalArgumentException("Le nouveau mot de passe est requis");
        }
        if (isBlank(confirmationMotDePasse)) {
            throw new IllegalArgumentException("La confirmation du mot de passe est requise");
        }

        // Vérifier que le nouveau mot de passe et la confirmation correspondent
        if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas");
        }

        // Vérifier que le nouveau mot de passe est différent de l'ancien
        if (motDePasseActuel.equals(nouveauMotDePasse)) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // Validation de la force du mot de passe (min 8, 1 maj, 1 min, 1 chiffre, 1 spécial)
        if (!isStrongPassword(nouveauMotDePasse)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au minimum 8 caractères, avec au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial");
        }

        // Récupérer l'utilisateur
        var userOpt = utilisateurRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }
        var user = userOpt.get();

        // Vérifier que le mot de passe actuel est correct
        if (!passwordEncoder.matches(motDePasseActuel, user.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
        }

        // Mettre à jour le mot de passe
        user.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(user);
    }

    public record AuthResponse(Long id, String nom, String prenom, String telephone, String email, String role, String genre) {}
    public record LoginResponse(AuthResponse user, String token) {}
}
