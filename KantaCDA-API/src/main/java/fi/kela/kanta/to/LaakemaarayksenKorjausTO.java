package fi.kela.kanta.to;

public class LaakemaarayksenKorjausTO extends LaakemaaraysTO {

    private static final long serialVersionUID = -211777865037738094L;

    private AmmattihenkiloTO korjaaja;
    private String korjauksenPerustelu;
    private String korjauksenSyyKoodi;
    private String alkuperainenOid;
    private int alkuperainenCdaTyyppi;

    public LaakemaarayksenKorjausTO() {
        super(false);
    }

    public AmmattihenkiloTO getKorjaaja() {
        return korjaaja;
    }

    public void setKorjaaja(AmmattihenkiloTO korjaaja) {
        this.korjaaja = korjaaja;
    }

    public String getKorjauksenPerustelu() {
        return korjauksenPerustelu;
    }

    public void setKorjauksenPerustelu(String korjauksenPerustelu) {
        this.korjauksenPerustelu = korjauksenPerustelu;
    }

    public String getKorjauksenSyyKoodi() {
        return korjauksenSyyKoodi;
    }

    public void setKorjauksenSyyKoodi(String korjauksenSyyKoodi) {
        this.korjauksenSyyKoodi = korjauksenSyyKoodi;
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

}
