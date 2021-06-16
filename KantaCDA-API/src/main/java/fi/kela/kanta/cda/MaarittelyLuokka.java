package fi.kela.kanta.cda;

public enum MaarittelyLuokka {

    PUUTTUU("puuttuu"),
    EI_TUETTU("ei tuettu"),
    VANHA("vanha"),
    NYKYINEN("nykyinen"),
    TULEVA("tuleva");

    private String kuvaus;

    private MaarittelyLuokka(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public boolean isTuettu() {
        return (this == VANHA) || (this == NYKYINEN);
    }
}
