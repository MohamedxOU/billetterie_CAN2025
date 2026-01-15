package models;

public abstract class Client {
    
    private static int idCounter = 1;
    private int id;
    private String nom;
    private String email;

    public Client(String nom, String email) {
        this.id = idCounter++;
        this.nom = nom;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Réinitialise le compteur d'ID pour les tests
    public static void resetIdCounter() {
        idCounter = 1;
    }

    // Retourne le prochain ID qui sera attribué
    public static int getNextId() {
        return idCounter;
    }

    @Override
    public String toString() {
        return "Client [id=" + id + ", nom=" + nom + ", email=" + email + "]";
    }

    // Retourne le pourcentage de réduction (0.0 = pas de réduction, 1.0 = 100% gratuit)
    public abstract double getReduction();
}
