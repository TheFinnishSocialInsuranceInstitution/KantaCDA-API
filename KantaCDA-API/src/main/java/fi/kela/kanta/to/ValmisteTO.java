package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ValmisteTO implements Serializable {

    private static final long serialVersionUID = 23523452111L;
    // valmisteen yksilointitiedot:VamlisteenYksilointitiedot
    private ValmisteenYksilointitiedotTO yksilointitiedot;
    // Käyttötapa:ValmisteenKäyttötapa
    final private ArrayList<ValmisteenKayttotapaTO> kayttotavat;
    // Vaikuttava aine: VaikuttavaAine
    final private ArrayList<VaikuttavaAineTO> vaikuttavatAineet;
    // Muut tiedot:ValmisteenMuuttiedot
    private ValmisteenMuutTiedotTO muutTiedot;

    public ValmisteTO() {
        vaikuttavatAineet = new ArrayList<VaikuttavaAineTO>();
        kayttotavat = new ArrayList<ValmisteenKayttotapaTO>();
    }

    public ValmisteenYksilointitiedotTO getYksilointitiedot() {
        return yksilointitiedot;
    }

    public void setYksilointitiedot(ValmisteenYksilointitiedotTO yksilointitiedot) {
        this.yksilointitiedot = yksilointitiedot;
    }

    public Collection<ValmisteenKayttotapaTO> getKayttotavat() {
        return kayttotavat;
    }

    public Collection<VaikuttavaAineTO> getVaikuttavatAineet() {
        return vaikuttavatAineet;
    }

    public ValmisteenMuutTiedotTO getMuutTiedot() {
        return muutTiedot;
    }

    public void setMuutTiedot(ValmisteenMuutTiedotTO muutTiedot) {
        this.muutTiedot = muutTiedot;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Valmiste [yksilointitunnus: ").append(getYksilointitiedot().getYksilointitunnus());
        sb.append(", atcKoodi: ").append(getYksilointitiedot().getATCkoodi());
        sb.append(", atcNimi: ").append(getYksilointitiedot().getATCnimi());
        sb.append(", valmisteenlaji: ").append(getYksilointitiedot().getValmisteenLaji());
        sb.append(", valmisteenlajinimi: ").append(getYksilointitiedot().getValmisteenLajiNimi());
        sb.append(", kauppanimi: ").append(getYksilointitiedot().getKauppanimi());
        sb.append(", valmisteen lisätieto: ").append(getYksilointitiedot().getValmisteenLisatieto());
        sb.append(", pakkauskoko: ").append(getYksilointitiedot().getPakkauskoko());
        sb.append(", pakkauskokoteksti: ").append(getYksilointitiedot().getPakkauskokoteksti());
        sb.append("]");
        return sb.toString();
    }
}
