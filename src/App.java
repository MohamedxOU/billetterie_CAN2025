import java.util.Scanner;

import services.TicketingService;

public class App {
    public static void main(String[] args) throws Exception {
        
        TicketingService ticketingService = new TicketingService();
        Scanner scanner = new Scanner(System.in);
        boolean active = true;
        while (active) {
            System.out.println("Menu Principal:");
            System.out.println("1. Ajouter match / zones de places");
            System.out.println("2. Ajouter client (spectateur/média)");
            System.out.println("3. Accréditer média");
            System.out.println("4. Vendre billet");
            System.out.println("5. Annuler billet");
            System.out.println("6. Afficher billets d’un client");
            System.out.println("7. Rapports (CA total, top matchs, taux de remplissage par zone)");
            System.out.println("8. Quitter");
           
            System.out.print("Choisissez une option: ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer la nouvelle ligne

            switch (choix) {
                case 1:
                    System.out.println("Entez code match : ");
                    String codeMatch = scanner.nextLine();
                    //verifier si le match existe déjà
                    if (ticketingService.getMatches().stream().anyMatch(m -> m.getCodeMatch().equals(codeMatch))) {
                        System.out.println("Le match avec le code " + codeMatch + " existe déjà.");
                        break;
                    }
                    
                    System.out.println("Entez équipe A : ");
                    String equipeA = scanner.nextLine();
                    System.out.println("Entez équipe B : ");
                    String equipeB = scanner.nextLine();
                    System.out.println("Entez stade : ");
                    String stade = scanner.nextLine();
                    System.out.println("Entez date (DD/MM/YYYY) : ");
                    String date = scanner.nextLine();
                    System.out.println("Entez heure (HH:MM) : ");
                    String heure = scanner.nextLine();
                    System.out.println("Entez importance (1: faible, 2: moyenne, 3: élevée) : ");
                    int importance = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne

                    ticketingService.ajouterMatch(codeMatch, equipeA, equipeB, stade, date, heure, importance);
                    System.out.println("Match ajouté avec succès.");

                    //ajouter des zones de places pour ce match
                    //...
                    //boucle pour ajouter plusieurs zones
                    boolean ajouterPlusZones = true;
                    while (ajouterPlusZones) {
                        /*private String nomZone;
                        private int capacite;
                        private double coefficientPrix; */
                        System.out.println("Entez nom de la zone : ");
                        String nomZone = scanner.nextLine();
                        System.out.println("Entez capacité de la zone : ");
                        int capacite = scanner.nextInt();
                        System.out.println("Entez coefficient de prix de la zone : ");
                        double coefficientPrix = scanner.nextDouble();
                        scanner.nextLine(); // Consommer la nouvelle ligne
                        ticketingService.ajouterZonePlace(codeMatch, new models.ZonePlace(nomZone, capacite, coefficientPrix));
                        System.out.println("Zone de place ajoutée avec succès.");
                        System.out.println("Voulez-vous ajouter une autre zone de place pour ce match ? (oui/non) : ");
                        String reponse = scanner.nextLine();
                        if (!reponse.equalsIgnoreCase("oui")) {
                            ajouterPlusZones = false;
                        }
                    }

                    break;

                case 2:
                    System.out.println("Entez nom du client : ");
                    String nomClient = scanner.nextLine();
                    System.out.println("Entez email du client : ");
                    String emailClient = scanner.nextLine();
                    System.out.println("Le client est-il un média ? (oui/non) : ");
                    String isMediaStr = scanner.nextLine();
                    boolean isMedia = isMediaStr.equalsIgnoreCase("oui");
                    ticketingService.ajouterClient(nomClient, emailClient, isMedia);
                    System.out.println("Client ajouté avec succès.");

                    break;
                
                case 3:
                    // Accréditer média
                    //public ArrayList<Client> getClients() afficher la liste des clients type Media
                    System.out.println("Liste des médias :");
                    ticketingService.getClients().stream()
                        .filter(c -> c instanceof models.Media)
                        .forEach(c -> System.out.println(c));

                    System.out.println("Entrez l'ID du média à accréditer/désaccréditer : ");
                    int idMedia = scanner.nextInt();
                    scanner.nextLine(); // Consommer la nouvelle ligne
                    models.Media media = (models.Media) ticketingService.getClients().stream()
                        .filter(c -> c instanceof models.Media && c.getId() == idMedia)
                        .findFirst()
                        .orElse(null);
                    if (media != null) {
                        ticketingService.toggleAccreditation(media);
                        System.out.println("Accréditation du média mise à jour.");
                    } else {
                        System.out.println("Média non trouvé.");
                    }
                    break;
                
                case 4:
                    // Vendre billet
                    //...
                    break;
                case 8:
                    active = false;
                    System.out.println("Au revoir!");
                    break;
                default:
                    break;
            }
       }

        
    } 
}
