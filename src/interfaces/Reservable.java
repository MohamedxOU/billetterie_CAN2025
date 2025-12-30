package interfaces;

import models.ZonePlace;
import models.Match;
import models.Billet;
import models.Client;

public interface Reservable {
    
    //estDisponible
    boolean estDisponible(Match match, ZonePlace zonePlace)
        throws exceptions.BilletIndisponibleException;

    //reserverBillet
    Billet reserverBillet(Client client,Match match, ZonePlace zonePlace) 
        throws exceptions.BilletIndisponibleException;

    //annulerBillet
    void annulerBillet(int codeBillet);

    
}
