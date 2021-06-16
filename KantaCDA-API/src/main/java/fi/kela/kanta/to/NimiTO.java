package fi.kela.kanta.to;

import java.io.Serializable;

public class NimiTO implements Serializable {

    private static final long serialVersionUID = -2345211L;

    final private String tyyppi;
    final private String maare;
    final private String nimi;

    NimiTO(String tyyppi, String maare, String nimi) {
        this.tyyppi = tyyppi;
        this.maare = maare;
        this.nimi = nimi;
    }

    public String getTyyppi() {
        return tyyppi;
    }

    public String getMaare() {
        return maare;
    }

    public String getNimi() {
        return nimi;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NimiTO [tyyppi=");
        builder.append(tyyppi);
        builder.append(", maare=");
        builder.append(maare);
        builder.append(", nimi=");
        builder.append(nimi);
        builder.append("]");
        return builder.toString();
    }

}
