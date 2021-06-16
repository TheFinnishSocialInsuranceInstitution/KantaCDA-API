package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import fi.kela.kanta.to.LaakemaarayksenToimitusTO;

public class ReseptinToimituksenPurkajaCDATest {

    private static final String testToimitusXMLFile = "LT1_testiasiakirja_toimitus.xml";

    private String dateToString(Date date) {
        return dateToString(date, 100);
    }

    private String dateToString(Date date, int len) {
        if ( null != date ) {
            String pattern = "yyyyMMddHHmmss";
            if ( len > pattern.length() ) {
                len = pattern.length();
            }
            String newPattern = pattern.substring(0, len);
            SimpleDateFormat sdf = new SimpleDateFormat(newPattern);
            sdf.setTimeZone(TimeZone.getTimeZone("EET"));
            return sdf.format(date);
        }
        return "null";
    }

    @Test
    public void testPuraLaakemaarayksenToimitus() throws Exception {
        String cda = KantaCDATestUtils.lataa(testToimitusXMLFile);
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO toimitus = purkaja.puraLaakemaarayksenToimitus(cda);
        assertNotNull("Toimitus ei voi olla null", toimitus);
        assertNotNull("Toimituksen oid ei voi olla null", toimitus.getOid());
        assertNotNull("Toimituksen setId ei voi olla null", toimitus.getSetId());
        assertNotNull("Toimituksen author ei voi olla null", toimitus.getAmmattihenkilo());
        assertNotNull("Toimituksen aikaleima ei voi olla null", toimitus.getAikaleima());
        assertNotNull("Toimituksen määräyspäivä ei voi olla null", toimitus.getMaarayspaiva());
        assertNotNull("Toimituksen valmiste ei voi olla null", toimitus.getValmiste());
        assertNotNull("Toimituksen valmisteen yksilointitiedot ei voi olla null",
                toimitus.getValmiste().getYksilointitiedot());

        assertEquals("Toimituksen oid tulee olla :'1.2.246.10.2323232.93.2014.2250'", "1.2.246.10.2323232.93.2014.2250",
                toimitus.getOid());
        assertEquals("Toimituksen setId tulee olla :'1.2.246.10.2323232.93.2014.2250'",
                "1.2.246.10.2323232.93.2014.2250", toimitus.getSetId());
        assertEquals("Toimituksen cda tyyppi tulee olla 10", 10, toimitus.getCdaTyyppi());
        // TODO: miksi headerista puretaan aikaleima minuutin tarkkuudella? Asiakirjalla aika 20140424094012
        assertEquals("Toimituksen aikaleima tulee olla: '20140424094000'", "20140424094000",
                dateToString(toimitus.getAikaleima()));
        assertEquals("Toimituksen versio numero tulee olla: 1", 1, toimitus.getVersio());

        assertEquals("Toimituksen tekijän rooli tulee olla LTE", "LTE", toimitus.getAmmattihenkilo().getRooli());
        assertEquals("Toimituksen tekijän rekisteröintinumero tulee olla :12312312312", "12312312312",
                toimitus.getAmmattihenkilo().getRekisterointinumero());
        assertEquals("Toimituksen tekijän ammattioikeus tulee olla: 005", "005",
                toimitus.getAmmattihenkilo().getAmmattioikeus());
        assertEquals("Toimituksen tehneen apteekin id tulee olla: 1.2.246.10.2323232.34", "1.2.246.10.2323232.34",
                toimitus.getAmmattihenkilo().getOrganisaatio().getYksilointitunnus());
        assertEquals("Toimituksen toimituspäivä (määräyspäivä) tulee olla :'20140424094012'", "20140424094012",
                dateToString(toimitus.getMaarayspaiva()));
        assertEquals("Toimitetun valmisteen vahvuus tulee olla : '10 mg'", "10 mg",
                toimitus.getValmiste().getYksilointitiedot().getVahvuus());
        assertEquals("Toimitetun valmisteen ATC-koodin tulee olla: 'N05BA01'", "N05BA01",
                toimitus.getValmiste().getYksilointitiedot().getATCkoodi());
        assertEquals("Toimitetun valmisteen ATC-koodin mukainen nimi tulee olla: 'Diatsepaami'", "Diatsepaami",
                toimitus.getValmiste().getYksilointitiedot().getATCnimi());
        assertEquals("Toimitetun valmisteen lääketietokannan versio tulee olla: '2014.008'", "2014.008",
                toimitus.getLaaketietokannanVersio());
        assertEquals("Toimitetun kokonaismäärän koon tulee olla: 21", BigDecimal.valueOf(21),
                toimitus.getToimitettuKokonaismaaraValue());
        assertEquals("Toimitetun kokonaismäärän yksikön tulee olla: 'fol'", "fol",
                toimitus.getToimitettuKokonaismaaraUnit());
        assertEquals("Jäljellä oleva määrä tulee olla: '3X21'", "3X21", toimitus.getJaljellaOlevaMaaraOriginal());
        assertEquals("Jäljellä olevan määrän yksikkö tulee olla: 'fol'", "fol", toimitus.getJaljellaOlevaMaaraUnit());
        assertEquals("Toimituettujen pakkausten lukumaara tulee olla: 1", 1,
                toimitus.getPakkauksienLukumaara().intValue());
        assertEquals("Toimitettu pakkauskoko tulee olla : 21", 21,
                (int) toimitus.getValmiste().getYksilointitiedot().getPakkauskoko());
        assertEquals("Toimitetun pakkauskoon yksikkö tulee olla: 'fol'", "fol",
                toimitus.getValmiste().getYksilointitiedot().getPakkausyksikko());
        assertEquals("Toimitetun valmisteen VNR-numero tulee olla: '469452'", "469452",
                toimitus.getValmiste().getYksilointitiedot().getYksilointitunnus());
        assertEquals("Toimitetun valmisteen kauppanimi tulee olla: 'DIAPAM'", "DIAPAM",
                toimitus.getValmiste().getYksilointitiedot().getKauppanimi());
        assertEquals("Toimitetun valmisteen myyntiluvan haltija tulee olla: 'ORION OYJ'", "ORION OYJ",
                toimitus.getValmiste().getYksilointitiedot().getMyyntiluvanHaltija());
        assertEquals("Toimitetun valmisteen lääkemuoto tulee olla: 'tabletti'", "tabletti",
                toimitus.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        assertTrue("Osapakkaustiedon osotin tulee olla true", toimitus.isOsapakkaus());
        assertFalse("Apteekissa valmistettavan lääkkeen osoitin tulee olla false",
                toimitus.isApteekissaValmistettavaLaake());
        assertEquals("Toimitetun valmisteen pakkauskoon kerroin tulee olla: 1", 1,
                toimitus.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        assertEquals("Toimitetun valmisteen laji tulee olla: '1'", "1",
                toimitus.getValmiste().getYksilointitiedot().getValmisteenLaji());

        assertFalse("isHuume tulee olla false", toimitus.isHuume());
        assertFalse("isKokonaanaToimitettu tulee olla false", toimitus.isKokonaanToimitettu());
    }

}
