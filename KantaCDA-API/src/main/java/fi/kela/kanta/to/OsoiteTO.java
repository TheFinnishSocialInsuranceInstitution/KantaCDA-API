package fi.kela.kanta.to;

import fi.kela.kanta.interfaces.Osoite;

public class OsoiteTO implements Osoite {

    private static final long serialVersionUID = -4222164103902178L;
    // Katuosoite:String
    private String katuosoite;
    // Maa:String
    private String maa;
    // Postinumero:String
    private String postinumero;
    // Postitoimipaikka:String
    private String postitoimipaikka;

    @Override
    public String getKatuosoite() {
        return katuosoite;
    }

    @Override
    public void setKatuosoite(String katuosoite) {
        this.katuosoite = katuosoite;
    }

    @Override
    public String getMaa() {
        return maa;
    }

    @Override
    public void setMaa(String maa) {
        this.maa = maa;
    }

    @Override
    public String getPostinumero() {
        return postinumero;
    }

    @Override
    public void setPostinumero(String postinumero) {
        this.postinumero = postinumero;
    }

    @Override
    public String getPostitoimipaikka() {
        return postitoimipaikka;
    }

    @Override
    public void setPostitoimipaikka(String postitoimipaikka) {
        this.postitoimipaikka = postitoimipaikka;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Osoite [katuosoite: ").append(getKatuosoite());
        sb.append(", postinumero: ").append(getPostinumero());
        sb.append(", postitoimipaikka: ").append(getPostitoimipaikka());
        sb.append(", maa: ").append(getMaa());
        sb.append("]");
        return sb.toString();
    }
}
