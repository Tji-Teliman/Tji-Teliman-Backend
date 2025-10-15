package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.dto.MessageDTO;
import com.example.Tji_Teliman.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // Envoi d'un message vocal
    @PostMapping("/vocal")
    public ResponseEntity<?> envoyerMessageVocal(
            @RequestParam Long recruteurId,
            @RequestParam Long jeunePrestateurId,
            @RequestParam("file") MultipartFile file,
            @RequestParam boolean envoyeParRecruteur) {

        try {
            // Calcul automatique de la durée
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads/vocaux/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            int dureeSecondes = getAudioDurationInSeconds(filePath.toFile());

            // Utiliser le service qui retourne maintenant un DTO
            MessageDTO messageDTO = messageService.enregistrerMessageVocal(
                    recruteurId, jeunePrestateurId, envoyeParRecruteur, file, dureeSecondes
            );

            return ResponseEntity.ok(messageDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur: " + e.getMessage());
        }
    }

    // Envoi d'un message texte
    @PostMapping("/texte")
    public ResponseEntity<MessageDTO> envoyerMessageTexte(
            @RequestParam Long recruteurId,
            @RequestParam Long jeunePrestateurId,
            @RequestParam boolean envoyeParRecruteur,
            @RequestParam String contenu) {

        MessageDTO messageDTO = messageService.enregistrerMessageTexte(
                recruteurId, jeunePrestateurId, envoyeParRecruteur, contenu
        );
        return ResponseEntity.ok(messageDTO);
    }

    // Lecture de la conversation entre deux utilisateurs
    @GetMapping
    public ResponseEntity<List<MessageDTO>> lireMessages(
            @RequestParam Long recruteurId,
            @RequestParam Long jeunePrestateurId) {

        List<MessageDTO> messages = messageService.lireMessages(recruteurId, jeunePrestateurId);
        return ResponseEntity.ok(messages);
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