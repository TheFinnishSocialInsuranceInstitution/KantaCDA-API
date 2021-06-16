package fi.kela.kanta.to;

import java.io.Serializable;

public class VaikuttavaAinesosaTO implements Serializable {

    private static final long serialVersionUID = 14232211111L;
    // ATC-koodi:CV
    private String ATCkoodi;
    private String ATCnimi;
    // Koodaamaton nimi:String
    private String koodamatonNimi;
    // Kauppanimi, vahvuus ja lääkemuoto:String
    private String kauppanimiVahvuusJaLaakemuoto;
    // Suolamuoto:String
    private String suolamuoto;
    // Ainesosan määrä:PQ
    private double ainesosanMaaraValue;
    private String ainesosanMaaraUnit;
    // Ainesosan määrä tekstinä:String
    private String ainesosanMaaraTekstina;

    public String getATCkoodi() {
        return ATCkoodi;
    }

    public void setATCkoodi(String aTCkoodi) {
        ATCkoodi = aTCkoodi;
    }

    public String getKoodamatonNimi() {
        return koodamatonNimi;
    }

    public void setKoodamatonNimi(String koodamatonNimi) {
        this.koodamatonNimi = koodamatonNimi;
    }

    public String getKauppanimiVahvuusJaLaakemuoto() {
        return kauppanimiVahvuusJaLaakemuoto;
    }

    public void setKauppanimiVahvuusJaLaakemuoto(String kauppanimiVahvuusJaLaakemuoto) {
        this.kauppanimiVahvuusJaLaakemuoto = kauppanimiVahvuusJaLaakemuoto;
    }

    public String getSuolamuoto() {
        return suolamuoto;
    }

    public void setSuolamuoto(String suolamuoto) {
        this.suolamuoto = suolamuoto;
    }

    public double getAinesosanMaaraValue() {
        return ainesosanMaaraValue;
    }

    public void setAinesosanMaaraValue(double ainesosanMaaraValue) {
        this.ainesosanMaaraValue = ainesosanMaaraValue;
    }

    public String getAinesosanMaaraUnit() {
        return ainesosanMaaraUnit;
    }

    public void setAinesosanMaaraUnit(String ainesosanMaaraUnit) {
        this.ainesosanMaaraUnit = ainesosanMaaraUnit;
    }

    public String getAinesosanMaaraTekstina() {
        return ainesosanMaaraTekstina;
    }

    public void setAinesosanMaaraTekstina(String ainesosanMaaraTekstina) {
        this.ainesosanMaaraTekstina = ainesosanMaaraTekstina;
    }

    public String getATCNimi() {
        return ATCnimi;
    }

    public void setATCnimi(String ATCnimi) {
        this.ATCnimi = ATCnimi;
    }

}
