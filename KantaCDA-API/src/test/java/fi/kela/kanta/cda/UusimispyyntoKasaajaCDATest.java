package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.hl7.v3.CE;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040Location;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040Place;
import org.junit.Test;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.UusimispyyntoTO;

/**
 * Testiluokka jonka avulla voidaan generoida uusimispyyntö cda
 */
public class UusimispyyntoKasaajaCDATest {

    private static final String propertyFile = "testi_properties.properties";
    private static final String testVastaanottajaId = "1.2.3.4.5";
    private static final String testVastaanottajaNimi = "VastaanottajaNimi";
    private static final String testVastaanottajaKatu = "VastaanottajaKatu";
    private static final String testVastaanottajaPostinumero = "123456";
    private static final String testVastaanottajaKaupunki = "VastaanottajaKaupunki";
    private static final String testVastaanottajaPuhelinnumero = "1234567";
    private static final String testMaaraajaEtunimi = "MäärääjäEtunimi";
    private static final String testMaaraajaSukunimi = "MäärääjäSukunimi";
    private static final String testMaaraajaId = "87654321";
    private static final String testHetu = "123456-321O";
    private static final String testEtunumi = "Etunimi";
    private static final String testSukunimi = "Sukunimi";
    private static final String testPuhelinnumero = "0451234567";
    private static final String testUusittavaLaakemaaraysOid = "1.12.123.1234.12345.123456";
    private static final String testUusittavaLaakemaaraysSetId = "1.12.123.1234.12345.123456.1";
    private static final String testValmisteenNimi = "ValmisteenNimi";
    private static final String testEffectiveTime = "20151014093130";
    private static final String testOrgYTunnus = "1234567890";

    private UusimispyyntoKasaaja setupUusimispyyntoKasaaja(UusimispyyntoTO uusimispyynto) throws IOException {
        return new UusimispyyntoKasaaja(KantaCDATestUtils.loadProperties(propertyFile), uusimispyynto);
    }

    private KokoNimiTO getTestMaaraajanKokoNimi() {
        KokoNimiTO testiNimi = new KokoNimiTO();
        testiNimi.lisaa("family", "", "Kvaak");
        testiNimi.lisaa("given", "", testEtunumi);
        testiNimi.lisaa("given", "AD", "Alfred");
        testiNimi.lisaa("given", "IN", "J");
        testiNimi.lisaa("given", "CL", "Aatu");
        testiNimi.lisaa("prefix", "NB", "Sir");
        testiNimi.lisaa("suffix", "TITLE", "Loppuliite");
        testiNimi.lisaa("suffix", "PR", "PhD");
        testiNimi.lisaa("delimiter", "", "Välimerkki");
        return testiNimi;
    }

    private void taytaPakollisetTiedot(UusimispyyntoTO to) throws ParseException {
        to.setVastaanottajaId(testVastaanottajaId);
        to.setVastaanottajaNimi(testVastaanottajaNimi);
        to.setVastaanottajaKatu(testVastaanottajaKatu);
        to.setVastaanottajaPostinumero(testVastaanottajaPostinumero);
        to.setVastaanottajaKaupunki(testVastaanottajaKaupunki);
        to.setVastaanottajaPuhelinnumero(testVastaanottajaPuhelinnumero);

        to.setMaaraajanKokonimi(getTestMaaraajanKokoNimi());
        to.setMaaraajanId(testMaaraajaId);

        HenkilotiedotTO ht = new HenkilotiedotTO(new KokoNimiTO(testEtunumi, testSukunimi), testHetu);
        to.setHenkilotiedot(ht);
        to.setMatkapuhelinnumero(testPuhelinnumero);

        to.setUusittavaLaakemaaraysOid(testUusittavaLaakemaaraysOid);
        to.setUusittavaLaakemaaraysSetId(testUusittavaLaakemaaraysSetId);
        to.setValmisteenNimi(testValmisteenNimi);

        to.setAikaleima(new SimpleDateFormat("yyyyMMddhhmmss").parse(testEffectiveTime));

        to.setSuostumus(4);

        to.setLaatimispaikka(KantaCDATestUtils.luoOrganisaatio());
        to.getLaatimispaikka().setNimi("Testi Laatimispaikka");
        to.getLaatimispaikka().setToimintaYksikko(KantaCDATestUtils.luoOrganisaatio());
        to.getLaatimispaikka().getToimintaYksikko().setNimi("Test TOIMINTAYKSIKKÖ");

        // to.setOrgYTunnus(testOrgYTunnus);
    }

    private OrganisaatioTO lisaaToimintayksikko() {
        return KantaCDATestUtils.luoOrganisaatio();
    }

    private void lisaaUusija(UusimispyyntoTO to) {
        AmmattihenkiloTO uusija = KantaCDATestUtils.luoAmmattihenkilo();
        uusija.getOrganisaatio().setToimintaYksikko(lisaaToimintayksikko());
        to.setUusija(uusija);
    }

    @Test
    public void testKasaaUusimispyynto() throws IOException, ParseException {
        UusimispyyntoTO uusimispyynto = new UusimispyyntoTO();

        taytaPakollisetTiedot(uusimispyynto);

        lisaaUusija(uusimispyynto);

        UusimispyyntoKasaaja kasaaja = setupUusimispyyntoKasaaja(uusimispyynto);

        // <DEBUG>
        // String cda = kasaaja.kasaaReseptiAsiakirja();
        // assertNotNull("cda ei voi olla null",cda);
        // System.out.println(cda);
        // </DEBUG>

        POCDMT000040ClinicalDocument doc = kasaaja.kasaaReseptiCDA();
        assertNotNull("doc ei voi olla null", doc);

        POCDMT000040Organization representedOrganization = doc.getAuthors().get(0).getAssignedAuthor()
                .getRepresentedOrganization();
        assertNotNull("RepresentedOrganization ei voi olla null", representedOrganization);
        assertFalse("representedOrganization/name ei voi olla tyhjä", representedOrganization.getNames().isEmpty());
        assertFalse("representedOrganization/addr ei voi olla tyhjä", representedOrganization.getAddrs().isEmpty());
        assertNotNull("representedOrganization/asOrganizationPartOf ei voi olla null",
                representedOrganization.getAsOrganizationPartOf());

        POCDMT000040Location location = doc.getComponentOf().getEncompassingEncounter().getLocation();
        assertNotNull("componentOf/encompassingEncounter/location ei voi olla null", location);
        POCDMT000040Place place = location.getHealthCareFacility().getLocation();
        assertNotNull("componentOf/encompassingEncounter/location/healthcareFacility/location/addr ei voi olla null",
                place.getAddr());
        assertNotNull(
                "componentOf/encompassingEncounter/location/healthcareFacility/serviceProviderOrganization ei voi olla null",
                location.getHealthCareFacility().getServiceProviderOrganization());

        POCDMT000040Entry uusimispyyntoEntry = doc.getComponent().getStructuredBody().getComponents().get(0)
                .getSection().getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()
                .get(0);
        assertNotNull("uusimispyynnön kohde organisaation tiedoissa tulee olla playingEntity (tyhjänä)",
                uusimispyyntoEntry.getAct().getParticipants().get(0).getParticipantRole().getPlayingEntity());

        // propseissa asetettu subject.relatedSubject.code.codeSystemName tyhjäksi joten tarkistetaan että on null
        CE code = uusimispyyntoEntry.getAct().getSubject().getRelatedSubject().getCode();
        assertNull("uusimispyynnössä potilaan codeSystemName ei saa olla Lääkityslista", code.getCodeSystemName());

        POCDMT000040Entry valmisteennimiEntry = doc.getComponent().getStructuredBody().getComponents().get(0)
                .getSection().getComponents().get(0).getSection().getComponents().get(0).getSection().getEntries()
                .get(1);
        assertEquals("määrääjän nimi ei saa vaihtua (nimiä pitää olla samamäärä)",
                getTestMaaraajanKokoNimi().getNimet().size(),
                valmisteennimiEntry.getSubstanceAdministration().getAuthors().get(0).getAssignedAuthor()
                        .getAssignedPerson().getNames().get(0).getContent().size());
    }
}
