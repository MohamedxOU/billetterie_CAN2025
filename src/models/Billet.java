package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Billet {
    private static int compteur = 100;
    private int codeBillet;
    private Client client;
    private Match match; 
    private ZonePlace zone;
    private String statut; // "réservé", "payé", "annulé"
    private double montant;
    private LocalDateTime dateCreation;

    public Billet(Client client, Match match, ZonePlace zone, String statut, double montant) {
        this.codeBillet = compteur++;
        this.client = client;
        this.match = match;
        this.zone = zone;
        this.statut = statut;
        this.montant = montant;
        this.dateCreation = LocalDateTime.now();
    }

    public int getCodeBillet() {
        return codeBillet;
    }

    public Client getClient() {
        return client;
    }

    public Match getMatch() {
        return match;
    }

    public ZonePlace getZone() {
        return zone;
    }

    public String getStatut() {
        return statut;
    }

    public double getMontant() {
        return montant;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public static void resetCompteur() {
        compteur = 100;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Billet [code=" + codeBillet + ", client=" + client.getNom() + 
               ", match=" + match.getEquipeA() + " vs " + match.getEquipeB() + 
               ", zone=" + zone.getNomZone() + ", statut=" + statut + 
               ", montant=" + String.format("%.2f", montant) + " MAD" +
               ", créé le " + dateCreation.format(formatter) + "]";
    }
}
