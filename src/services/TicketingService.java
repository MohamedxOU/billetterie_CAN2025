package services;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import exceptions.BilletIndisponibleException;
import interfaces.Payable;
import interfaces.Reservable;
import models.Billet;
import models.Media;
import models.ZonePlace;
import models.Client;
import models.Match;
import models.Spectateur;

import exceptions.*;

public class TicketingService implements Reservable, Payable {

    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Match> matches = new ArrayList<>();
    private HashMap<String, ArrayList<ZonePlace>> zoneParMatch = new HashMap<>(); 


    public ArrayList<Client> getClients() {
        return clients;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }


    public void ajouterMatch(String codeMatch, String equipeA, String equipeB, String stade, String date, String heure, int importance) {
        this.matches.add(new Match(codeMatch, equipeA, equipeB, stade, date, heure, importance));
    }

    public void ajouterZonePlace(String codeMatch, ZonePlace zonePlace) {
        this.zoneParMatch.putIfAbsent(codeMatch, new ArrayList<>());
        this.zoneParMatch.get(codeMatch).add(zonePlace);
    }

   

    public void ajouterClient(String nom, String email, boolean isMedia) {
        this.clients.add( isMedia ? new Media(nom, email) : new Spectateur(nom, email));
    }

    

    //accredite media
    public void toggleAccreditation(Media media) {
        media.setAccredite(!media.isAccredite());
    }




    

    
    @Override
    public boolean estDisponible(Match match, ZonePlace zonePlace) throws BilletIndisponibleException {
        // Implémentation de la vérification de disponibilité
        //if match exists, check date and time with current, if zonePlace exists for this match, check capacity

        Match foundMatch = null;
        for (Match m : matches) {
            if (m.getCodeMatch().equals(match.getCodeMatch())) {
                foundMatch = m;
                break;
            }
        }

        if (foundMatch != null) {
            ArrayList<ZonePlace> zones = zoneParMatch.get(foundMatch.getCodeMatch());
            if (zones != null) {
                for (ZonePlace zp : zones) {
                    if (zp.getNomZone().equals(zonePlace.getNomZone())) {
                        if (zp.getCapacite() > 0) {
                            throw new BilletIndisponibleException("Pas de billet disponible pour ce match");
                        }
                        return true; // Placeholder
                    }
                }
            }
        }

        


        

        return false;
    }

    @Override
    public Billet reserverBillet(models.Client client, models.Match match, models.ZonePlace zonePlace) {
        // Implémentation de la réservation de billet
        return null;
    }

    @Override
    public void annulerBillet(int codeBillet) {
        // Implémentation de l'annulation de billet
    }

    @Override
    public void payer(models.Billet billet, String moyenPaiement) {
        // Implémentation du paiement
    }

    @Override
    public boolean verifierPaiement(models.Billet billet) {
        // Implémentation de la vérification du paiement
        return false;
    }

    

    
    
}
