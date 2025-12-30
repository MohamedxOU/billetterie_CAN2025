package models;

public class Media extends Client {
    private boolean accredite;
    
    public Media(String nom, String email) {
        super(nom, email);
        this.accredite = false;
    }

    @Override
    public String toString() {
        return "\tId: " + getId() + ", Nom : " + getNom() + ", Email: " + getEmail() + ", Type: Média" + ", Accrédité: " + (accredite ? "Oui" : "Non");
    }

    public double getReduction() {
        return accredite ? 1.0 : 0.0;
    }

    public boolean isAccredite() {
        return accredite;
    }

    public void setAccredite(boolean accredite) {
        this.accredite = accredite;
    }
}
