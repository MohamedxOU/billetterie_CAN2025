package models;

public class Spectateur extends Client {
    
    public Spectateur(String nom, String email) {
        super(nom, email);
    
    }

    @Override
    public double getReduction() {
        return 0.0;
    }

}
    