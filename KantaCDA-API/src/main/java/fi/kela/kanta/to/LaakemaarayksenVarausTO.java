package fi.kela.kanta.to;

public class LaakemaarayksenVarausTO extends LaakemaaraysTO {

    private static final long serialVersionUID = 857775888488467548L;

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
