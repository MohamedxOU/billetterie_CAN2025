package models;

public class ZonePlace {
    private String nomZone;
    private int capacite;
    private double coefficientPrix;

    public ZonePlace(String nomZone, int capacite, double coefficientPrix) {
        this.nomZone = nomZone;
        this.capacite = capacite;
        this.coefficientPrix = coefficientPrix;
    }

    public String getNomZone() {
        return nomZone; 
    }

    public int getCapacite() {
        return capacite;
    }

    public double getCoefficientPrix() {
        return coefficientPrix;
    }

     public void setNomZone(String nomZone) {
        this.nomZone = nomZone;
    }
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
    public void setCoefficientPrix(double coefficientPrix) {
        this.coefficientPrix = coefficientPrix;
    }

    @Override
    public String toString() {
        return "ZonePlace [nomZone=" + nomZone + ", capacite=" + capacite + ", coefficientPrix=" + coefficientPrix + "]";
    }

   

}
