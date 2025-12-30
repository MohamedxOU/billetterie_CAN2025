package models;

public class Billet {
    private static int compteur = 100;
    private int codeBillet;
    private Client client;
    private Match match; 
    private ZonePlace zone;
    private String statut; // "réservé", "payé", "annulé"
    private double montant;

    public Billet(Client client, Match match, ZonePlace zone, String statut, double montant) {
        this.codeBillet = compteur++;
        this.client = client;
        this.match = match;
        this.zone = zone;
        this.statut = statut;
        this.montant = montant;
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


    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Billet [codeBillet=" + codeBillet + ", client=" + client + ", match=" + match + ", zone=" + zone
                + ", statut=" + statut + ", montant=" + montant + "]";
    }
}
