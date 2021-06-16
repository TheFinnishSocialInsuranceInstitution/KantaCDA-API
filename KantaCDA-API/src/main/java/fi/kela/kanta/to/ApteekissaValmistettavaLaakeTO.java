package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ApteekissaValmistettavaLaakeTO implements Serializable {

    private static final long serialVersionUID = 15222225252L;
    // Vaikuttava ainesosa:Vaikuttava ainesosa
    final private ArrayList<VaikuttavaAinesosaTO> vaikuttavatAinesosat;
    // Muu ainesosa:Muu ainesosa
    final private ArrayList<MuuAinesosaTO> muutAinesosat;
    // Valmistusohje:String
    private String valmistusohje;

    public ApteekissaValmistettavaLaakeTO() {
        vaikuttavatAinesosat = new ArrayList<VaikuttavaAinesosaTO>();
        muutAinesosat = new ArrayList<MuuAinesosaTO>();
    }

    public Collection<VaikuttavaAinesosaTO> getVaikuttavatAinesosat() {
        return vaikuttavatAinesosat;
    }

    public Collection<MuuAinesosaTO> getMuutAinesosat() {
        return muutAinesosat;
    }

    public String getValmistusohje() {
        return valmistusohje;
    }

    public void setValmistusohje(String valmistusohje) {
        this.valmistusohje = valmistusohje;
    }
}
