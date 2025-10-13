package com.example.Tji_Teliman.dto;

import java.util.Date;

public class NotationDTO {
    private Long id;
    private Integer note;
    private String commentaire;
    private Date dateNotation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public Date getDateNotation() { return dateNotation; }
    public void setDateNotation(Date dateNotation) { this.dateNotation = dateNotation; }
}


