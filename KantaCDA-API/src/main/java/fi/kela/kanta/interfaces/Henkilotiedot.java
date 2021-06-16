package fi.kela.kanta.interfaces;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Määrittelee henkilön perustiedot.
 *
 */
public interface Henkilotiedot extends Serializable {

    /**
     * Nimilista henkilölle määrittellyistä nimistä. Ensimmäisenä palautettava nimi on oletuksena käytetty kutsumanimi.
     *
     * @return Lista (etu)nimistä.
     */
    public List<String> getEtunimet();

    /**
     * @return Henkilön sukunimi.
     */
    public String getSukunimi();

    /**
     * @return Henkilötunnus.
     */
    public String getHetu();

    /**
     * @return Syntymäaika.
     */
    public Date getSyntymapaiva();
}
