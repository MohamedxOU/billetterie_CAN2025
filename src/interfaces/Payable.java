package interfaces;

import models.Billet;

public interface Payable {
    
    //payer
    void payer(Billet billet, String moyenPaiement)
        throws exceptions.PaiementInvalideException;

    //verifierPaiement
    boolean verifierPaiement(Billet billet);
}
