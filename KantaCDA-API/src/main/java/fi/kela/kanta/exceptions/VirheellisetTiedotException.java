package fi.kela.kanta.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Tähän luokkaan voidan kerätä tiedot kaikista entiteteeistä (tai miksei jostain muustakin), joita ei kyetty
 * käsittelemään bean validation virheiden vuoksi. Voidaan käyttää siis mm. BeanValidation:in yhteydessä JPA
 * entiteettien virheiden kapselointiin. <br/>
 * Tähän virheeseen voidaan siis kapseloida joukko validointivirheitä, jotta ensimmäinen kohdattu virhe ei katkaise
 * esimerkiksi kaikkien muiden entiteettien käsittelyä.
 *
 */
public class VirheellisetTiedotException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String list_start = "Objektissa seuraavia virheitä [";
    private static final String list_start_with_name = "Objektissa '%s' seuraavia virheitä [";
    private static final String list_end = "]";
    private static final String sepr = ": ";
    private final LinkedHashMap<String, String> virheet;
    private final String objektinNimi;

    /**
     * Oletuskonstruktori.
     */
    public VirheellisetTiedotException() {

        this(null);
    }

    /**
     * Oletuskonstruktori.
     */
    public VirheellisetTiedotException(String objektinNimi) {

        virheet = new LinkedHashMap<String, String>();
        this.objektinNimi = objektinNimi;
    }

    /**
     * Lisää uuden virhemerkinnän.
     *
     * @param avain
     *            Tunniste, mihin virhe liittyy.
     * @param arvo
     *            Selite virheestä.
     */
    public void addVirhe(String avain, String arvo) {
        virheet.put(avain, arvo);
    }

    /**
     * @return Listaus virheistä, jota tähän poikkeukseen on lueteltu.
     */
    public Map<String, String> getVirheet() {
        return virheet;
    }

    /**
     * @return <code>True</code> jos tässä poikkeuksessa on listattu virheitä.
     */
    public boolean isVirheita() {
        return !virheet.isEmpty();
    }

    @Override
    public String getMessage() {

        StringBuilder sb = new StringBuilder();
        if ( !StringUtils.isEmpty(getObjektinNimi()) ) {
            Object[] args = { this.getObjektinNimi() };
            sb.append(String.format(VirheellisetTiedotException.list_start_with_name, args));
        }
        else {
            sb.append(VirheellisetTiedotException.list_start);
        }
        for (Map.Entry<String, String> entry : virheet.entrySet()) {
            sb.append(entry.getKey()).append(VirheellisetTiedotException.sepr).append(entry.getValue());
            sb.append(", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append(VirheellisetTiedotException.list_end);
        return sb.toString();
    }

    public String getObjektinNimi() {
        return objektinNimi;
    }
}
