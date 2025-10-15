package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.controllers.UtilisateurController.UserRegistrationRequest;
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

        if (req.email != null && !req.email.trim().isEmpty() && utilisateurRepository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        if (utilisateurRepository.existsByTelephone(req.telephone)) {
            throw new IllegalArgumentException("Téléphone déjà utilisé");
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

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private String emptyToNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s; }

    @Transactional(readOnly = true)
    public Object authenticate(String telephone, String motDePasse) {
        if (isBlank(telephone) || isBlank(motDePasse)) {
            throw new IllegalArgumentException("Téléphone et motDePasse requis");
        }
        var userOpt = utilisateurRepository.findByTelephone(telephone);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur introuvable");
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe invalide");
        }
        // Retourner un payload sans motDePasse + token
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

        // Validation de la force du mot de passe (minimum 6 caractères)
        if (nouveauMotDePasse.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères");
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
