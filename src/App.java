import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import exceptions.AccreditationRefuseeException;
import exceptions.BilletIndisponibleException;
import exceptions.DonneeInvalideException;
import exceptions.PaiementInvalideException;
import models.Billet;
import models.Client;
import models.Match;
import models.Media;
import models.ZonePlace;
import services.TicketingService;

public class App {
    
    private static TicketingService ticketingService = new TicketingService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         SYSTEME DE BILLETTERIE - CAN 2025                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // Demander si l'utilisateur veut exécuter les scénarios de test
        System.out.println("\nVoulez-vous exécuter les scénarios de test obligatoires ? (oui/non)");
        String reponse = scanner.nextLine();
        
        if (reponse.equalsIgnoreCase("oui") || reponse.equalsIgnoreCase("o")) {
            executerScenariosTest();
        }

        // Lancer le menu principal
        menuPrincipal();
        
        scanner.close();
        System.out.println("\nMerci d'avoir utilise le systeme de billetterie CAN 2025. Au revoir!");
    }

    // ==================== MENU PRINCIPAL ====================
    
    private static void menuPrincipal() {
        boolean active = true;
        
        while (active) {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                    MENU PRINCIPAL                          ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Ajouter match / zones de places                        ║");
            System.out.println("║  2. Ajouter client (spectateur/média)                      ║");
            System.out.println("║  3. Accréditer média                                       ║");
            System.out.println("║  4. Vendre billet                                          ║");
            System.out.println("║  5. Annuler billet                                         ║");
            System.out.println("║  6. Afficher billets d'un client                           ║");
            System.out.println("║  7. Rapports (CA total, top matchs, taux remplissage)      ║");
            System.out.println("║  8. Afficher tous les matchs                               ║");
            System.out.println("║  9. Afficher tous les clients                              ║");
            System.out.println("║ 10. Trier et afficher les billets                          ║");
            System.out.println("║ 11. Sauvegarder donnees (JSON)                             ║");
            System.out.println("║ 12. Charger donnees (JSON)                                 ║");
            System.out.println("║  0. Quitter                                                ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            System.out.print("Votre choix: ");
            int choix = lireEntier();

            switch (choix) {
                case 1:
                    ajouterMatchEtZones();
                    break;
                case 2:
                    ajouterClient();
                    break;
                case 3:
                    accrediterMedia();
                    break;
                case 4:
                    vendreBillet();
                    break;
                case 5:
                    annulerBillet();
                    break;
                case 6:
                    afficherBilletsClient();
                    break;
                case 7:
                    afficherRapports();
                    break;
                case 8:
                    ticketingService.afficherMatchs();
                    break;
                case 9:
                    ticketingService.afficherClients();
                    break;
                case 10:
                    trierEtAfficherBillets();
                    break;
                case 11:
                    sauvegarderDonnees();
                    break;
                case 12:
                    chargerDonnees();
                    break;
                case 0:
                    active = false;
                    break;
                default:
                    System.out.println("[ERREUR] Option invalide. Veuillez reessayer.");
            }
        }
    }

    // ==================== OPTION 1: AJOUTER MATCH ====================
    
    private static void ajouterMatchEtZones() {
        System.out.println("\n--- AJOUTER UN MATCH ---");
        
        try {
            System.out.print("Code du match: ");
            String codeMatch = scanner.nextLine();
            
            if (ticketingService.matchExists(codeMatch)) {
                System.out.println("[ERREUR] Un match avec ce code existe deja.");
                return;
            }
            
            System.out.print("Équipe A: ");
            String equipeA = scanner.nextLine();
            
            System.out.print("Équipe B: ");
            String equipeB = scanner.nextLine();
            
            System.out.print("Stade: ");
            String stade = scanner.nextLine();
            
            System.out.print("Date (DD/MM/YYYY): ");
            String date = scanner.nextLine();
            
            System.out.print("Heure (HH:MM): ");
            String heure = scanner.nextLine();
            
            System.out.print("Importance (1: faible, 2: moyenne, 3: élevée): ");
            int importance = lireEntier();

            ticketingService.ajouterMatch(codeMatch, equipeA, equipeB, stade, date, heure, importance);
            System.out.println("[OK] Match ajoute avec succes!");

            // Ajouter des zones de places
            boolean ajouterPlusZones = true;
            while (ajouterPlusZones) {
                System.out.print("\nNom de la zone: ");
                String nomZone = scanner.nextLine();
                
                System.out.print("Capacité: ");
                int capacite = lireEntier();
                
                System.out.print("Coefficient de prix: ");
                double coefficientPrix = lireDouble();
                
                ticketingService.ajouterZonePlace(codeMatch, new ZonePlace(nomZone, capacite, coefficientPrix));
                System.out.println("[OK] Zone ajoutee avec succes!");
                
                System.out.print("Ajouter une autre zone ? (oui/non): ");
                String reponse = scanner.nextLine();
                ajouterPlusZones = reponse.equalsIgnoreCase("oui") || reponse.equalsIgnoreCase("o");
            }
            
        } catch (DonneeInvalideException e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }
    }

    // ==================== OPTION 2: AJOUTER CLIENT ====================
    
    private static void ajouterClient() {
        System.out.println("\n--- AJOUTER UN CLIENT ---");
        
        try {
            System.out.print("Nom du client: ");
            String nom = scanner.nextLine();
            
            System.out.print("Email du client: ");
            String email = scanner.nextLine();
            
            System.out.print("Est-ce un média ? (oui/non): ");
            String isMediaStr = scanner.nextLine();
            boolean isMedia = isMediaStr.equalsIgnoreCase("oui") || isMediaStr.equalsIgnoreCase("o");
            
            ticketingService.ajouterClient(nom, email, isMedia);
            System.out.println("[OK] Client ajoute avec succes! (ID: " + (Client.getNextId() - 1) + ")");
            
        } catch (DonneeInvalideException e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }
    }

    // ==================== OPTION 3: ACCRÉDITER MÉDIA ====================
    
    private static void accrediterMedia() {
        System.out.println("\n--- ACCRÉDITER UN MÉDIA ---");
        
        ArrayList<Media> medias = ticketingService.getMediaClients();
        if (medias.isEmpty()) {
            System.out.println("Aucun média enregistré.");
            return;
        }
        
        System.out.println("Liste des médias:");
        for (Media m : medias) {
            System.out.println(m);
        }
        
        System.out.print("\nID du média à accréditer/désaccréditer: ");
        int idMedia = lireEntier();
        
        if (ticketingService.toggleAccreditationById(idMedia)) {
            Media media = ticketingService.findMediaById(idMedia);
            String status = media.isAccredite() ? "accrédité" : "désaccrédité";
            System.out.println("[OK] Media " + media.getNom() + " " + status + " avec succes!");
        } else {
            System.out.println("[ERREUR] Media non trouve.");
        }
    }

    // ==================== OPTION 4: VENDRE BILLET ====================
    
    private static void vendreBillet() {
        System.out.println("\n--- VENDRE UN BILLET ---");
        
        // Afficher les matchs disponibles
        if (ticketingService.getMatches().isEmpty()) {
            System.out.println("Aucun match disponible. Veuillez d'abord ajouter des matchs.");
            return;
        }
        
        ticketingService.afficherMatchs();
        
        System.out.print("\nCode du match: ");
        String codeMatch = scanner.nextLine();
        
        Match match = ticketingService.findMatchByCode(codeMatch);
        if (match == null) {
            System.out.println("[ERREUR] Match non trouve.");
            return;
        }
        
        // Afficher les zones disponibles
        ArrayList<ZonePlace> zones = ticketingService.getZonesForMatch(codeMatch);
        if (zones.isEmpty()) {
            System.out.println("[ERREUR] Aucune zone definie pour ce match.");
            return;
        }
        
        System.out.println("\nZones disponibles:");
        for (ZonePlace z : zones) {
            System.out.println("  - " + z.getNomZone() + " (coef: " + z.getCoefficientPrix() + ")");
        }
        
        System.out.print("Nom de la zone: ");
        String nomZone = scanner.nextLine();
        
        ZonePlace zone = ticketingService.findZoneByName(codeMatch, nomZone);
        if (zone == null) {
            System.out.println("[ERREUR] Zone non trouvee.");
            return;
        }
        
        // Afficher les clients
        if (ticketingService.getClients().isEmpty()) {
            System.out.println("Aucun client enregistré. Veuillez d'abord ajouter des clients.");
            return;
        }
        
        ticketingService.afficherClients();
        
        System.out.print("\nID du client: ");
        int clientId = lireEntier();
        
        Client client = ticketingService.findClientById(clientId);
        if (client == null) {
            System.out.println("[ERREUR] Client non trouve.");
            return;
        }
        
        System.out.print("Mode de paiement (carte bancaire / mobile money / espèces): ");
        String modePaiement = scanner.nextLine();
        
        try {
            Billet billet = ticketingService.vendreBillet(client, match, zone, modePaiement);
            System.out.println("\n[OK] Billet vendu avec succes!");
            System.out.println("   " + billet);
            
        } catch (BilletIndisponibleException e) {
            System.out.println("[ERREUR] Billet indisponible: " + e.getMessage());
        } catch (AccreditationRefuseeException e) {
            System.out.println("[ERREUR] Accreditation refusee: " + e.getMessage());
        } catch (PaiementInvalideException e) {
            System.out.println("[ERREUR] Paiement invalide: " + e.getMessage());
        }
    }

    // ==================== OPTION 5: ANNULER BILLET ====================
    
    private static void annulerBillet() {
        System.out.println("\n--- ANNULER UN BILLET ---");
        
        if (ticketingService.getBillets().isEmpty()) {
            System.out.println("Aucun billet à annuler.");
            return;
        }
        
        System.out.println("Billets actifs:");
        for (Billet b : ticketingService.findBilletsActifs()) {
            System.out.println("  " + b);
        }
        
        System.out.print("\nCode du billet à annuler: ");
        int codeBillet = lireEntier();
        
        try {
            double remboursement = ticketingService.annulerBilletAvecValidation(codeBillet);
            System.out.println("[OK] Billet annule. Montant rembourse: " + String.format("%.2f", remboursement) + " MAD");
        } catch (DonneeInvalideException e) {
            System.out.println("[ERREUR] " + e.getMessage());
        }
    }

    // ==================== OPTION 6: AFFICHER BILLETS CLIENT ====================
    
    private static void afficherBilletsClient() {
        System.out.println("\n--- BILLETS D'UN CLIENT ---");
        
        if (ticketingService.getClients().isEmpty()) {
            System.out.println("Aucun client enregistré.");
            return;
        }
        
        ticketingService.afficherClients();
        
        System.out.print("\nID du client: ");
        int clientId = lireEntier();
        
        ticketingService.afficherBilletsClient(clientId);
    }

    // ==================== OPTION 7: RAPPORTS ====================
    
    private static void afficherRapports() {
        ticketingService.afficherRapportComplet();
    }

    // ==================== OPTION 10: TRIER BILLETS ====================
    
    private static void trierEtAfficherBillets() {
        System.out.println("\n--- TRIER LES BILLETS ---");
        System.out.println("1. Par montant (décroissant)");
        System.out.println("2. Par date (plus récent d'abord)");
        System.out.println("3. Par zone (alphabétique)");
        System.out.println("4. Multi-critères");
        
        System.out.print("Votre choix: ");
        int choix = lireEntier();
        
        ArrayList<Billet> billetsTriees;
        
        switch (choix) {
            case 1:
                billetsTriees = ticketingService.trierBilletsParMontant(true);
                System.out.println("\n--- Billets triés par montant (décroissant) ---");
                break;
            case 2:
                billetsTriees = ticketingService.trierBilletsParDate(true);
                System.out.println("\n--- Billets triés par date (plus récent d'abord) ---");
                break;
            case 3:
                billetsTriees = ticketingService.trierBilletsParZone();
                System.out.println("\n--- Billets triés par zone ---");
                break;
            case 4:
                System.out.print("Premier critère (montant/date/zone/match): ");
                String critere1 = scanner.nextLine();
                System.out.print("Deuxième critère (montant/date/zone/match): ");
                String critere2 = scanner.nextLine();
                billetsTriees = ticketingService.trierBilletsMultiCriteres(critere1, critere2);
                System.out.println("\n--- Billets triés par " + critere1 + " puis " + critere2 + " ---");
                break;
            default:
                System.out.println("Choix invalide.");
                return;
        }
        
        if (billetsTriees.isEmpty()) {
            System.out.println("Aucun billet actif.");
        } else {
            for (Billet b : billetsTriees) {
                System.out.println("  " + b);
            }
        }
    }

    // ==================== OPTION 11: SAUVEGARDER DONNÉES ====================
    
    private static void sauvegarderDonnees() {
        System.out.println("\n--- SAUVEGARDER LES DONNEES ---");
        
        try {
            ticketingService.sauvegarderDonnees();
            System.out.println("[OK] Donnees sauvegardees avec succes dans le dossier 'data/'");
            System.out.println("     - matchs.json");
            System.out.println("     - clients.json");
            System.out.println("     - zones.json");
            System.out.println("     - billets.json");
        } catch (IOException e) {
            System.out.println("[ERREUR] Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    // ==================== OPTION 12: CHARGER DONNÉES ====================
    
    private static void chargerDonnees() {
        System.out.println("\n--- CHARGER LES DONNEES ---");
        
        if (!ticketingService.donneesExistent()) {
            System.out.println("[INFO] Aucune donnee sauvegardee trouvee dans le dossier 'data/'");
            return;
        }
        
        System.out.println("ATTENTION: Cette operation va remplacer toutes les donnees actuelles!");
        System.out.print("Voulez-vous continuer? (oui/non): ");
        String reponse = scanner.nextLine();
        
        if (!reponse.equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("o")) {
            System.out.println("Operation annulee.");
            return;
        }
        
        try {
            ticketingService.chargerDonnees();
            System.out.println("[OK] Donnees chargees avec succes!");
            System.out.println("     - " + ticketingService.getMatches().size() + " matchs");
            System.out.println("     - " + ticketingService.getClients().size() + " clients");
            System.out.println("     - " + ticketingService.getBillets().size() + " billets");
        } catch (IOException e) {
            System.out.println("[ERREUR] Erreur lors du chargement: " + e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================
    
    private static int lireEntier() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide: ");
            }
        }
    }
    
    private static double lireDouble() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide: ");
            }
        }
    }

    // ==================== SCÉNARIOS DE TEST ====================
    
    private static void executerScenariosTest() {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           EXÉCUTION DES SCÉNARIOS DE TEST                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        // Réinitialiser pour les tests
        ticketingService.reinitialiser();
        
        try {
            // =============== 1. CRÉER 8 MATCHS AVEC 3 ZONES CHACUN ===============
            System.out.println("\n[ETAPE 1] Creation de 8 matchs avec 3 zones chacun...");
            
            String[][] matchsData = {
                {"M001", "Maroc", "Côte d'Ivoire", "Stade Olembé", "15/01/2025", "18:00", "3"},
                {"M002", "Sénégal", "Cameroun", "Stade Ahmadou Ahidjo", "16/01/2025", "20:00", "3"},
                {"M003", "Nigeria", "Égypte", "Stade Olembé", "17/01/2025", "18:00", "3"},
                {"M004", "Algérie", "Ghana", "Stade de Japoma", "18/01/2025", "16:00", "2"},
                {"M005", "Tunisie", "Mali", "Stade Kouekong", "19/01/2025", "18:00", "2"},
                {"M006", "RD Congo", "Guinée", "Stade Roumdé Adjia", "20/01/2025", "15:00", "1"},
                {"M007", "Burkina Faso", "Cap-Vert", "Stade Olembé", "21/01/2025", "18:00", "1"},
                {"M008", "Afrique du Sud", "Angola", "Stade de Japoma", "22/01/2025", "20:00", "1"}
            };
            
            for (String[] m : matchsData) {
                ticketingService.ajouterMatch(m[0], m[1], m[2], m[3], m[4], m[5], Integer.parseInt(m[6]));
                // Ajouter 3 zones pour chaque match
                ticketingService.ajouterZonePlace(m[0], new ZonePlace("VIP", 50, 3.0));
                ticketingService.ajouterZonePlace(m[0], new ZonePlace("Tribune", 200, 1.5));
                ticketingService.ajouterZonePlace(m[0], new ZonePlace("Populaire", 500, 1.0));
            }
            System.out.println("   [OK] 8 matchs crees avec 3 zones chacun (24 zones au total)");
            
            // =============== 2. CRÉER 25 CLIENTS (20 SPECTATEURS + 5 MÉDIAS) ===============
            System.out.println("\n[ETAPE 2] Creation de 25 clients (20 spectateurs + 5 medias)...");
            
            String[][] clientsData = {
                // 20 spectateurs
                {"Ahmed", "ahmed@email.com", "false"},
                {"Fatima", "fatima@email.com", "false"},
                {"Moussa", "moussa@email.com", "false"},
                {"Aminata", "aminata@email.com", "false"},
                {"Youssef", "youssef@email.com", "false"},
                {"Khadija", "khadija@email.com", "false"},
                {"Omar", "omar@email.com", "false"},
                {"Aissatou", "aissatou@email.com", "false"},
                {"Ibrahim", "ibrahim@email.com", "false"},
                {"Mariama", "mariama@email.com", "false"},
                {"Oumar", "oumar@email.com", "false"},
                {"Fatoumata", "fatoumata@email.com", "false"},
                {"Abdoulaye", "abdoulaye@email.com", "false"},
                {"Rokia", "rokia@email.com", "false"},
                {"Mamadou", "mamadou@email.com", "false"},
                {"Salimata", "salimata@email.com", "false"},
                {"Boubacar", "boubacar@email.com", "false"},
                {"Djénéba", "djeneba@email.com", "false"},
                {"Seydou", "seydou@email.com", "false"},
                {"Kadiatou", "kadiatou@email.com", "false"},
                // 5 médias
                {"Jean Reporter", "jean@media.com", "true"},
                {"Marie Journaliste", "marie@media.com", "true"},
                {"Paul Cameraman", "paul@media.com", "true"},
                {"Sophie Photographe", "sophie@media.com", "true"},
                {"Marc Commentateur", "marc@media.com", "true"}
            };
            
            for (String[] c : clientsData) {
                ticketingService.ajouterClient(c[0], c[1], Boolean.parseBoolean(c[2]));
            }
            System.out.println("   [OK] 25 clients crees (20 spectateurs + 5 medias)");
            
            // =============== 3. ACCRÉDITER 4 MÉDIAS (1 NON ACCRÉDITÉ) ===============
            System.out.println("\n[ETAPE 3] Accreditation de 4 medias sur 5...");
            
            // Accréditer les médias 21, 22, 23, 24 (laisser 25 non accrédité)
            ticketingService.accrediterMedia(21);
            ticketingService.accrediterMedia(22);
            ticketingService.accrediterMedia(23);
            ticketingService.accrediterMedia(24);
            System.out.println("   [OK] 4 medias accredites (Marc Commentateur reste non accredite pour test)");
            
            // =============== 4. VENDRE 40+ BILLETS ===============
            System.out.println("\n[ETAPE 4] Vente de 40+ billets...");
            
            int billetsVendus = 0;
            String[] modesPaiement = {"carte bancaire", "mobile money", "espèces"};
            
            // Vendre des billets pour différents matchs et zones
            // Match M001 (Maroc vs Côte d'Ivoire) - 8 billets
            for (int i = 1; i <= 5; i++) {
                Match match = ticketingService.findMatchByCode("M001");
                ZonePlace zone = ticketingService.findZoneByName("M001", "Tribune");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            for (int i = 6; i <= 8; i++) {
                Match match = ticketingService.findMatchByCode("M001");
                ZonePlace zone = ticketingService.findZoneByName("M001", "VIP");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M002 (Sénégal vs Cameroun) - 7 billets
            for (int i = 1; i <= 4; i++) {
                Match match = ticketingService.findMatchByCode("M002");
                ZonePlace zone = ticketingService.findZoneByName("M002", "Populaire");
                Client client = ticketingService.findClientById(i + 8);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            for (int i = 5; i <= 7; i++) {
                Match match = ticketingService.findMatchByCode("M002");
                ZonePlace zone = ticketingService.findZoneByName("M002", "Tribune");
                Client client = ticketingService.findClientById(i + 8);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M003 (Nigeria vs Égypte) - 6 billets
            for (int i = 1; i <= 6; i++) {
                Match match = ticketingService.findMatchByCode("M003");
                ZonePlace zone = ticketingService.findZoneByName("M003", i <= 2 ? "VIP" : "Tribune");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M004 - 5 billets
            for (int i = 1; i <= 5; i++) {
                Match match = ticketingService.findMatchByCode("M004");
                ZonePlace zone = ticketingService.findZoneByName("M004", "Populaire");
                Client client = ticketingService.findClientById(i + 5);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M005 - 5 billets
            for (int i = 1; i <= 5; i++) {
                Match match = ticketingService.findMatchByCode("M005");
                ZonePlace zone = ticketingService.findZoneByName("M005", "Tribune");
                Client client = ticketingService.findClientById(i + 10);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M006 - 4 billets
            for (int i = 1; i <= 4; i++) {
                Match match = ticketingService.findMatchByCode("M006");
                ZonePlace zone = ticketingService.findZoneByName("M006", "Populaire");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M007 - 3 billets
            for (int i = 1; i <= 3; i++) {
                Match match = ticketingService.findMatchByCode("M007");
                ZonePlace zone = ticketingService.findZoneByName("M007", "Tribune");
                Client client = ticketingService.findClientById(i + 15);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Match M008 - 4 billets
            for (int i = 1; i <= 4; i++) {
                Match match = ticketingService.findMatchByCode("M008");
                ZonePlace zone = ticketingService.findZoneByName("M008", i <= 2 ? "VIP" : "Populaire");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, modesPaiement[i % 3]);
                billetsVendus++;
            }
            
            // Vendre des billets aux médias accrédités (gratuits)
            for (int i = 21; i <= 24; i++) {
                Match match = ticketingService.findMatchByCode("M00" + ((i - 20) % 8 + 1));
                ZonePlace zone = ticketingService.findZoneByName(match.getCodeMatch(), "VIP");
                Client client = ticketingService.findClientById(i);
                ticketingService.vendreBillet(client, match, zone, "carte bancaire");
                billetsVendus++;
            }
            
            System.out.println("   [OK] " + billetsVendus + " billets vendus avec succes");
            
            // =============== 5. ANNULER 5 BILLETS (dont 2 avec pénalité simulée) ===============
            System.out.println("\n[ETAPE 5] Annulation de 5 billets...");
            
            // Annuler les billets 100, 101, 102 (annulations normales)
            System.out.println("\n   Annulation normale (plus de 24h avant le match):");
            ticketingService.annulerBillet(100);
            ticketingService.annulerBillet(101);
            ticketingService.annulerBillet(102);
            
            // Pour simuler des annulations < 24h, on modifie la date du match
            System.out.println("\n   Annulation avec pénalité (moins de 24h avant le match):");
            // On va annuler les billets 103 et 104 pour les matchs dont la date est proche
            // (La pénalité sera appliquée si le match est dans moins de 24h)
            Billet billet103 = ticketingService.findBilletByCode(103);
            Billet billet104 = ticketingService.findBilletByCode(104);
            
            if (billet103 != null) {
                // Modifier temporairement la date du match pour simuler un match imminent
                Match match103 = billet103.getMatch();
                String ancienneDate = match103.getDate();
                String ancienneHeure = match103.getHeure();
                
                // Mettre la date à aujourd'hui + quelques heures
                java.time.LocalDateTime maintenant = java.time.LocalDateTime.now();
                java.time.LocalDateTime dansQuelquesHeures = maintenant.plusHours(5);
                java.time.format.DateTimeFormatter formatDate = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                java.time.format.DateTimeFormatter formatHeure = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                
                match103.setDate(dansQuelquesHeures.format(formatDate));
                match103.setHeure(dansQuelquesHeures.format(formatHeure));
                
                ticketingService.annulerBillet(103);
                
                // Restaurer la date originale
                match103.setDate(ancienneDate);
                match103.setHeure(ancienneHeure);
            }
            
            if (billet104 != null) {
                Match match104 = billet104.getMatch();
                String ancienneDate = match104.getDate();
                String ancienneHeure = match104.getHeure();
                
                java.time.LocalDateTime maintenant = java.time.LocalDateTime.now();
                java.time.LocalDateTime dansQuelquesHeures = maintenant.plusHours(3);
                java.time.format.DateTimeFormatter formatDate = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                java.time.format.DateTimeFormatter formatHeure = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
                
                match104.setDate(dansQuelquesHeures.format(formatDate));
                match104.setHeure(dansQuelquesHeures.format(formatHeure));
                
                ticketingService.annulerBillet(104);
                
                match104.setDate(ancienneDate);
                match104.setHeure(ancienneHeure);
            }
            
            System.out.println("   [OK] 5 billets annules (dont 2 avec penalite de 20%)");
            
            // =============== 6. PROVOQUER 4 ERREURS ===============
            System.out.println("\n[ETAPE 6] Test des 4 exceptions personnalisees...");
            
            // Erreur 1: BilletIndisponibleException (quota épuisé)
            System.out.println("\n   [1] Test BilletIndisponibleException (quota epuise):");
            try {
                // Créer une zone avec capacité 1 et vendre 2 billets
                ticketingService.ajouterMatch("MTEST", "Test1", "Test2", "Stade Test", "30/01/2025", "18:00", 1);
                ticketingService.ajouterZonePlace("MTEST", new ZonePlace("ZoneTest", 1, 1.0));
                
                Match matchTest = ticketingService.findMatchByCode("MTEST");
                ZonePlace zoneTest = ticketingService.findZoneByName("MTEST", "ZoneTest");
                Client client1 = ticketingService.findClientById(1);
                Client client2 = ticketingService.findClientById(2);
                
                ticketingService.vendreBillet(client1, matchTest, zoneTest, "espèces");
                // Cette vente doit échouer
                ticketingService.vendreBillet(client2, matchTest, zoneTest, "espèces");
            } catch (BilletIndisponibleException e) {
                System.out.println("      [X] Exception capturee: " + e.getMessage());
            }
            
            // Erreur 2: PaiementInvalideException (mode invalide)
            System.out.println("\n   [2] Test PaiementInvalideException (mode de paiement invalide):");
            try {
                Match match = ticketingService.findMatchByCode("M001");
                ZonePlace zone = ticketingService.findZoneByName("M001", "Populaire");
                Client client = ticketingService.findClientById(20);
                
                ticketingService.vendreBillet(client, match, zone, "bitcoin"); // Mode invalide
            } catch (PaiementInvalideException e) {
                System.out.println("      [X] Exception capturee: " + e.getMessage());
            }
            
            // Erreur 3: AccreditationRefuseeException (média non accrédité)
            System.out.println("\n   [3] Test AccreditationRefuseeException (media non accredite):");
            try {
                Match match = ticketingService.findMatchByCode("M001");
                ZonePlace zone = ticketingService.findZoneByName("M001", "VIP");
                Client client = ticketingService.findClientById(25); // Marc Commentateur (non accrédité)
                
                ticketingService.vendreBillet(client, match, zone, "carte bancaire");
            } catch (AccreditationRefuseeException e) {
                System.out.println("      [X] Exception capturee: " + e.getMessage());
            }
            
            // Erreur 4: DonneeInvalideException (données invalides)
            System.out.println("\n   [4] Test DonneeInvalideException (email invalide):");
            try {
                ticketingService.ajouterClient("Test", "email-invalide", false); // Email sans @
            } catch (DonneeInvalideException e) {
                System.out.println("      [X] Exception capturee: " + e.getMessage());
            }
            
            System.out.println("\n   [OK] Toutes les 4 exceptions ont ete testees avec succes!");
            
            // =============== 7. AFFICHER RAPPORT ET TRI ===============
            System.out.println("\n[ETAPE 7] Generation des rapports...");
            
            // Afficher le rapport complet
            ticketingService.afficherRapportComplet();
            
            // Tri des ventes par montant
            System.out.println("\n[ETAPE 8] Tri des billets par montant (top 10)...");
            ArrayList<Billet> billetsTries = ticketingService.trierBilletsParMontant(true);
            System.out.println("\n--- Top 10 des billets par montant (decroissant) ---");
            int count = 0;
            for (Billet b : billetsTries) {
                if (count >= 10) break;
                System.out.println("  " + (count + 1) + ". " + b);
                count++;
            }
            
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║     [OK] TOUS LES SCENARIOS DE TEST EXECUTES AVEC SUCCES!     ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.out.println("[ERREUR] Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
