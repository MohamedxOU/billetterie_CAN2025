package utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.*;

/**
 * Classe utilitaire pour la sauvegarde et le chargement des données en JSON.
 * Implémentation manuelle sans bibliothèque externe.
 */
public class JsonStorage {
    
    private static final String DATA_DIR = "data";
    private static final String MATCHS_FILE = "matchs.json";
    private static final String CLIENTS_FILE = "clients.json";
    private static final String BILLETS_FILE = "billets.json";
    private static final String ZONES_FILE = "zones.json";
    
    // ==================== SAUVEGARDE ====================
    
    /**
     * Sauvegarde tous les matchs dans un fichier JSON
     */
    public static void sauvegarderMatchs(ArrayList<Match> matchs) throws IOException {
        createDataDir();
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < matchs.size(); i++) {
            Match m = matchs.get(i);
            json.append("  {\n");
            json.append("    \"codeMatch\": \"").append(escapeJson(m.getCodeMatch())).append("\",\n");
            json.append("    \"equipeA\": \"").append(escapeJson(m.getEquipeA())).append("\",\n");
            json.append("    \"equipeB\": \"").append(escapeJson(m.getEquipeB())).append("\",\n");
            json.append("    \"stade\": \"").append(escapeJson(m.getStade())).append("\",\n");
            json.append("    \"date\": \"").append(escapeJson(m.getDate())).append("\",\n");
            json.append("    \"heure\": \"").append(escapeJson(m.getHeure())).append("\",\n");
            json.append("    \"importance\": ").append(m.getImportance()).append("\n");
            json.append("  }");
            if (i < matchs.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        writeFile(DATA_DIR + "/" + MATCHS_FILE, json.toString());
    }
    
    /**
     * Sauvegarde tous les clients dans un fichier JSON
     */
    public static void sauvegarderClients(ArrayList<Client> clients) throws IOException {
        createDataDir();
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < clients.size(); i++) {
            Client c = clients.get(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(c.getId()).append(",\n");
            json.append("    \"nom\": \"").append(escapeJson(c.getNom())).append("\",\n");
            json.append("    \"email\": \"").append(escapeJson(c.getEmail())).append("\",\n");
            json.append("    \"type\": \"").append(c instanceof Media ? "media" : "spectateur").append("\"");
            
            if (c instanceof Media) {
                Media m = (Media) c;
                json.append(",\n    \"accredite\": ").append(m.isAccredite());
            }
            
            json.append("\n  }");
            if (i < clients.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        writeFile(DATA_DIR + "/" + CLIENTS_FILE, json.toString());
    }
    
    /**
     * Sauvegarde toutes les zones par match dans un fichier JSON
     */
    public static void sauvegarderZones(HashMap<String, ArrayList<ZonePlace>> zoneParMatch, 
                                         HashMap<String, Integer> billetsVendusParZone) throws IOException {
        createDataDir();
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        int matchCount = 0;
        int totalMatchs = zoneParMatch.size();
        
        for (Map.Entry<String, ArrayList<ZonePlace>> entry : zoneParMatch.entrySet()) {
            String codeMatch = entry.getKey();
            ArrayList<ZonePlace> zones = entry.getValue();
            
            json.append("  \"").append(escapeJson(codeMatch)).append("\": [\n");
            
            for (int i = 0; i < zones.size(); i++) {
                ZonePlace z = zones.get(i);
                String key = codeMatch + "_" + z.getNomZone();
                int vendus = billetsVendusParZone.getOrDefault(key, 0);
                
                json.append("    {\n");
                json.append("      \"nomZone\": \"").append(escapeJson(z.getNomZone())).append("\",\n");
                json.append("      \"capacite\": ").append(z.getCapacite()).append(",\n");
                json.append("      \"coefficientPrix\": ").append(z.getCoefficientPrix()).append(",\n");
                json.append("      \"billetsVendus\": ").append(vendus).append("\n");
                json.append("    }");
                if (i < zones.size() - 1) json.append(",");
                json.append("\n");
            }
            
            json.append("  ]");
            matchCount++;
            if (matchCount < totalMatchs) json.append(",");
            json.append("\n");
        }
        
        json.append("}");
        writeFile(DATA_DIR + "/" + ZONES_FILE, json.toString());
    }
    
    /**
     * Sauvegarde tous les billets dans un fichier JSON
     */
    public static void sauvegarderBillets(ArrayList<Billet> billets) throws IOException {
        createDataDir();
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (int i = 0; i < billets.size(); i++) {
            Billet b = billets.get(i);
            json.append("  {\n");
            json.append("    \"codeBillet\": ").append(b.getCodeBillet()).append(",\n");
            json.append("    \"clientId\": ").append(b.getClient().getId()).append(",\n");
            json.append("    \"codeMatch\": \"").append(escapeJson(b.getMatch().getCodeMatch())).append("\",\n");
            json.append("    \"nomZone\": \"").append(escapeJson(b.getZone().getNomZone())).append("\",\n");
            json.append("    \"statut\": \"").append(escapeJson(b.getStatut())).append("\",\n");
            json.append("    \"montant\": ").append(b.getMontant()).append(",\n");
            json.append("    \"dateCreation\": \"").append(b.getDateCreation().format(formatter)).append("\"\n");
            json.append("  }");
            if (i < billets.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        writeFile(DATA_DIR + "/" + BILLETS_FILE, json.toString());
    }
    
    /**
     * Sauvegarde toutes les données
     */
    public static void sauvegarderTout(ArrayList<Match> matchs, 
                                        ArrayList<Client> clients,
                                        ArrayList<Billet> billets,
                                        HashMap<String, ArrayList<ZonePlace>> zoneParMatch,
                                        HashMap<String, Integer> billetsVendusParZone) throws IOException {
        sauvegarderMatchs(matchs);
        sauvegarderClients(clients);
        sauvegarderZones(zoneParMatch, billetsVendusParZone);
        sauvegarderBillets(billets);
    }
    
    // ==================== CHARGEMENT ====================
    
    /**
     * Charge les matchs depuis le fichier JSON
     */
    public static ArrayList<Match> chargerMatchs() throws IOException {
        ArrayList<Match> matchs = new ArrayList<>();
        String content = readFile(DATA_DIR + "/" + MATCHS_FILE);
        
        if (content == null || content.trim().isEmpty()) {
            return matchs;
        }
        
        // Parser le JSON manuellement
        List<Map<String, String>> items = parseJsonArray(content);
        
        for (Map<String, String> item : items) {
            String codeMatch = item.get("codeMatch");
            String equipeA = item.get("equipeA");
            String equipeB = item.get("equipeB");
            String stade = item.get("stade");
            String date = item.get("date");
            String heure = item.get("heure");
            int importance = Integer.parseInt(item.get("importance"));
            
            matchs.add(new Match(codeMatch, equipeA, equipeB, stade, date, heure, importance));
        }
        
        return matchs;
    }
    
    /**
     * Charge les clients depuis le fichier JSON
     */
    public static ArrayList<Client> chargerClients() throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        String content = readFile(DATA_DIR + "/" + CLIENTS_FILE);
        
        if (content == null || content.trim().isEmpty()) {
            return clients;
        }
        
        List<Map<String, String>> items = parseJsonArray(content);
        
        // Trouver l'ID max pour réinitialiser le compteur
        int maxId = 0;
        
        for (Map<String, String> item : items) {
            int id = Integer.parseInt(item.get("id"));
            String nom = item.get("nom");
            String email = item.get("email");
            String type = item.get("type");
            
            if (id > maxId) maxId = id;
            
            Client client;
            if ("media".equals(type)) {
                Media media = new Media(nom, email);
                if ("true".equals(item.get("accredite"))) {
                    media.setAccredite(true);
                }
                client = media;
            } else {
                client = new Spectateur(nom, email);
            }
            
            clients.add(client);
        }
        
        return clients;
    }
    
    /**
     * Charge les zones depuis le fichier JSON
     */
    public static Map<String, Object> chargerZones() throws IOException {
        HashMap<String, ArrayList<ZonePlace>> zoneParMatch = new HashMap<>();
        HashMap<String, Integer> billetsVendusParZone = new HashMap<>();
        
        String content = readFile(DATA_DIR + "/" + ZONES_FILE);
        
        if (content == null || content.trim().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("zones", zoneParMatch);
            result.put("vendus", billetsVendusParZone);
            return result;
        }
        
        // Parser le JSON objet
        Map<String, List<Map<String, String>>> zonesData = parseJsonObject(content);
        
        for (Map.Entry<String, List<Map<String, String>>> entry : zonesData.entrySet()) {
            String codeMatch = entry.getKey();
            ArrayList<ZonePlace> zones = new ArrayList<>();
            
            for (Map<String, String> zoneData : entry.getValue()) {
                String nomZone = zoneData.get("nomZone");
                int capacite = Integer.parseInt(zoneData.get("capacite"));
                double coef = Double.parseDouble(zoneData.get("coefficientPrix"));
                int vendus = Integer.parseInt(zoneData.getOrDefault("billetsVendus", "0"));
                
                zones.add(new ZonePlace(nomZone, capacite, coef));
                
                String key = codeMatch + "_" + nomZone;
                billetsVendusParZone.put(key, vendus);
            }
            
            zoneParMatch.put(codeMatch, zones);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("zones", zoneParMatch);
        result.put("vendus", billetsVendusParZone);
        return result;
    }
    
    /**
     * Charge les billets depuis le fichier JSON (nécessite les matchs, clients et zones déjà chargés)
     */
    public static ArrayList<Billet> chargerBillets(ArrayList<Match> matchs, 
                                                    ArrayList<Client> clients,
                                                    HashMap<String, ArrayList<ZonePlace>> zoneParMatch) throws IOException {
        ArrayList<Billet> billets = new ArrayList<>();
        String content = readFile(DATA_DIR + "/" + BILLETS_FILE);
        
        if (content == null || content.trim().isEmpty()) {
            return billets;
        }
        
        List<Map<String, String>> items = parseJsonArray(content);
        
        // Créer des maps pour recherche rapide
        Map<String, Match> matchMap = new HashMap<>();
        for (Match m : matchs) {
            matchMap.put(m.getCodeMatch(), m);
        }
        
        Map<Integer, Client> clientMap = new HashMap<>();
        for (Client c : clients) {
            clientMap.put(c.getId(), c);
        }
        
        int maxCode = 99;
        
        for (Map<String, String> item : items) {
            int codeBillet = Integer.parseInt(item.get("codeBillet"));
            int clientId = Integer.parseInt(item.get("clientId"));
            String codeMatch = item.get("codeMatch");
            String nomZone = item.get("nomZone");
            String statut = item.get("statut");
            double montant = Double.parseDouble(item.get("montant"));
            
            if (codeBillet > maxCode) maxCode = codeBillet;
            
            Client client = clientMap.get(clientId);
            Match match = matchMap.get(codeMatch);
            
            if (client != null && match != null) {
                ZonePlace zone = null;
                ArrayList<ZonePlace> zones = zoneParMatch.get(codeMatch);
                if (zones != null) {
                    for (ZonePlace z : zones) {
                        if (z.getNomZone().equals(nomZone)) {
                            zone = z;
                            break;
                        }
                    }
                }
                
                if (zone != null) {
                    Billet billet = new Billet(client, match, zone, statut, montant);
                    billets.add(billet);
                }
            }
        }
        
        return billets;
    }
    
    /**
     * Vérifie si des données sauvegardées existent
     */
    public static boolean donneesExistent() {
        File matchsFile = new File(DATA_DIR + "/" + MATCHS_FILE);
        return matchsFile.exists() && matchsFile.length() > 0;
    }
    
    // ==================== UTILITAIRES ====================
    
    private static void createDataDir() throws IOException {
        Path path = Paths.get(DATA_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
    
    private static void writeFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            writer.write(content);
        }
    }
    
    private static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Parse un tableau JSON simple
     */
    private static List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        
        // Supprimer les crochets externes et espaces
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        // Diviser par objets {...}
        int depth = 0;
        StringBuilder currentObject = new StringBuilder();
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '{') {
                depth++;
                if (depth == 1) {
                    currentObject = new StringBuilder();
                }
                currentObject.append(c);
            } else if (c == '}') {
                currentObject.append(c);
                depth--;
                if (depth == 0) {
                    result.add(parseJsonObjectSimple(currentObject.toString()));
                }
            } else if (depth > 0) {
                currentObject.append(c);
            }
        }
        
        return result;
    }
    
    /**
     * Parse un objet JSON simple (clé -> valeur string)
     */
    private static Map<String, String> parseJsonObjectSimple(String json) {
        Map<String, String> result = new HashMap<>();
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        // Regex-free parsing
        boolean inQuote = false;
        boolean inKey = false;
        boolean inValue = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            char prev = i > 0 ? json.charAt(i - 1) : ' ';
            
            if (c == '"' && prev != '\\') {
                inQuote = !inQuote;
                if (!inKey && !inValue) {
                    inKey = true;
                    currentKey = new StringBuilder();
                } else if (inKey && !inQuote) {
                    inKey = false;
                } else if (!inKey && inValue && !inQuote) {
                    inValue = false;
                    result.put(currentKey.toString().trim(), currentValue.toString().trim());
                }
            } else if (c == ':' && !inQuote && !inValue) {
                inValue = true;
                currentValue = new StringBuilder();
            } else if (c == ',' && !inQuote) {
                if (inValue && currentValue.length() > 0) {
                    result.put(currentKey.toString().trim(), currentValue.toString().trim());
                }
                inValue = false;
                inKey = false;
            } else if (inKey && inQuote) {
                currentKey.append(c);
            } else if (inValue) {
                if (inQuote || (!Character.isWhitespace(c) && c != '"')) {
                    currentValue.append(c);
                }
            }
        }
        
        // Dernière valeur
        if (inValue && currentValue.length() > 0) {
            result.put(currentKey.toString().trim(), currentValue.toString().trim());
        }
        
        return result;
    }
    
    /**
     * Parse un objet JSON avec des tableaux comme valeurs
     */
    private static Map<String, List<Map<String, String>>> parseJsonObject(String json) {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        int depth = 0;
        int arrayDepth = 0;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentArray = new StringBuilder();
        boolean inKey = false;
        boolean inArray = false;
        boolean inQuote = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            char prev = i > 0 ? json.charAt(i - 1) : ' ';
            
            if (c == '"' && prev != '\\') {
                inQuote = !inQuote;
                if (!inArray) {
                    if (!inKey) {
                        inKey = true;
                        currentKey = new StringBuilder();
                    } else {
                        inKey = false;
                    }
                } else {
                    currentArray.append(c);
                }
            } else if (c == '[' && !inQuote) {
                if (!inArray) {
                    inArray = true;
                    currentArray = new StringBuilder();
                    currentArray.append(c);
                } else {
                    currentArray.append(c);
                }
                arrayDepth++;
            } else if (c == ']' && !inQuote) {
                currentArray.append(c);
                arrayDepth--;
                if (arrayDepth == 0) {
                    inArray = false;
                    result.put(currentKey.toString().trim(), parseJsonArray(currentArray.toString()));
                }
            } else if (inKey && inQuote) {
                currentKey.append(c);
            } else if (inArray) {
                currentArray.append(c);
            }
        }
        
        return result;
    }
}
