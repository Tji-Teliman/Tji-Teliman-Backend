package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.dto.UserProfileDTO;
import com.example.Tji_Teliman.dto.ConversationDTO;
import com.example.Tji_Teliman.services.MessageService;
import com.example.Tji_Teliman.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private ProfileService profileService;
    
    public record ApiResponse(boolean success, String message, Object data) {}
    
    public record MessageTexteRequest(String contenu) {}

    // Envoyer un message vocal (unifié pour recruteur et jeune)
    @PostMapping("/vocal/{destinataireId}")
    public ResponseEntity<?> envoyerMessageVocal(
            @PathVariable Long destinataireId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            
            // Déterminer le type d'utilisateur et qui envoie le message
            UserProfileDTO userProfile = profileService.getUserProfile(userId);
            boolean envoyeParRecruteur = userProfile.getRole().equals("RECRUTEUR");
            
            // Calcul automatique de la durée
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads/vocaux/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            int dureeSecondes = getAudioDurationInSeconds(filePath.toFile());

            // Utiliser le service avec les bons paramètres selon le type d'utilisateur
            MessageDTO messageDTO;
            if (envoyeParRecruteur) {
                // Recruteur envoie vers jeune
                messageDTO = messageService.enregistrerMessageVocal(
                        userId, destinataireId, true, file, dureeSecondes
                );
            } else {
                // Jeune envoie vers recruteur
                messageDTO = messageService.enregistrerMessageVocal(
                        destinataireId, userId, false, file, dureeSecondes
                );
            }

            return ResponseEntity.ok(new ApiResponse(true, "Message vocal envoyé", messageDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Envoyer un message texte (unifié pour recruteur et jeune)
    @PostMapping("/texte/{destinataireId}")
    public ResponseEntity<?> envoyerMessageTexte(
            @PathVariable Long destinataireId,
            @RequestBody MessageTexteRequest request,
            HttpServletRequest httpRequest) {

        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }
            
            // Déterminer le type d'utilisateur et qui envoie le message
            UserProfileDTO userProfile = profileService.getUserProfile(userId);
            boolean envoyeParRecruteur = userProfile.getRole().equals("RECRUTEUR");

            // Utiliser le service avec les bons paramètres selon le type d'utilisateur
            MessageDTO messageDTO;
            if (envoyeParRecruteur) {
                // Recruteur envoie vers jeune
                messageDTO = messageService.enregistrerMessageTexte(
                        userId, destinataireId, true, request.contenu()
                );
            } else {
                // Jeune envoie vers recruteur
                messageDTO = messageService.enregistrerMessageTexte(
                        destinataireId, userId, false, request.contenu()
                );
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Message texte envoyé", messageDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Lecture de la conversation entre deux utilisateurs (unifié pour recruteur et jeune)
    @GetMapping("/conversation/{destinataireId}")
    public ResponseEntity<?> lireMessages(
            @PathVariable Long destinataireId,
            HttpServletRequest httpRequest) {

        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }

            // Déterminer le type d'utilisateur pour lire les messages dans le bon ordre
            UserProfileDTO userProfile = profileService.getUserProfile(userId);
            boolean estRecruteur = userProfile.getRole().equals("RECRUTEUR");

            List<MessageDTO> messages;
            if (estRecruteur) {
                // Recruteur lit conversation avec jeune
                messages = messageService.lireMessages(userId, destinataireId);
            } else {
                // Jeune lit conversation avec recruteur
                messages = messageService.lireMessages(destinataireId, userId);
            }
            
            return ResponseEntity.ok(new ApiResponse(true, "Messages récupérés", messages));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Lister toutes les conversations de l'utilisateur connecté
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }

            List<ConversationDTO> conversations = messageService.getConversations(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Conversations récupérées", conversations));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Supprimer un message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> supprimerMessage(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }

            messageService.supprimerMessage(messageId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Message supprimé", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Obtenir un message par son ID
    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessageById(
            @PathVariable Long messageId,
            HttpServletRequest httpRequest) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(httpRequest);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Token manquant ou invalide", null));
            }

            MessageDTO messageDTO = messageService.getMessageById(messageId);
            return ResponseEntity.ok(new ApiResponse(true, "Message récupéré", messageDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Servir un fichier audio de message vocal
    @GetMapping("/audio/{filename}")
    public ResponseEntity<Resource> getAudioFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/vocaux/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private int getAudioDurationInSeconds(File audioFile) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double dureeSecondes = (frames + 0.0) / format.getFrameRate();
            return (int) Math.round(dureeSecondes);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}