package fi.kela.kanta.to;

import java.io.Serializable;

public class ValmisteenMuutTiedotTO implements Serializable {

    private static final long serialVersionUID = 1645547777745L;

    private boolean huume;
    private String PKVlaake;

    public boolean isHuume() {
        return huume;
    }

    public void setHuume(boolean huume) {
        this.huume = huume;
    }

    public String getPKVlaake() {
        return PKVlaake;
    }

    public void setPKVlaake(String pKVlaake) {
        PKVlaake = pKVlaake;
    }

}
