package fi.kela.kanta.interfaces;

import java.util.List;

public interface Kayttaja extends Henkilotiedot {

    /**
     * @return Sisäänkirjautuneen käyttäjän rekisteröitymistunnus (terhikki tunnus).
     */
    public String getRekisterinumero();

    /**
     * @return Henkilön 'katso id'.
     */
    public String getKatsoId();

    // /**
    // * Näistä tiedosta asetetaan shiro roolit, käyttäen <b>tunnisteina</b> roolien id:tä. Muuta tietoa voidaan käyttää
    // * mm. logituksen tarpeissa.
    // *
    // * @return Roolit jotka käyttäjälle on määritelty organisaatiokäytön oikeudeksi.
    // */
    // public List<Rooli> getOrganisaatioroolit();

    /**
     * Tästä tiedosta asetetaan shiro rooli, käyttäen <b>tunnisteena</b> roolin id:tä. Muuta tietoa voidaan käyttää mm.
     * logituksen tarpeissa.
     * 
     * @return Rooli joka käyttäjälle on määritelty organisaatiokäytön oikeudeksi.
     */
    public List<Rooli> getOrganisaatioroolit();
}
