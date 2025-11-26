package com.example.Tji_Teliman.controllers;

import com.example.Tji_Teliman.config.JwtUtils;
import com.example.Tji_Teliman.dto.LitigeAdminConversationDTO;
import com.example.Tji_Teliman.services.LitigeMessageService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/litiges/messages-admin")
public class LitigeMessageConversationController {

    private final LitigeMessageService litigeMessageService;
    private final JwtUtils jwtUtils;

    public LitigeMessageConversationController(LitigeMessageService litigeMessageService,
                                               JwtUtils jwtUtils) {
        this.litigeMessageService = litigeMessageService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<LitigeAdminConversationDTO>> listerConversations(HttpServletRequest httpRequest) {
        Long userId = jwtUtils.getUserIdFromToken(httpRequest);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<LitigeAdminConversationDTO> conversations = litigeMessageService.getConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(403).build();
        }
    }
}

