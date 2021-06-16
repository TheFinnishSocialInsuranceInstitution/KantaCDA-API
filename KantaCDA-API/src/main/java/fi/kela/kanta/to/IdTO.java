package fi.kela.kanta.to;

import java.io.Serializable;

public class IdTO implements Serializable {

    private static final long serialVersionUID = -42255555108L;

    private String root;
    private String extension;
    private String nullFlavor;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getNullFlavor() {
        return nullFlavor;
    }

    public void setNullFlavor(String nullFlavor) {
        this.nullFlavor = nullFlavor;
    }

}
