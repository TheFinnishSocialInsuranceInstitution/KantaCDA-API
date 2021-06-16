package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.Date;

public class AmmattihenkiloTO implements Serializable {

    private static final long serialVersionUID = -7765088847059931248L;

    // Sukunimi:string
    // Etunimi:String
    private KokoNimiTO kokonimi;
    // Rekisteröintinumero:II
    private String rekisterointinumero;
    // SV-numero:II
    private String svNumero;
    // Ammattioikeus:CV
    private String ammattioikeus;
    private String ammattioikeusName;
    // Virkanimike:String
    private String virkanimike;
    // Oppiarvo:CV
    private String oppiarvo;
    // Oppiarvo teksinä:String
    private String oppiarvoTekstina;
    // Erikoisala:CV
    private String erikoisala;
    private String erikoisalaName;
    // Rooli:CV
    private String rooli;

    private Date kirjautumisaika;

    private OrganisaatioTO organisaatio;

    public KokoNimiTO getKokonimi() {
        return kokonimi;
    }

    public void setKokonimi(KokoNimiTO kokonimi) {
        this.kokonimi = kokonimi;
    }

    public String getRekisterointinumero() {
        return rekisterointinumero;
    }

    public void setRekisterointinumero(String rekisterointinumero) {
        this.rekisterointinumero = rekisterointinumero;
    }

    public String getSvNumero() {
        return svNumero;
    }

    public void setSvNumero(String svNumero) {
        this.svNumero = svNumero;
    }

    public String getAmmattioikeus() {
        return ammattioikeus;
    }

    public void setAmmattioikeus(String ammattioikeus) {
        this.ammattioikeus = ammattioikeus;
    }

    public String getVirkanimike() {
        return virkanimike;
    }

    public void setVirkanimike(String virkanimike) {
        this.virkanimike = virkanimike;
    }

    public String getOppiarvo() {
        return oppiarvo;
    }

    public void setOppiarvo(String oppiarvo) {
        this.oppiarvo = oppiarvo;
    }

    public String getOppiarvoTekstina() {
        return oppiarvoTekstina;
    }

    public void setOppiarvoTekstina(String oppiarvoTekstina) {
        this.oppiarvoTekstina = oppiarvoTekstina;
    }

    public String getErikoisala() {
        return erikoisala;
    }

    public void setErikoisala(String erikoisala) {
        this.erikoisala = erikoisala;
    }

    public String getRooli() {
        return rooli;
    }

    public void setRooli(String rooli) {
        this.rooli = rooli;
    }

    public String getErikoisalaName() {
        return erikoisalaName;
    }

    public void setErikoisalaName(String erikoisalaName) {
        this.erikoisalaName = erikoisalaName;
    }

    public String getAmmattioikeusName() {
        return ammattioikeusName;
    }

    public void setAmmattioikeusName(String ammattioikeusName) {
        this.ammattioikeusName = ammattioikeusName;
    }

    /**
     * @return the kirjautumisaika
     */
    public Date getKirjautumisaika() {
        return kirjautumisaika;
    }

    /**
     * @param kirjautumisaika
     *            the kirjautumisaika to set
     */
    public void setKirjautumisaika(Date kirjautumisaika) {
        this.kirjautumisaika = kirjautumisaika;
    }

    public OrganisaatioTO getOrganisaatio() {
        return organisaatio;
    }

    public void setOrganisaatio(OrganisaatioTO organisaatio) {
        this.organisaatio = organisaatio;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Ammattihenkilö [reknro: ").append(getRekisterointinumero());
        sb.append(", svnnro: ").append(getSvNumero());
        sb.append(", nimi: ").append(getKokonimi());
        sb.append(", rooli: ").append(getRooli());
        sb.append(", reknro: ").append(getRekisterointinumero());
        sb.append(", virkanimike: ").append(getVirkanimike());
        sb.append(", erikoisala: ").append(getErikoisala());
        sb.append(", erikoisalan nimi: ").append(getErikoisalaName());
        sb.append(", oppiarvo: ").append(getOppiarvo());
        sb.append(", oppiarvo teksti: ").append(getOppiarvoTekstina());
        if ( getOrganisaatio() != null ) {
            sb.append(", ").append(getOrganisaatio().toString());
        }
        else {
            sb.append(", henkilöllä ei ole organisaatiotietoja!");
        }
        sb.append("]");
        return sb.toString();
    }
}
