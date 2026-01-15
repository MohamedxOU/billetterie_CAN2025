package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Match {
    private String codeMatch;
    private String equipeA;
    private String equipeB;
    private String stade;
    private String date;// date (DD/MM/YYYY)
    private String heure; // heure (HH:MM)
    private int importance; // 1: faible, 2: moyenne, 3: élevée

    public Match(String codeMatch, String equipeA, String equipeB, String stade, String date, String heure, int importance) {
        this.codeMatch = codeMatch;
        this.equipeA = equipeA;
        this.equipeB = equipeB;
        this.stade = stade;
        this.date = date;
        this.heure = heure;
        this.importance = importance;
    }

    public String getCodeMatch() {
        return codeMatch;
    } 

    public String getEquipeA() {
        return equipeA;
    }

    public String getEquipeB() {
        return equipeB;
    }

    public String getStade() {
        return stade;
    }

    public String getDate() {
        return date;
    }

    public String getHeure() {
        return heure;
    }

    public int getImportance() {
        return importance;
    }

    // Retourne le coefficient d'importance pour le calcul du prix
    // 1 (faible) = 1.0, 2 (moyenne) = 1.5, 3 (élevée) = 2.0
    public double getCoefficientImportance() {
        switch (importance) {
            case 1: return 1.0;
            case 2: return 1.5;
            case 3: return 2.0;
            default: return 1.0;
        }
    }

    // Retourne la date et l'heure du match sous forme de LocalDateTime
    public LocalDateTime getDateHeure() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return LocalDateTime.parse(date + " " + heure, formatter);
        } catch (Exception e) {
            return LocalDateTime.now().plusDays(30); // Par défaut, match dans 30 jours
        }
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public void setStade(String stade) {
        this.stade = stade;
    }

    public void setEquipeA(String equipeA) {
        this.equipeA = equipeA;
    }   

    public void setEquipeB(String equipeB) {
        this.equipeB = equipeB;
    }

    public void setCodeMatch(String codeMatch) {
        this.codeMatch = codeMatch;
    }

    public String getImportanceLabel() {
        switch (importance) {
            case 1: return "Faible";
            case 2: return "Moyenne";
            case 3: return "Élevée";
            default: return "Inconnue";
        }
    }

    @Override
    public String toString() {
        return "Match [code=" + codeMatch + ", " + equipeA + " vs " + equipeB + 
               ", stade=" + stade + ", " + date + " " + heure + 
               ", importance=" + getImportanceLabel() + "]";
    }
}
