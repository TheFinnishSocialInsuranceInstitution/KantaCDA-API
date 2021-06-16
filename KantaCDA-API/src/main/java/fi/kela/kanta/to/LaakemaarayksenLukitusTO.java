package fi.kela.kanta.to;

public class LaakemaarayksenLukitusTO extends LaakemaaraysTO {

    private static final long serialVersionUID = 8097555458362L;

    private String laakemaarayksenYksilointitunnus;
    private String selitys;

    public String getLaakemaarayksenYksilointitunnus() {
        return laakemaarayksenYksilointitunnus;
    }

    public void setLaakemaarayksenYksilointitunnus(String laakemaarayksenYksilointitunnus) {
        this.laakemaarayksenYksilointitunnus = laakemaarayksenYksilointitunnus;
    }

    public String getSelitys() {
        return selitys;
    }

    public void setSelitys(String selitys) {
        this.selitys = selitys;
    }
}
