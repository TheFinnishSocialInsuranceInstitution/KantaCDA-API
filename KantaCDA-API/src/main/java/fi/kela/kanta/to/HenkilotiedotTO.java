package fi.kela.kanta.to;

import java.io.Serializable;

import fi.kela.kanta.util.KantaCDAUtil;

public class HenkilotiedotTO implements Serializable {

    private static final long serialVersionUID = -4362222346L;

    private KokoNimiTO nimi;
    private String hetu;
    private String syntymaaika;
    private Integer sukupuoli;

    /**
     * Kostruktori joka asettaa nimen, hetun, sukupuolen ja syntymäajan sukupuoli ja syntymäaika täydennetään hetun
     * perusteella.
     * 
     * @param nimi
     *            KokoNimiTO henkilön koko nimi
     * @param hetu
     *            String henkilön suomalainen henkiötunnus
     */
    public HenkilotiedotTO(KokoNimiTO nimi, String hetu) {
        this.nimi = nimi;
        this.hetu = hetu;
        this.sukupuoli = KantaCDAUtil.hetuToGender(hetu);
        this.syntymaaika = KantaCDAUtil.hetuToBirthTime(hetu);
    }

    /**
     * Konstruktori hetutomalle henkilölle.
     * 
     * @param nimi
     *            KokoNimiTO henkilön koko nimi
     * @param syntymaaika
     *            String henkilön syntymäaika (yyyyMMdd)
     * @param sukupuoli
     *            Integer henkilön sukupuoli (codeSystem = 1.2.246.537.5.1.1997)
     */
    public HenkilotiedotTO(KokoNimiTO nimi, String syntymaaika, Integer sukupuoli) {
        this.nimi = nimi;
        this.sukupuoli = sukupuoli;
        this.syntymaaika = syntymaaika;
    }

    public String getHetu() {
        return hetu;
    }

    public KokoNimiTO getNimi() {
        return nimi;
    }

    public String getSyntymaaika() {
        return syntymaaika;
    }

    public Integer getSukupuoli() {
        return sukupuoli;
    }

    public void setHetu(String hetu) {
        this.hetu = hetu;
    }

    public void setNimi(KokoNimiTO nimi) {
        this.nimi = nimi;
    }

    public void setSyntymaaika(String syntymaaika) {
        this.syntymaaika = syntymaaika;
    }

    public void setSukupuoli(Integer sukupuoli) {
        this.sukupuoli = sukupuoli;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HenkilotiedotTO [nimi=");
        builder.append(nimi);
        builder.append(", hetu=");
        builder.append(hetu);
        builder.append(", syntymaaika=");
        builder.append(syntymaaika);
        builder.append(", sukupuoli=");
        builder.append(String.valueOf(sukupuoli));
        builder.append("]");
        return builder.toString();
    }

}
