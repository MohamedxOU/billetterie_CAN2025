package models;

public class Paiment {
    //reference, billet, mode, statut, dateHeure.
    private String reference;
    private Billet billet;
    private String mode; // "carte bancaire", "mobile money", "espèces"
    private String statut; // "réussi", "échoué", "en attente"
    private String dateHeure; // format "YYYY-MM-DD HH:MM:SS"

    public Paiment(String reference, Billet billet, String mode, String statut, String dateHeure) {
        this.reference = reference;
        this.billet = billet;
        this.mode = mode;
        this.statut = statut;
        this.dateHeure = dateHeure;
    }

    public String getReference() {
        return reference;
    }

    public Billet getBillet() {
        return billet;
    }
    public String getMode() {
        return mode;
    }
    public String getStatut() {
        return statut;
    }
    public String getDateHeure() {
        return dateHeure;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    @Override
    public String toString() {
        return "Paiment [reference=" + reference + ", billet=" + billet + ", mode=" + mode + ", statut=" + statut
                + ", dateHeure=" + dateHeure + "]";
    }
}
