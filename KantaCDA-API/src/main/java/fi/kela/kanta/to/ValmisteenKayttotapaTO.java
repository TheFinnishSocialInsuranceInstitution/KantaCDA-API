package fi.kela.kanta.to;

import java.io.Serializable;

public class ValmisteenKayttotapaTO implements Serializable {

    private static final long serialVersionUID = 135111113252356L;
    // Lääkemuoto:CV
    private String laakemuoto;
    // Lääkemuodon lyhenne:String
    private String laakemuodonLyhenne;
    // Lääkkeenantoreitti:CV
    private String laakeenantoreitti;

    public String getLaakemuoto() {
        return laakemuoto;
    }

    public void setLaakemuoto(String laakemuoto) {
        this.laakemuoto = laakemuoto;
    }

    public String getLaakemuodonLyhenne() {
        return laakemuodonLyhenne;
    }

    public void setLaakemuodonLyhenne(String laakemuodonLyhenne) {
        this.laakemuodonLyhenne = laakemuodonLyhenne;
    }

    public String getLaakeenantoreitti() {
        return laakeenantoreitti;
    }

    public void setLaakeenantoreitti(String laakeenantoreitti) {
        this.laakeenantoreitti = laakeenantoreitti;
    }
}
