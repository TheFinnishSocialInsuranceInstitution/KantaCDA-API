package fi.kela.kanta.to;

public class LaakemaarayksenLukituksenPurkuTO extends LeimakentatTO<LaakemaarayksenLukituksenPurkuTO> {

    private static final long serialVersionUID = 82469109222248362L;

    private String lukitussanomanOid;
    private String lukitussanomanSetId;
    private int lukitussanomanVersio;
    private AmmattihenkiloTO purkaja;
    private String selitys;

    public String getLukitussanomanOid() {
        return lukitussanomanOid;
    }

    public void setLukitussanomanOid(String lukitussanomanOid) {
        this.lukitussanomanOid = lukitussanomanOid;
    }

    public String getLukitussanomanSetId() {
        return lukitussanomanSetId;
    }

    public void setLukitussanomanSetId(String lukitussanomanSetId) {
        this.lukitussanomanSetId = lukitussanomanSetId;
    }

    public int getLukitussanomanVersio() {
        return lukitussanomanVersio;
    }

    public void setLukitussanomanVersio(int lukitussanomanVersio) {
        this.lukitussanomanVersio = lukitussanomanVersio;
    }

    public AmmattihenkiloTO getPurkaja() {
        return purkaja;
    }

    public void setPurkaja(AmmattihenkiloTO purkaja) {
        this.purkaja = purkaja;
    }

    public String getSelitys() {
        return selitys;
    }

    public void setSelitys(String selitys) {
        this.selitys = selitys;
    }

}
