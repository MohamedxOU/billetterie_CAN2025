package services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import exceptions.AccreditationRefuseeException;
import exceptions.BilletIndisponibleException;
import exceptions.DonneeInvalideException;
import exceptions.PaiementInvalideException;
import interfaces.Payable;
import interfaces.Reservable;
import models.Billet;
import models.Client;
import models.Match;
import models.Media;
import models.Paiment;
import models.Spectateur;
import models.ZonePlace;
import utils.JsonStorage;

public class TicketingService implements Reservable, Payable {

    // Prix de base pour un billet (en MAD)
    private static final double PRIX_BASE = 5000.0;
    
    // Pénalité d'annulation si < 24h avant le match (20%)
    private static final double PENALITE_ANNULATION = 0.20;

    // Collections principales
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Match> matches = new ArrayList<>();
    private ArrayList<Billet> billets = new ArrayList<>();
    private ArrayList<Paiment> paiements = new ArrayList<>();
    
    // HashMap pour accès rapide
    private HashMap<String, ArrayList<ZonePlace>> zoneParMatch = new HashMap<>();
    private HashMap<String, Integer> billetsVendusParZone = new HashMap<>(); // "codeMatch_nomZone" -> nb billets vendus
    private HashMap<Integer, Billet> billetParCode = new HashMap<>(); // codeBillet -> Billet

    // ==================== GETTERS ====================
    
    public ArrayList<Client> getClients() {
        return clients;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public ArrayList<Billet> getBillets() {
        return billets;
    }

    public ArrayList<Paiment> getPaiements() {
        return paiements;
    }

    public HashMap<String, ArrayList<ZonePlace>> getZoneParMatch() {
        return zoneParMatch;
    }

    // ==================== GESTION DES MATCHS ====================

    public void ajouterMatch(String codeMatch, String equipeA, String equipeB, String stade, 
                             String date, String heure, int importance) throws DonneeInvalideException {
        // Validation des données
        if (codeMatch == null || codeMatch.trim().isEmpty()) {
            throw new DonneeInvalideException("Le code du match ne peut pas être vide");
        }
        if (equipeA == null || equipeA.trim().isEmpty() || equipeB == null || equipeB.trim().isEmpty()) {
            throw new DonneeInvalideException("Les noms des équipes ne peuvent pas être vides");
        }
        if (stade == null || stade.trim().isEmpty()) {
            throw new DonneeInvalideException("Le nom du stade ne peut pas être vide");
        }
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new DonneeInvalideException("Format de date invalide. Utilisez DD/MM/YYYY");
        }
        if (heure == null || !heure.matches("\\d{2}:\\d{2}")) {
            throw new DonneeInvalideException("Format d'heure invalide. Utilisez HH:MM");
        }
        if (importance < 1 || importance > 3) {
            throw new DonneeInvalideException("L'importance doit être entre 1 et 3");
        }
        if (matchExists(codeMatch)) {
            throw new DonneeInvalideException("Un match avec ce code existe déjà");
        }

        this.matches.add(new Match(codeMatch, equipeA, equipeB, stade, date, heure, importance));
    }

    public void ajouterZonePlace(String codeMatch, ZonePlace zonePlace) throws DonneeInvalideException {
        if (!matchExists(codeMatch)) {
            throw new DonneeInvalideException("Le match avec le code " + codeMatch + " n'existe pas");
        }
        if (zonePlace.getNomZone() == null || zonePlace.getNomZone().trim().isEmpty()) {
            throw new DonneeInvalideException("Le nom de la zone ne peut pas être vide");
        }
        if (zonePlace.getCapacite() <= 0) {
            throw new DonneeInvalideException("La capacité doit être supérieure à 0");
        }
        if (zonePlace.getCoefficientPrix() <= 0) {
            throw new DonneeInvalideException("Le coefficient de prix doit être supérieur à 0");
        }

        this.zoneParMatch.putIfAbsent(codeMatch, new ArrayList<>());
        this.zoneParMatch.get(codeMatch).add(zonePlace);
        
        // Initialiser le compteur de billets vendus pour cette zone
        String key = codeMatch + "_" + zonePlace.getNomZone();
        this.billetsVendusParZone.put(key, 0);
    }

    public boolean matchExists(String codeMatch) {
        return findMatchByCode(codeMatch) != null;
    }

    public Match findMatchByCode(String codeMatch) {
        for (Match m : matches) {
            if (m.getCodeMatch().equals(codeMatch)) {
                return m;
            }
        }
        return null;
    }

    public ArrayList<ZonePlace> getZonesForMatch(String codeMatch) {
        return zoneParMatch.getOrDefault(codeMatch, new ArrayList<>());
    }

    public ZonePlace findZoneByName(String codeMatch, String nomZone) {
        ArrayList<ZonePlace> zones = zoneParMatch.get(codeMatch);
        if (zones != null) {
            for (ZonePlace zp : zones) {
                if (zp.getNomZone().equals(nomZone)) {
                    return zp;
                }
            }
        }
        return null;
    }

    // ==================== GESTION DES CLIENTS ====================

    public void ajouterClient(String nom, String email, boolean isMedia) throws DonneeInvalideException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new DonneeInvalideException("Le nom du client ne peut pas être vide");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new DonneeInvalideException("L'email du client ne peut pas être vide");
        }
        if (!email.contains("@")) {
            throw new DonneeInvalideException("L'email doit contenir un @");
        }

        this.clients.add(isMedia ? new Media(nom, email) : new Spectateur(nom, email));
    }

    public Client findClientById(int id) {
        for (Client c : clients) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Media> getMediaClients() {
        ArrayList<Media> mediaList = new ArrayList<>();
        for (Client c : clients) {
            if (c instanceof Media) {
                mediaList.add((Media) c);
            }
        }
        return mediaList;
    }

    public Media findMediaById(int id) {
        for (Client c : clients) {
            if (c instanceof Media && c.getId() == id) {
                return (Media) c;
            }
        }
        return null;
    }

    // ==================== GESTION DES ACCRÉDITATIONS ====================

    public void accrediterMedia(int idMedia) throws AccreditationRefuseeException {
        Media media = findMediaById(idMedia);
        if (media == null) {
            throw new AccreditationRefuseeException("Média non trouvé avec l'ID: " + idMedia);
        }
        if (media.isAccredite()) {
            throw new AccreditationRefuseeException("Ce média est déjà accrédité");
        }
        media.setAccredite(true);
    }

    public void retirerAccreditation(int idMedia) throws AccreditationRefuseeException {
        Media media = findMediaById(idMedia);
        if (media == null) {
            throw new AccreditationRefuseeException("Média non trouvé avec l'ID: " + idMedia);
        }
        if (!media.isAccredite()) {
            throw new AccreditationRefuseeException("Ce média n'est pas accrédité");
        }
        media.setAccredite(false);
    }

    public boolean toggleAccreditationById(int id) {
        Media media = findMediaById(id);
        if (media != null) {
            media.setAccredite(!media.isAccredite());
            return true;
        }
        return false;
    }

    // ==================== CALCUL DU PRIX ====================

    public double calculerPrix(Match match, ZonePlace zone, Client client) {
        // Prix = prixBase × coefficient(zone) × coefficient(importance) − réduction(client)
        double prixBrut = PRIX_BASE * zone.getCoefficientPrix() * match.getCoefficientImportance();
        double reduction = client.getReduction(); // 0.0 à 1.0
        return prixBrut * (1 - reduction);
    }

    // ==================== INTERFACE RESERVABLE ====================

    @Override
    public boolean estDisponible(Match match, ZonePlace zonePlace) throws BilletIndisponibleException {
        // Vérifier si le match existe
        Match foundMatch = findMatchByCode(match.getCodeMatch());
        if (foundMatch == null) {
            throw new BilletIndisponibleException("Match non trouvé: " + match.getCodeMatch());
        }

        // Vérifier si la zone existe pour ce match
        ZonePlace foundZone = findZoneByName(match.getCodeMatch(), zonePlace.getNomZone());
        if (foundZone == null) {
            throw new BilletIndisponibleException("Zone non trouvée pour ce match: " + zonePlace.getNomZone());
        }

        // Vérifier le quota
        String key = match.getCodeMatch() + "_" + zonePlace.getNomZone();
        int vendus = billetsVendusParZone.getOrDefault(key, 0);
        
        if (vendus >= foundZone.getCapacite()) {
            throw new BilletIndisponibleException("Quota épuisé pour la zone " + zonePlace.getNomZone() + 
                " du match " + match.getEquipeA() + " vs " + match.getEquipeB());
        }

        return true;
    }

    @Override
    public Billet reserverBillet(Client client, Match match, ZonePlace zonePlace) 
            throws BilletIndisponibleException {
        // Vérifier la disponibilité
        estDisponible(match, zonePlace);

        // Calculer le prix
        double montant = calculerPrix(match, zonePlace, client);

        // Créer le billet
        Billet billet = new Billet(client, match, zonePlace, "réservé", montant);
        billets.add(billet);
        billetParCode.put(billet.getCodeBillet(), billet);

        // Incrémenter le compteur de billets vendus
        String key = match.getCodeMatch() + "_" + zonePlace.getNomZone();
        billetsVendusParZone.put(key, billetsVendusParZone.getOrDefault(key, 0) + 1);

        return billet;
    }

    // Méthode pour vendre un billet (réservation + paiement en une étape)
    public Billet vendreBillet(Client client, Match match, ZonePlace zonePlace, String modePaiement) 
            throws BilletIndisponibleException, AccreditationRefuseeException, PaiementInvalideException {
        
        // Vérifier si c'est un média non accrédité
        if (client instanceof Media) {
            Media media = (Media) client;
            if (!media.isAccredite()) {
                throw new AccreditationRefuseeException("Le média " + media.getNom() + 
                    " n'est pas accrédité. Veuillez l'accréditer avant de pouvoir lui vendre un billet.");
            }
        }

        // Réserver le billet
        Billet billet = reserverBillet(client, match, zonePlace);

        // Effectuer le paiement
        payer(billet, modePaiement);

        return billet;
    }

    @Override
    public void annulerBillet(int codeBillet) {
        Billet billet = billetParCode.get(codeBillet);
        if (billet != null && !billet.getStatut().equals("annulé")) {
            // Calculer la pénalité si < 24h avant le match
            LocalDateTime maintenant = LocalDateTime.now();
            LocalDateTime dateMatch = billet.getMatch().getDateHeure();
            long heuresAvantMatch = ChronoUnit.HOURS.between(maintenant, dateMatch);

            double montantRemboursement = billet.getMontant();
            String message = "";
            
            if (heuresAvantMatch < 24 && heuresAvantMatch > 0) {
                // Appliquer la pénalité de 20%
                double penalite = billet.getMontant() * PENALITE_ANNULATION;
                montantRemboursement = billet.getMontant() - penalite;
                message = " (Pénalité 20% appliquée: -" + String.format("%.2f", penalite) + " MAD)";
            }

            billet.setStatut("annulé");
            
            // Libérer une place dans la zone
            String key = billet.getMatch().getCodeMatch() + "_" + billet.getZone().getNomZone();
            int vendus = billetsVendusParZone.getOrDefault(key, 1);
            billetsVendusParZone.put(key, Math.max(0, vendus - 1));

            System.out.println("Billet " + codeBillet + " annulé. Remboursement: " + 
                String.format("%.2f", montantRemboursement) + " MAD" + message);
        }
    }

    // Méthode avec exception pour validation
    public double annulerBilletAvecValidation(int codeBillet) throws DonneeInvalideException {
        Billet billet = billetParCode.get(codeBillet);
        if (billet == null) {
            throw new DonneeInvalideException("Billet non trouvé avec le code: " + codeBillet);
        }
        if (billet.getStatut().equals("annulé")) {
            throw new DonneeInvalideException("Ce billet est déjà annulé");
        }

        // Calculer la pénalité si < 24h avant le match
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dateMatch = billet.getMatch().getDateHeure();
        long heuresAvantMatch = ChronoUnit.HOURS.between(maintenant, dateMatch);

        double montantRemboursement = billet.getMontant();
        boolean penaliteAppliquee = false;
        
        if (heuresAvantMatch < 24 && heuresAvantMatch > 0) {
            penaliteAppliquee = true;
            montantRemboursement = billet.getMontant() * (1 - PENALITE_ANNULATION);
        }

        billet.setStatut("annulé");
        
        // Libérer une place dans la zone
        String key = billet.getMatch().getCodeMatch() + "_" + billet.getZone().getNomZone();
        int vendus = billetsVendusParZone.getOrDefault(key, 1);
        billetsVendusParZone.put(key, Math.max(0, vendus - 1));

        if (penaliteAppliquee) {
            System.out.println("[!] Penalite de 20% appliquee (annulation a moins de 24h du match)");
        }

        return montantRemboursement;
    }

    // ==================== INTERFACE PAYABLE ====================

    @Override
    public void payer(Billet billet, String modePaiement) throws PaiementInvalideException {
        // Valider le mode de paiement
        if (modePaiement == null || modePaiement.trim().isEmpty()) {
            throw new PaiementInvalideException("Le mode de paiement ne peut pas être vide");
        }
        
        List<String> modesValides = List.of("carte bancaire", "mobile money", "espèces", "cb", "mm");
        String modeNormalise = modePaiement.toLowerCase().trim();
        
        if (!modesValides.contains(modeNormalise)) {
            throw new PaiementInvalideException("Mode de paiement invalide: " + modePaiement + 
                ". Modes acceptés: carte bancaire, mobile money, espèces");
        }

        // Normaliser le mode
        if (modeNormalise.equals("cb")) modeNormalise = "carte bancaire";
        if (modeNormalise.equals("mm")) modeNormalise = "mobile money";

        // Créer le paiement
        String reference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateHeure = LocalDateTime.now().format(formatter);

        Paiment paiement = new Paiment(reference, billet, modeNormalise, "réussi", dateHeure);
        paiements.add(paiement);

        // Mettre à jour le statut du billet
        billet.setStatut("payé");
    }

    @Override
    public boolean verifierPaiement(Billet billet) {
        for (Paiment p : paiements) {
            if (p.getBillet().getCodeBillet() == billet.getCodeBillet() && 
                p.getStatut().equals("réussi")) {
                return true;
            }
        }
        return false;
    }

    // ==================== RECHERCHE DE BILLETS ====================

    public Billet findBilletByCode(int codeBillet) {
        return billetParCode.get(codeBillet);
    }

    public ArrayList<Billet> findBilletsByClient(int clientId) {
        ArrayList<Billet> result = new ArrayList<>();
        for (Billet b : billets) {
            if (b.getClient().getId() == clientId) {
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<Billet> findBilletsByMatch(String codeMatch) {
        ArrayList<Billet> result = new ArrayList<>();
        for (Billet b : billets) {
            if (b.getMatch().getCodeMatch().equals(codeMatch)) {
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<Billet> findBilletsActifs() {
        ArrayList<Billet> result = new ArrayList<>();
        for (Billet b : billets) {
            if (!b.getStatut().equals("annulé")) {
                result.add(b);
            }
        }
        return result;
    }

    // ==================== TRIS ====================

    public ArrayList<Billet> trierBilletsParMontant(boolean decroissant) {
        ArrayList<Billet> liste = new ArrayList<>(findBilletsActifs());
        if (decroissant) {
            Collections.sort(liste, Comparator.comparingDouble(Billet::getMontant).reversed());
        } else {
            Collections.sort(liste, Comparator.comparingDouble(Billet::getMontant));
        }
        return liste;
    }

    public ArrayList<Billet> trierBilletsParDate(boolean recent) {
        ArrayList<Billet> liste = new ArrayList<>(findBilletsActifs());
        if (recent) {
            Collections.sort(liste, Comparator.comparing(Billet::getDateCreation).reversed());
        } else {
            Collections.sort(liste, Comparator.comparing(Billet::getDateCreation));
        }
        return liste;
    }

    public ArrayList<Billet> trierBilletsParZone() {
        ArrayList<Billet> liste = new ArrayList<>(findBilletsActifs());
        Collections.sort(liste, Comparator.comparing(b -> b.getZone().getNomZone()));
        return liste;
    }

    // Tri multi-critères (Bonus)
    public ArrayList<Billet> trierBilletsMultiCriteres(String critere1, String critere2) {
        ArrayList<Billet> liste = new ArrayList<>(findBilletsActifs());
        
        Comparator<Billet> comparator = getComparator(critere1);
        if (critere2 != null && !critere2.isEmpty()) {
            comparator = comparator.thenComparing(getComparator(critere2));
        }
        
        Collections.sort(liste, comparator);
        return liste;
    }

    private Comparator<Billet> getComparator(String critere) {
        switch (critere.toLowerCase()) {
            case "montant":
                return Comparator.comparingDouble(Billet::getMontant).reversed();
            case "date":
                return Comparator.comparing(Billet::getDateCreation).reversed();
            case "zone":
                return Comparator.comparing(b -> b.getZone().getNomZone());
            case "match":
                return Comparator.comparing(b -> b.getMatch().getCodeMatch());
            default:
                return Comparator.comparingInt(Billet::getCodeBillet);
        }
    }

    // ==================== RAPPORTS & STATISTIQUES ====================

    // Chiffre d'affaires total
    public double getChiffreAffairesTotal() {
        double total = 0;
        for (Billet b : billets) {
            if (!b.getStatut().equals("annulé")) {
                total += b.getMontant();
            }
        }
        return total;
    }

    // CA par match
    public HashMap<String, Double> getChiffreAffairesParMatch() {
        HashMap<String, Double> caParMatch = new HashMap<>();
        for (Billet b : billets) {
            if (!b.getStatut().equals("annulé")) {
                String key = b.getMatch().getEquipeA() + " vs " + b.getMatch().getEquipeB();
                caParMatch.put(key, caParMatch.getOrDefault(key, 0.0) + b.getMontant());
            }
        }
        return caParMatch;
    }

    // Top matchs par CA
    public List<Map.Entry<String, Double>> getTopMatchs(int limit) {
        HashMap<String, Double> caParMatch = getChiffreAffairesParMatch();
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(caParMatch.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    // Taux de remplissage par zone
    public HashMap<String, String> getTauxRemplissageParZone() {
        HashMap<String, String> taux = new HashMap<>();
        
        for (Map.Entry<String, ArrayList<ZonePlace>> entry : zoneParMatch.entrySet()) {
            String codeMatch = entry.getKey();
            Match match = findMatchByCode(codeMatch);
            if (match == null) continue;
            
            for (ZonePlace zone : entry.getValue()) {
                String key = codeMatch + "_" + zone.getNomZone();
                int vendus = billetsVendusParZone.getOrDefault(key, 0);
                int capacite = zone.getCapacite();
                double pourcentage = (capacite > 0) ? (double) vendus / capacite * 100 : 0;
                
                String label = match.getEquipeA() + " vs " + match.getEquipeB() + " - " + zone.getNomZone();
                taux.put(label, String.format("%d/%d (%.1f%%)", vendus, capacite, pourcentage));
            }
        }
        return taux;
    }

    // Nombre de billets vendus
    public int getNombreBilletsVendus() {
        int count = 0;
        for (Billet b : billets) {
            if (!b.getStatut().equals("annulé")) {
                count++;
            }
        }
        return count;
    }

    // Nombre de billets annulés
    public int getNombreBilletsAnnules() {
        int count = 0;
        for (Billet b : billets) {
            if (b.getStatut().equals("annulé")) {
                count++;
            }
        }
        return count;
    }

    // Afficher le rapport complet
    public void afficherRapportComplet() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              RAPPORT DE VENTES - CAN 2025");
        System.out.println("=".repeat(60));
        
        System.out.println("\nSTATISTIQUES GENERALES:");
        System.out.println("  - Nombre total de matchs: " + matches.size());
        System.out.println("  - Nombre total de clients: " + clients.size());
        System.out.println("  - Nombre de medias: " + getMediaClients().size());
        System.out.println("  - Billets vendus: " + getNombreBilletsVendus());
        System.out.println("  - Billets annules: " + getNombreBilletsAnnules());
        
        System.out.println("\nCHIFFRE D'AFFAIRES:");
        System.out.println("  - CA Total: " + String.format("%,.2f", getChiffreAffairesTotal()) + " MAD");
        
        System.out.println("\nTOP 5 MATCHS (par CA):");
        List<Map.Entry<String, Double>> topMatchs = getTopMatchs(5);
        int rang = 1;
        for (Map.Entry<String, Double> entry : topMatchs) {
            System.out.println("  " + rang + ". " + entry.getKey() + " - " + 
                String.format("%,.2f", entry.getValue()) + " MAD");
            rang++;
        }
        
        System.out.println("\nTAUX DE REMPLISSAGE PAR ZONE:");
        HashMap<String, String> tauxRemplissage = getTauxRemplissageParZone();
        for (Map.Entry<String, String> entry : tauxRemplissage.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\n" + "=".repeat(60));
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    public void afficherClients() {
        System.out.println("\n--- Liste des clients ---");
        for (Client c : clients) {
            String type = (c instanceof Media) ? "Média" : "Spectateur";
            String accreditation = "";
            if (c instanceof Media) {
                accreditation = ((Media) c).isAccredite() ? " [ACCRÉDITÉ]" : " [NON ACCRÉDITÉ]";
            }
            System.out.println("  ID: " + c.getId() + " | " + c.getNom() + " | " + c.getEmail() + 
                " | " + type + accreditation);
        }
    }

    public void afficherMatchs() {
        System.out.println("\n--- Liste des matchs ---");
        for (Match m : matches) {
            System.out.println("  " + m);
            ArrayList<ZonePlace> zones = zoneParMatch.get(m.getCodeMatch());
            if (zones != null) {
                for (ZonePlace z : zones) {
                    String key = m.getCodeMatch() + "_" + z.getNomZone();
                    int vendus = billetsVendusParZone.getOrDefault(key, 0);
                    System.out.println("    " + z.getNomZone() + " (capacité: " + z.getCapacite() + 
                        ", vendus: " + vendus + ", coef: " + z.getCoefficientPrix() + ")");
                }
            }
        }
    }

    public void afficherBilletsClient(int clientId) {
        Client client = findClientById(clientId);
        if (client == null) {
            System.out.println("Client non trouvé.");
            return;
        }
        
        ArrayList<Billet> billetsClient = findBilletsByClient(clientId);
        System.out.println("\n--- Billets de " + client.getNom() + " ---");
        if (billetsClient.isEmpty()) {
            System.out.println("  Aucun billet trouvé pour ce client.");
        } else {
            for (Billet b : billetsClient) {
                System.out.println("  " + b);
            }
        }
    }

    // ==================== RÉINITIALISATION POUR TESTS ====================

    public void reinitialiser() {
        clients.clear();
        matches.clear();
        billets.clear();
        paiements.clear();
        zoneParMatch.clear();
        billetsVendusParZone.clear();
        billetParCode.clear();
        Client.resetIdCounter();
        Billet.resetCompteur();
    }

    // ==================== SAUVEGARDE / CHARGEMENT JSON ====================

    /**
     * Sauvegarde toutes les données dans des fichiers JSON
     */
    public void sauvegarderDonnees() throws IOException {
        JsonStorage.sauvegarderTout(matches, clients, billets, zoneParMatch, billetsVendusParZone);
    }

    /**
     * Charge les données depuis les fichiers JSON
     */
    @SuppressWarnings("unchecked")
    public void chargerDonnees() throws IOException {
        // Réinitialiser les compteurs
        reinitialiser();
        
        // Charger les matchs
        this.matches = JsonStorage.chargerMatchs();
        
        // Charger les clients
        this.clients = JsonStorage.chargerClients();
        
        // Charger les zones
        Map<String, Object> zonesResult = JsonStorage.chargerZones();
        this.zoneParMatch = (HashMap<String, ArrayList<ZonePlace>>) zonesResult.get("zones");
        this.billetsVendusParZone = (HashMap<String, Integer>) zonesResult.get("vendus");
        
        // Charger les billets
        this.billets = JsonStorage.chargerBillets(matches, clients, zoneParMatch);
        
        // Reconstruire la HashMap des billets par code
        for (Billet b : billets) {
            billetParCode.put(b.getCodeBillet(), b);
        }
    }

    /**
     * Vérifie si des données sauvegardées existent
     */
    public boolean donneesExistent() {
        return JsonStorage.donneesExistent();
    }

    public HashMap<String, Integer> getBilletsVendusParZone() {
        return billetsVendusParZone;
    }
}
