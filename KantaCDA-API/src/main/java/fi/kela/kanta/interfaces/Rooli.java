package fi.kela.kanta.interfaces;

/**
 * Rajapinta käyttäjärekisterin, shiron ja mahdollisesti logituksen tarvitsemien roolitietojen käsittelyyn. <br/>
 * Pitäisi selvittää löytyvätkö arvot koodistosta. Esim: 1.2.246.537.6.246 (THL - Toimenpiteen tekijän rooli)
 */
public interface Rooli {

    /**
     * PK arvo joka pitäisi löytyä käyttäjärekisteristä, sekä mahdollisesti myös koodistosta.
     *
     * @return Id roolille.
     */
    public String getRooliId();

    /**
     * Tämä arvo antaa selkokielisen (lyhyen) kuvauksen roolista.
     *
     * @return Roolille annettu nimi.
     */
    public String getRooliNimi();

    /**
     * @return Vapaamuotoista lisätietoa roolista.
     */
    public String getLisatieto();
}
