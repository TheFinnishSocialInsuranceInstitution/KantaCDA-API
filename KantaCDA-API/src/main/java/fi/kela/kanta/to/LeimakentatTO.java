package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.kela.kanta.cda.MaarittelyLuokka;

public abstract class LeimakentatTO<TO extends LeimakentatTO<?>> implements Serializable {

    private static final long serialVersionUID = 5754888865781L;

    private String setId;
    private String oid;
    private int versio = 0;
    private int cdaTyyppi;
    private Date aikaleima;
    private String CDAOidBase = null;
    // Tuottanut sovellus ja sen versio
    private String product;
    private String productVersion;

    final private List<String> templateIds = new ArrayList<String>();
    final private List<String> bodyTemplateIds = new ArrayList<String>();

    private MaarittelyLuokka maarittelyLuokka;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getVersio() {
        return versio;
    }

    public void setVersio(int versio) {
        this.versio = versio;
    }

    public int getCdaTyyppi() {
        return cdaTyyppi;
    }

    public void setCdaTyyppi(int cdaTyyppi) {
        this.cdaTyyppi = cdaTyyppi;
    }

    public Date getAikaleima() {
        return aikaleima;
    }

    public void setAikaleima(Date aikaleima) {
        this.aikaleima = aikaleima;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getCDAOidBase() {
        return CDAOidBase;
    }

    public void setCDAOidBase(String cDAOidBase) {
        CDAOidBase = cDAOidBase;
    }

    public List<String> getTemplateIds() {
        return templateIds;
    }

    public MaarittelyLuokka getMaarittelyLuokka() {
        return maarittelyLuokka;
    }

    public void setMaarittelyLuokka(MaarittelyLuokka maarittelyLuokka) {
        this.maarittelyLuokka = maarittelyLuokka;
    }

    public List<String> getBodyTemplateIds() {
        return bodyTemplateIds;
    }

}
