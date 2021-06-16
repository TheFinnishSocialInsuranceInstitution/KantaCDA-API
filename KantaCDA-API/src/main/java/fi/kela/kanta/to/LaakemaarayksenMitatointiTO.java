package fi.kela.kanta.to;

public class LaakemaarayksenMitatointiTO extends LaakemaaraysTO {

    private static final long serialVersionUID = -211727037738094L;

    private AmmattihenkiloTO mitatoija;
    private String mitatoinninPerustelu;
    private String mitatoinninSyyKoodi;
    private String mitatoinninTyyppiKoodi;
    private String mitatoinninOsapuoli;

    /*
     * //koodisto 1.2.246.537.5.40119.2006 1 Suullinen suostumus Suullinen suostumus Suullinen suostumus 01.01.2006
     * 31.12.2030 2 Allekirjoitettu suostumus Allekirjoitettu suostumus Allekirjoitettu suostumus 01.01.2006 31.12.2030
     * 3 Suullinen pyyntö Suullinen pyyntö Suullinen pyyntö 01.01.2006 31.12.2030 4 Potilas ei anna suostumusta Potilas
     * ei anna suostumusta Potilas ei anna suostumusta 01.01.2006 31.12.2030 5 Suostumusta ei tarvita Suostumusta ei
     * tarvita Suostumusta ei tarvita 01.01.2006 31.12.2030
     */
    private String mitatoinninSuostumusKoodi;

    // Jos mitätöidään reseptin uudempia versioita (korjauksia), tarvitaan mitätöintiasiakirjalle
    // myös korjaajan tiedot mitätöitävältä asiakirjalta.
    // Toteutettu kopioimalla korjaajan tiedot tähän TO:hon, koska on haluttu välttää mahdollisia sivuvaikutuksia
    // reseptin korjauksen toiminnallisuuteen.
    private AmmattihenkiloTO alkuperainenKorjaaja;
    private String alkuperainenKorjauksenPerustelu;
    private String alkuperainenKorjauksenSyyKoodi;

    private String alkuperainenOid;
    private int alkuperainenCdaTyyppi;

    public LaakemaarayksenMitatointiTO() {
        super(false);
    }

    public AmmattihenkiloTO getMitatoija() {
        return mitatoija;
    }

    public void setMitatoija(AmmattihenkiloTO mitatoija) {
        this.mitatoija = mitatoija;
    }

    public String getMitatoinninPerustelu() {
        return mitatoinninPerustelu;
    }

    public void setMitatoinninPerustelu(String mitatoinninPerustelu) {
        this.mitatoinninPerustelu = mitatoinninPerustelu;
    }

    public String getMitatoinninSyyKoodi() {
        return mitatoinninSyyKoodi;
    }

    public void setMitatoinninSyyKoodi(String mitatoinninSyyKoodi) {
        this.mitatoinninSyyKoodi = mitatoinninSyyKoodi;
    }

    public String getMitatoinninTyyppiKoodi() {
        return mitatoinninTyyppiKoodi;
    }

    public void setMitatoinninTyyppiKoodi(String mitatoinninTyyppiKoodi) {
        this.mitatoinninTyyppiKoodi = mitatoinninTyyppiKoodi;
    }

    /**
     * @return the mitatoinninOsapuoli
     */
    public String getMitatoinninOsapuoli() {
        return mitatoinninOsapuoli;
    }

    /**
     * @param mitatoinninOsapuoli
     *            the mitatoinninOsapuoli to set
     */
    public void setMitatoinninOsapuoli(String mitatoinninOsapuoli) {
        this.mitatoinninOsapuoli = mitatoinninOsapuoli;
    }

    public String getAlkuperainenOid() {
        return alkuperainenOid;
    }

    public void setAlkuperainenOid(String alkuperainenOid) {
        this.alkuperainenOid = alkuperainenOid;
    }

    public int getAlkuperainenCdaTyyppi() {
        return alkuperainenCdaTyyppi;
    }

    public void setAlkuperainenCdaTyyppi(int cdaTyyppi) {
        this.alkuperainenCdaTyyppi = cdaTyyppi;
    }

    public String getMitatoinninSuostumusKoodi() {
        return mitatoinninSuostumusKoodi;
    }

    public void setMitatoinninSuostumusKoodi(String mitatoinninSuostumusKoodi) {
        this.mitatoinninSuostumusKoodi = mitatoinninSuostumusKoodi;
    }

    public String getAlkuperainenKorjauksenPerustelu() {
        return alkuperainenKorjauksenPerustelu;
    }

    public void setAlkuperainenKorjauksenPerustelu(String korjauksenPerustelu) {
        this.alkuperainenKorjauksenPerustelu = korjauksenPerustelu;
    }

    public String getAlkuperainenKorjauksenSyyKoodi() {
        return alkuperainenKorjauksenSyyKoodi;
    }

    public void setAlkuperainenKorjauksenSyyKoodi(String korjauksenSyyKoodi) {
        this.alkuperainenKorjauksenSyyKoodi = korjauksenSyyKoodi;
    }

    public AmmattihenkiloTO getAlkuperainenKorjaaja() {
        return alkuperainenKorjaaja;
    }

    public void setAlkuperainenKorjaaja(AmmattihenkiloTO alkuperainenKorjaaja) {
        this.alkuperainenKorjaaja = alkuperainenKorjaaja;
    }
}
