package fi.kela.kanta.to;

import java.io.Serializable;

public class VaikuttavaAineTO implements Serializable {

    private static final long serialVersionUID = 16785685655533L;

    private String laakeaine;
    private String laakeaineenTarkenne;
    private double vahvuus;
    private String vahvuusYksikko;

    public String getLaakeaine() {
        return laakeaine;
    }

    public void setLaakeaine(String laakeaine) {
        this.laakeaine = laakeaine;
    }

    public String getLaakeaineenTarkenne() {
        return laakeaineenTarkenne;
    }

    public void setLaakeaineenTarkenne(String laakeaineenTarkenne) {
        this.laakeaineenTarkenne = laakeaineenTarkenne;
    }

    public double getVahvuus() {
        return vahvuus;
    }

    public void setVahvuus(double vahvuus) {
        this.vahvuus = vahvuus;
    }

    public String getVahvuusYksikko() {
        return vahvuusYksikko;
    }

    public void setVahvuusYksikko(String vahvuusYksikko) {
        this.vahvuusYksikko = vahvuusYksikko;
    }
}
