package fi.kela.kanta.util;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Luo oideja. Kts. Lääkemääräyksen_CDA_ R2_header_v.3.xx.doc : 13.1 id - asiakirjan tunniste.<br/>
 * Mikäli yksityisvastaanottotoiminnassa juoksevan numeron hallinta on mahdotonta (esimerkiksi useita
 * PDA/kännykkäsovelluksia jotka eivät keskustele keskenään), OID muodostetaan seuraavasti:
 * 1.2.246.537.25.1.123456.93.2007. 412.91203 Jossa 123456 yksilöintitunnus (sv-numero)/Fimean apteekkinumero
 * Apteekkinumero esitetään muodossa nnnnxx, missä nnnn on apteekkinumero ja xx on työasemanumero. 93 lääkemääräysten
 * solmuluokka (2 tai 5 numeroa) 2007 antovuoden sarja 0412 antopäivä (kuukaudesta poistetaan etunolla) 091203
 * kellonaika sekunnin tarkkuudella (kellonajasta poistetaan etunollat)
 * http://www.sfs.fi/julkaisut_ja_palvelut/palvelut/tunnukset</br>
 * https://www.julkari.fi/bitstream/handle/10024/79996/c5aa4d04-871f-42fe-843f-bb495996d77d.pdf?sequence=1</br>
 */
public class OidGenerator {

    /**
     * Jos organisaatiolla on yksi tietojärjestelmä, jossa kaikki reseptit kirjoitetaan, reseptin solmuluokka on 93.
     */
    private static final String medicalNode = "93";
    public static final char SEPR = '.';

    /**
     * Suomen maa-tunnus.
     */
    private static String baseOid = "1.2.246.";
    private static String sosJaTervhuolto = "537";
    private static final String TYHJA = "";
    private static final String VALIVIIVA = "-";
    private static final String NOLLA = "0";
    private static final String AMO_ROOT = "30";
    private static final String SV_ROOT = "25";
    private static final String ORG_OID_TEMPLATE = OidGenerator.baseOid + OidGenerator.sosJaTervhuolto
            + OidGenerator.SEPR + "%1$s.%2$s";

    private static OidGenerator instance;

    private String lastOidGenerated = null;
    // Luokka on singleton, joten ei merkitystä vaikka staattinen. Helpottaa testeissä.
    private static long sequence = 1;

    public static OidGenerator getInstance() {

        if ( OidGenerator.instance == null ) {
            OidGenerator.instance = new OidGenerator();
        }
        return OidGenerator.instance;
    }

    /**
     * Default konstruktori
     */
    private OidGenerator() {
    }

    /**
     * Luo uuden document oid:in perustuen henkilö-authoriin (537-solmuinen oid), ilman juoksevaa counteria.
     *
     * @return
     */
    public synchronized final String createNewDocumentOid(String orgOid) {
        GregorianCalendar greg = new GregorianCalendar();
        StringBuilder oid = new StringBuilder();
        oid.append(orgOid).append(OidGenerator.SEPR);
        oid.append(OidGenerator.medicalNode).append(OidGenerator.SEPR);
        oid.append(greg.get(Calendar.YEAR)).append(OidGenerator.SEPR);
        oid.append(Integer.toString(greg.get(Calendar.MONTH) + 1)); // Kuukausien numerointi lähtee 0:sta
        oid.append(muutaPaivaKahdelleNumerolle(Integer.toString(greg.get(Calendar.DAY_OF_MONTH))));
        oid.append(OidGenerator.SEPR);
        oid.append(Integer.toString(greg.get(Calendar.HOUR_OF_DAY) + 1)); // tunnit aloitetaan 1:stä, koska
                                                                          // oidin solmu ei voi alkaa 0:lla
        oid.append(Integer.toString(greg.get(Calendar.MINUTE)));
        oid.append(Integer.toString(greg.get(Calendar.SECOND)));
        notifyAll();
        return oid.toString();
    }

    /**
     * Luo uusi oid perustuen annettuun document oid:iin. Käytännössä lisää perään vain juoksevan numeroinnin.
     *
     * @param documentOid
     *            document oid, muotoa 1.2.246.orgRootOid.orgYId.93.year (arvoa ei validoida)
     * @return Uusi oid perustuen annettuun pohja-oidiin
     */
    public synchronized final String createNewOidInSequence(String documentOid) {

        StringBuilder oid = new StringBuilder(documentOid);
        oid.append(OidGenerator.SEPR);
        oid.append(OidGenerator.sequence);
        OidGenerator.sequence++;

        String newOid = oid.toString();
        // Ei pitäisi olla mahdollista, mutta katsotaan nyt varmuuden vuoksi kuitenkin
        if ( newOid.equals(getLastOidGenerated()) ) {
            // Rekursiivisesti luo uusi oid uudella ajalla
            // Muokattu siten, ettei tänne pitäisi edes päätyä
            newOid = createNewOidInSequence(documentOid);
        }
        else {
            lastOidGenerated = newOid;
        }
        notifyAll();
        return newOid;
    }

    /**
     * Luo uuden oidin AMO:n organisaatiota varten.
     *
     * @param terhikkiId
     * @return Organisaation oid, esim. 1.2.246.537.28.10000196039
     */
    public synchronized final String createNewAMOOrganizationOid(String rekisterinumero) {
        Object[] args = { OidGenerator.AMO_ROOT, rekisterinumero };
        return String.format(OidGenerator.ORG_OID_TEMPLATE, args);
    }

    /**
     * Luo uuden henkilökohtaisen oidin sv-numeron perusteella.
     *
     * @param svnumero
     *            sv-numero
     * @return Henkilökohtainen oid, esim. 1.2.246.537.25.&lt;sv-numero&gt;
     */
    public synchronized final String createNewSvOid(String svnumero) {
        Object[] args = { OidGenerator.SV_ROOT, svnumero };
        return String.format(OidGenerator.ORG_OID_TEMPLATE, args);
    }

    /**
     * Mikäli sattuu tulemaan tarvetta tarkistaa viimeksi generoitu oid.
     *
     * @return Viimeksi generoitu oid
     */
    public String getLastOidGenerated() {
        return lastOidGenerated;
    }

    /**
     * Asettaa base oid:in, jota käytetään root oid:n generointiin.<br/>
     * <b>Huom. Luokka on singleton ja muutenkin muutetaan staattista muuttujaa, joten tämä vaikuttaa kaikkiin luokan
     * instansseihin.</b>
     *
     * @return base oid
     */
    public static String getBaseOid() {
        return OidGenerator.baseOid;
    }

    /**
     * Asettaa base oid:in, jota käytetään root oid:n generointiin.<br/>
     * <b>Huom. Luokka on singleton ja muutenkin muutetaan staattista muuttujaa, joten tämä vaikuttaa kaikkiin luokan
     * instansseihin.</b>
     *
     * @param baseOid
     *            Root oid:n generointiin käytetty 'base oid'
     */
    public static void setBaseOid(String baseOid) {

        if ( baseOid != null && baseOid.trim().length() > 0 ) {
            if ( !baseOid.endsWith(Character.toString(OidGenerator.SEPR)) ) {
                OidGenerator.baseOid = baseOid + OidGenerator.SEPR;
            }
            else {
                OidGenerator.baseOid = baseOid;
            }
        }
        else {
            // Ei muuteta arvoa jos annettu tyhjä String
            throw new InvalidParameterException("Base oid ei saa olla tyhjä");
        }
    }

    /**
     * Antaa millisekuntiajan ilman vuositietoa. Alusta poistetaan nollat jos niitä on.<br/>
     * <b>Poistettu käytöstä, koska testatessa ilmeni ongelmia. Parempi käyttää vain timestampia 'as is'.</b><br/>
     * (Katsotaan ehkä joskus myöhemmin, mikäli käytössä olisi mitään järkeä.)
     *
     * @param millis
     *            Millisekuntiaika, jota käytetään pohjana sekvenssiarvon generoimiseen
     * @return Millisekunnit ilman vuosia
     * @deprecated
     */
    @Deprecated
    protected String getYearlessMillis(long millis) {

        GregorianCalendar gregZero = new GregorianCalendar();
        gregZero.setTimeInMillis(millis);
        gregZero.set(Calendar.MONTH, 0);
        gregZero.set(Calendar.DAY_OF_MONTH, 1);
        gregZero.set(Calendar.HOUR_OF_DAY, 0);
        gregZero.set(Calendar.MINUTE, 0);
        gregZero.set(Calendar.SECOND, 0);
        gregZero.set(Calendar.MILLISECOND, 0);
        long alteredMillis = gregZero.getTimeInMillis();

        return Long.toString(millis - alteredMillis).substring(0, 3);
    }

    /**
     * Metodi muokkaa annettua Y-tunnusta oikeaan muotoon seuraavan JHS 159 (SFS) määrityksen mukaan. <br/>
     * Yritys- ja yhteisötunnus on 1.2.246.10.xxxxxxxx, missä xxxxxxxx on 8 merkkiä pitkä Y-tunnus ilman väliviivaa. Jos
     * Y-tunnuksen ensimmäinen merkki on 0, on Y-tunnus 7 merkkiä pitkä.
     *
     * @{Link http://www.sfs.fi/julkaisut_ja_palvelut/palvelut/tunnukset}
     * @param ytunnus
     * @return
     */
    public String modifyBusinessId(String ytunnus) {

        String stripped = ytunnus.replaceAll(OidGenerator.VALIVIIVA, OidGenerator.TYHJA);
        if ( stripped.startsWith(OidGenerator.NOLLA) ) {
            return stripped.replaceFirst(OidGenerator.NOLLA, OidGenerator.TYHJA);
        }
        return stripped;
    }

    /**
     * Asettaa oid:in juoksevan numeron takaisin nollaan. Tätä tarvitaan testeissä, älä käytä muuten.
     * 
     * @deprecated
     */
    @Deprecated
    public static void resetSequence() {

        OidGenerator.sequence = 1;
    }

    /**
     * Luo uusi oid perustuen annettuun document oid:iin. Käytännössä lisää perään parametrina annetun
     * dokumenttikohtaisen juoksevan numeroinnin.
     *
     * @param documentOid
     *            document oid, muotoa 1.2.246.orgRootOid.orgYId.93.year (arvoa ei validoida)
     * @param sequence,
     *            järjestysnumero
     * @return Uusi oid perustuen annettuun pohja-oidiin
     */
    public synchronized final String createNewOidManualSequence(String documentOid, long sequence) {

        StringBuilder oid = new StringBuilder(documentOid);
        oid.append(OidGenerator.SEPR);
        oid.append(sequence);
        String newOid = oid.toString();
        notifyAll();
        return newOid;
    }

    private static String muutaPaivaKahdelleNumerolle(String paiva) {
        return paiva.length() < 2 ? "0" + paiva : paiva;
    }
}
