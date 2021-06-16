package fi.kela.kanta.to;

import java.io.Serializable;

public class MuuAinesosaTO implements Serializable {

    private static final long serialVersionUID = 64754788881181L;
    // Nimi:String
    private String nimi;
    // Ainesosan määrä:PQ
    private double ainesosanMaaraValue;
    private String ainesosanMaaraUnit;
    // Ainesosan määrä tekstinä:String
    private String ainesosanMaaraTekstina;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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

}
