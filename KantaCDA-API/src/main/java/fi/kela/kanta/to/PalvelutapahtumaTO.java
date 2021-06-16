package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.Date;

public class PalvelutapahtumaTO implements Serializable {

    private static final long serialVersionUID = -688883223902178L;

    private Date aikaleima;
    private IdTO oid;
    private OrganisaatioTO toimintayksikko;

    public Date getAikaleima() {
        return aikaleima;
    }

    public void setAikaleima(Date aikaleima) {
        this.aikaleima = aikaleima;
    }

    public IdTO getOid() {
        return oid;
    }

    public void setOid(IdTO oid) {
        this.oid = oid;
    }

    public OrganisaatioTO getToimintayksikko() {
        return toimintayksikko;
    }

    public void setToimintayksikko(OrganisaatioTO toimintayksikko) {
        this.toimintayksikko = toimintayksikko;
    }

}
