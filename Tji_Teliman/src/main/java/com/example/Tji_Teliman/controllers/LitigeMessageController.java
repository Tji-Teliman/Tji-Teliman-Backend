package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.AdminLitigeMessageRequest;
import com.example.Tji_Teliman.dto.LitigeMessageDTO;
import com.example.Tji_Teliman.entites.enums.LitigeMessageDestinataire;
import com.example.Tji_Teliman.repository.AdministrateurRepository;
import com.example.Tji_Teliman.services.LitigeMessageService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/litiges/{litigeId}/messages-admin")
public class LitigeMessageController {

    private final LitigeMessageService litigeMessageService;
    private final JwtUtils jwtUtils;
    private final AdministrateurRepository administrateurRepository;

    public LitigeMessageController(LitigeMessageService litigeMessageService,
                                   JwtUtils jwtUtils,
                                   AdministrateurRepository administrateurRepository) {
        this.litigeMessageService = litigeMessageService;
        this.jwtUtils = jwtUtils;
        this.administrateurRepository = administrateurRepository;
    }

    @PostMapping
    public ResponseEntity<LitigeMessageDTO> envoyerMessage(@PathVariable Long litigeId,
                                                           @RequestBody AdminLitigeMessageRequest request,
                                                           HttpServletRequest httpRequest) {
        Long adminId = jwtUtils.getUserIdFromToken(httpRequest);
        if (adminId == null) {
            return ResponseEntity.badRequest().build();
        }

        if (administrateurRepository.findById(adminId).isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        try {
            if (request.getDestinataire() == null || request.getContenu() == null || request.getContenu().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            LitigeMessageDestinataire destinataire = LitigeMessageDestinataire.valueOf(request.getDestinataire().toUpperCase());
            LitigeMessageDTO dto = litigeMessageService.envoyerMessage(litigeId, adminId, destinataire, request.getContenu());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LitigeMessageDTO>> listerMessages(@PathVariable Long litigeId,
                                                                 HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromToken(httpRequest);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<LitigeMessageDTO> messages = litigeMessageService.getMessagesPourLitige(litigeId, userId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).build();
        }
    }

    @PutMapping("/{messageId}/lu")
    public ResponseEntity<Void> marquerCommeLu(@PathVariable Long litigeId,
                                               @PathVariable Long messageId,
                                               HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromToken(httpRequest);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            litigeMessageService.verifierAccesLitige(litigeId, userId);
            litigeMessageService.marquerCommeLu(messageId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).build();
        }
    }
}

