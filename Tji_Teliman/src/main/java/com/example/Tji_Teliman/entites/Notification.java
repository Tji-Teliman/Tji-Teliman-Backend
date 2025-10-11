package com.example.Tji_Teliman.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(nullable = false)
    private boolean estLue;
}
