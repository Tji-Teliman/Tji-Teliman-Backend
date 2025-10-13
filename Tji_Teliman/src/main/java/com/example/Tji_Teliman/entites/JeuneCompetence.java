package com.example.Tji_Teliman.entites;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jeune_competence")
@JsonIgnoreProperties({"jeunePrestateur"})
public class JeuneCompetence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jeune_prestateur_id", nullable = false)
    private JeunePrestateur jeunePrestateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;
}


