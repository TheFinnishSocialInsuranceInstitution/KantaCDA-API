package fi.kela.kanta.interfaces;

import java.io.Serializable;

public interface Osoite extends Serializable {

    public String getKatuosoite();

    public void setKatuosoite(String katuosoite);

    public String getMaa();

    public void setMaa(String maa);

    public String getPostinumero();

    public void setPostinumero(String postinumero);

    public String getPostitoimipaikka();

    public void setPostitoimipaikka(String postitoimipaikka);
}
