package fi.kela.kanta.interfaces;

import java.util.List;

/**
 * Laajentaa rekisteröitymistiedot käyttäjälle.
 * 
 */
public interface TunnistettuKayttaja extends Kayttaja {

    /**
     * @return Kyseiseen käyttäjään liittyvät Organisaatiotiedot.
     */
    public Organisaatio getOrganisaatio();

    /**
     * @return Listaus rajoitustiedoista jotka käyttäjälle on asetettu.
     */
    public List<String> getRajoitustekstitLista();

    /**
     * @return Tunnistautumistapakoodi
     */
    public String getTunnistautumistapakoodi();
}
