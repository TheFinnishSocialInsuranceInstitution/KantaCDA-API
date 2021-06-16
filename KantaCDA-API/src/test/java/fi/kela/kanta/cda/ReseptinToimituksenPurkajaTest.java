package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.BL;
import org.hl7.v3.CE;
import org.hl7.v3.II;
import org.hl7.v3.INT;
import org.hl7.v3.MO;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Organizer;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040Supply;
import org.hl7.v3.PQ;
import org.hl7.v3.PQR;
import org.hl7.v3.ST;
import org.hl7.v3.XActRelationshipDocument;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.LaakemaarayksenToimitusTO;

public class ReseptinToimituksenPurkajaTest {

    private ObjectFactory of;

    @Before
    public void setup() {
        of = new ObjectFactory();
    }

    @Test
    public void getCodeSystemTest() throws ConfigurationException {
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        assertNotNull("CodeSystem ei voi olla null", purkaja.getCodeSystem());
    }

    @Test
    public void puraLaakemaarayksenToimitusTest() {
        // TODO
    }

    // puraRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument, LaakemaarayksenToimitusTO toimitus)
    @Test
    public void puraRelatedDocumentTest() throws Exception {
        String testSetId = "1.2.3.4.5";
        String testId = "1.2.3.4.5.6";
        String testOtherSetId = "5.4.3.2.1";
        String testOtherId = "6.5.4.3.2.1";
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        POCDMT000040RelatedDocument relatedDocument = of.createPOCDMT000040RelatedDocument();
        POCDMT000040RelatedDocument otherRelatedDocument = of.createPOCDMT000040RelatedDocument();
        relatedDocument.setParentDocument(of.createPOCDMT000040ParentDocument());
        otherRelatedDocument.setParentDocument(of.createPOCDMT000040ParentDocument());
        relatedDocument.getParentDocument().setSetId(of.createII());
        relatedDocument.getParentDocument().getSetId().setRoot(testSetId);
        otherRelatedDocument.getParentDocument().setSetId(of.createII());
        otherRelatedDocument.getParentDocument().getSetId().setRoot(testOtherSetId);
        II id = of.createII();
        id.setRoot(testId);
        II otherId = of.createII();
        otherId.setRoot(testOtherId);
        relatedDocument.getParentDocument().getIds().add(id);
        otherRelatedDocument.getParentDocument().getIds().add(otherId);
        relatedDocument.setTypeCode(XActRelationshipDocument.APND);
        otherRelatedDocument.setTypeCode(XActRelationshipDocument.RPLC);
        cda.getRelatedDocuments().add(relatedDocument);
        cda.getRelatedDocuments().add(otherRelatedDocument);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraRelatedDocument", cda, to);

        assertEquals("LaakemaarayksenYksilointitunnus tulee olla : " + testId, testId,
                to.getLaakemaarayksenYksilointitunnus());
    }

    @Test
    public void puraRelatedDocumentTest_NoReleatedDocument() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraRelatedDocument", cda, to);

        assertNull("LaakemaarayksenYksilointitunnus tulee olla null", to.getLaakemaarayksenYksilointitunnus());
    }

    // puraToimitusTiedot(POCDMT000040ClinicalDocument, LaakemaarayksenToimitusTO)
    @Test
    public void puraToimitusTiedotTest() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        POCDMT000040Component3 component3 = of.createPOCDMT000040Component3();
        POCDMT000040Component5 component5a = of.createPOCDMT000040Component5();
        POCDMT000040Component5 component5b = of.createPOCDMT000040Component5();
        component5b.setSection(of.createPOCDMT000040Section());
        component5a.setSection(of.createPOCDMT000040Section());
        component5a.getSection().getComponents().add(component5b);
        component3.setSection(of.createPOCDMT000040Section());
        component3.getSection().getComponents().add(component5a);
        cda.setComponent(of.createPOCDMT000040Component2());
        cda.getComponent().setStructuredBody(of.createPOCDMT000040StructuredBody());
        cda.getComponent().getStructuredBody().getComponents().add(component3);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraToimitusTiedot", cda, to);
    }

    // puraEntryt(POCDMT000040Section, LaakemaarayksenToimitusTO)
    @Test
    public void puraEntrytTest() throws Exception {
        POCDMT000040Section section = of.createPOCDMT000040Section();
        POCDMT000040Entry valmisteJaPakkausEntry = of.createPOCDMT000040Entry();
        POCDMT000040Entry toimituksenMuutTiedotEntry = of.createPOCDMT000040Entry();
        POCDMT000040Entry randomEntry1 = of.createPOCDMT000040Entry();
        POCDMT000040Entry randomEntry2 = of.createPOCDMT000040Entry();
        POCDMT000040Entry randomEntry3 = of.createPOCDMT000040Entry();
        POCDMT000040Entry randomEntry4 = of.createPOCDMT000040Entry();

        valmisteJaPakkausEntry.setOrganizer(of.createPOCDMT000040Organizer());
        toimituksenMuutTiedotEntry.setOrganizer(of.createPOCDMT000040Organizer());
        randomEntry1.setOrganizer(of.createPOCDMT000040Organizer());
        randomEntry2.setOrganizer(of.createPOCDMT000040Organizer());
        randomEntry3.setOrganizer(of.createPOCDMT000040Organizer());

        valmisteJaPakkausEntry.getOrganizer().setCode(of.createCD());
        toimituksenMuutTiedotEntry.getOrganizer().setCode(of.createCD());
        randomEntry1.getOrganizer().setCode(of.createCD());
        randomEntry2.getOrganizer().setCode(of.createCD());

        valmisteJaPakkausEntry.getOrganizer().getCode()
                .setCode(KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT_TOIMITUSSANOMASSA);
        toimituksenMuutTiedotEntry.getOrganizer().getCode()
                .setCode(KantaCDAConstants.Laakityslista.TOIMITUKSEN_MUUT_TIEDOT);
        randomEntry1.getOrganizer().getCode().setCode("DIIPADAAPACODE");

        valmisteJaPakkausEntry.getOrganizer().getComponents().add(of.createPOCDMT000040Component4());

        section.getEntries().add(valmisteJaPakkausEntry);
        section.getEntries().add(toimituksenMuutTiedotEntry);
        section.getEntries().add(randomEntry1);
        section.getEntries().add(randomEntry2);
        section.getEntries().add(randomEntry3);
        section.getEntries().add(randomEntry4);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraEntryt", section, to);

    }

    // puraToimituksenMuutTiedot(POCDMT000040Organizer, LaakemaarayksenToimitusTO)
    @Test
    public void puraToimituksenMuutTiedotTest() throws Exception {
        POCDMT000040Organizer organizer = of.createPOCDMT000040Organizer();
        POCDMT000040Component4 hintaComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation hintaObs = of.createPOCDMT000040Observation();
        hintaObs.setCode(of.createCD());
        hintaObs.getCode().setCode(KantaCDAConstants.Laakityslista.TOIMITUKSEN_HINTA);
        hintaComponent.setObservation(hintaObs);
        POCDMT000040Component4 laakevaihtokieltoComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation laakevaihtokieltoObs = of.createPOCDMT000040Observation();
        laakevaihtokieltoObs.setCode(of.createCD());
        laakevaihtokieltoObs.getCode()
                .setCode(KantaCDAConstants.Laakityslista.TOIMITETTU_HINTAPUTKEEN_KUULUMATONTA_LAAKETTA);
        laakevaihtokieltoComponent.setObservation(laakevaihtokieltoObs);
        POCDMT000040Component4 omavastuuosuusComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation omavastuuosuusObs = of.createPOCDMT000040Observation();
        omavastuuosuusObs.setCode(of.createCD());
        omavastuuosuusObs.getCode().setCode(KantaCDAConstants.Laakityslista.OMAVASTUUOSUUKSIEN_LUKUMAARA);
        omavastuuosuusComponent.setObservation(omavastuuosuusObs);
        POCDMT000040Component4 kokonaantoimitettuComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation kokonaantoimitettuObs = of.createPOCDMT000040Observation();
        kokonaantoimitettuObs.setCode(of.createCD());
        kokonaantoimitettuObs.getCode().setCode(KantaCDAConstants.Laakityslista.KOKONAAN_TOIMITETTU);
        kokonaantoimitettuComponent.setObservation(kokonaantoimitettuObs);
        POCDMT000040Component4 laakevaihdettuComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation laakevaihdettuObs = of.createPOCDMT000040Observation();
        laakevaihdettuObs.setCode(of.createCD());
        laakevaihdettuObs.getCode().setCode(KantaCDAConstants.Laakityslista.LAAKE_VAIHDETTU);
        laakevaihdettuComponent.setObservation(laakevaihdettuObs);
        POCDMT000040Component4 annosjakeluComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation annosjakeluObs = of.createPOCDMT000040Observation();
        annosjakeluObs.setCode(of.createCD());
        annosjakeluObs.getCode().setCode(KantaCDAConstants.Laakityslista.ANNOSJAKELU);
        annosjakeluComponent.setObservation(annosjakeluObs);
        POCDMT000040Component4 apteekinhuomautusComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation apteekinhuomautusObs = of.createPOCDMT000040Observation();
        apteekinhuomautusObs.setCode(of.createCD());
        apteekinhuomautusObs.getCode().setCode(KantaCDAConstants.Laakityslista.APTEEKIN_HUOMAUTUS);
        apteekinhuomautusComponent.setObservation(apteekinhuomautusObs);
        POCDMT000040Component4 huumausainePkvComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation huumausainePkvObs = of.createPOCDMT000040Observation();
        huumausainePkvObs.setCode(of.createCD());
        huumausainePkvObs.getCode().setCode(KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS);
        huumausainePkvComponent.setObservation(huumausainePkvObs);
        POCDMT000040Component4 huumeComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation huumeObs = of.createPOCDMT000040Observation();
        huumeObs.setCode(of.createCD());
        huumeObs.getCode().setCode(KantaCDAConstants.Laakityslista.HUUME);
        huumeComponent.setObservation(huumeObs);
        POCDMT000040Component4 aptAnnosOhjeComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation aptAnnosOhjeObs = of.createPOCDMT000040Observation();
        aptAnnosOhjeObs.setCode(of.createCD());
        aptAnnosOhjeObs.getCode().setCode(KantaCDAConstants.Laakityslista.APTEEKIN_AUKIKIRJOITTAMA_ANNOSTUSOHJE);
        aptAnnosOhjeComponent.setObservation(aptAnnosOhjeObs);
        POCDMT000040Component4 lisaselvitysKelalleComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation lisaselvitysKelalleObs = of.createPOCDMT000040Observation();
        lisaselvitysKelalleObs.setCode(of.createCD());
        lisaselvitysKelalleObs.getCode().setCode(KantaCDAConstants.Laakityslista.LISASELVITYS_KELALLE);
        lisaselvitysKelalleComponent.setObservation(lisaselvitysKelalleObs);

        POCDMT000040Component4 missingObsComponent = of.createPOCDMT000040Component4();
        POCDMT000040Component4 missingCodeComponent = of.createPOCDMT000040Component4();
        missingCodeComponent.setObservation(of.createPOCDMT000040Observation());
        POCDMT000040Component4 missingCodeCodeComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation missingCodeCodeObs = of.createPOCDMT000040Observation();
        missingCodeCodeObs.setCode(of.createCD());
        missingCodeCodeComponent.setObservation(missingCodeCodeObs);
        POCDMT000040Component4 invalidCodeComponent = of.createPOCDMT000040Component4();
        POCDMT000040Observation invalidCodeObs = of.createPOCDMT000040Observation();
        invalidCodeObs.setCode(of.createCD());
        invalidCodeObs.getCode().setCode("KARVALAKKI");
        invalidCodeComponent.setObservation(invalidCodeObs);

        organizer.getComponents().add(hintaComponent);
        organizer.getComponents().add(laakevaihtokieltoComponent);
        organizer.getComponents().add(omavastuuosuusComponent);
        organizer.getComponents().add(kokonaantoimitettuComponent);
        organizer.getComponents().add(laakevaihdettuComponent);
        organizer.getComponents().add(annosjakeluComponent);
        organizer.getComponents().add(apteekinhuomautusComponent);
        organizer.getComponents().add(huumausainePkvComponent);
        organizer.getComponents().add(huumeComponent);
        organizer.getComponents().add(aptAnnosOhjeComponent);
        organizer.getComponents().add(lisaselvitysKelalleComponent);

        organizer.getComponents().add(missingCodeCodeComponent);
        organizer.getComponents().add(missingCodeComponent);
        organizer.getComponents().add(missingObsComponent);
        organizer.getComponents().add(invalidCodeComponent);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraToimituksenMuutTiedot", organizer, to);

    }

    // puraLisaselvitysKelalle(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraLisaselvitysKelalleTest() throws Exception {
        String lisaselvityskelalle = "Lisäselvitys Kelalle teksti";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ST value = of.createST();
        value.getContent().add(lisaselvityskelalle);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraLisaselvitysKelalle", observation, to);

        assertEquals("LisaselvitysKelalle tulee olla: " + lisaselvityskelalle, lisaselvityskelalle,
                to.getLisaselvitysKelalle());
    }

    @Test
    public void puraLisaselvitysKelalleTest_NullFlavorNI() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ST value = of.createST();
        value.getNullFlavors().add(KantaCDAConstants.NullFlavor.NI.getCode());
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraLisaselvitysKelalle", observation, to);

        assertNull("LisaselvitysKelalle tulee olla null", to.getLisaselvitysKelalle());
    }

    @Test
    public void puraLisaselvitysKelalleTest_NoValue() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraLisaselvitysKelalle", observation, to);

        assertNull("LisaselvitysKelalle tulee olla null", to.getLisaselvitysKelalle());
    }

    // puraAnnostusohje(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraAnnostusohjeTest() throws Exception {
        String annostusohje = "annostusohje teksti";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ST value = of.createST();
        value.getContent().add(annostusohje);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraAnnostusohje", observation, to);

        assertEquals("Annostusohje tulee olla: " + annostusohje, annostusohje, to.getAnnostusohje());
    }

    @Test
    public void puraAnnostusohjeTest_NullFlavorNI() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ST value = of.createST();
        value.getNullFlavors().add(KantaCDAConstants.NullFlavor.NI.getCode());
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraAnnostusohje", observation, to);

        assertNull("Annostusohje tulee olla null", to.getAnnostusohje());
    }

    @Test
    public void puraAnnostusohjeTest_NoValue() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraAnnostusohje", observation, to);

        assertNull("Annostusohje tulee olla null", to.getAnnostusohje());
    }

    // puraHuumausaineTieto(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraHuumausaineTietoTest() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(true);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraHuumausaineTieto", observation, to);

        assertTrue("isHuume tulee olla true", to.isHuume());
    }

    @Test
    public void puraHuumausaineTietoTest_NoValue() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraHuumausaineTieto", observation, to);

        assertFalse("isHuume tulee olla false", to.isHuume());
    }

    // puraHuumePKVLaakemaaraysTieto(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraHuumePKVLaakemaaraysTietoTest() throws Exception {
        String pkvCodeValue = "Z";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        CE value = of.createCE();
        value.setCode(pkvCodeValue);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraHuumePKVLaakemaaraysTieto", observation, to);

        assertEquals("PKVlaakemaarays tulee olla: " + pkvCodeValue, pkvCodeValue, to.getPKVlaakemaarays());
    }

    // puraApteekinHuomautus(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraApteekinHuomautusTest() throws Exception {
        String apteekinhuomautusteksti = "apteekin huomautus teksti";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ST value = of.createST();
        value.getContent().add(apteekinhuomautusteksti);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraApteekinHuomautus", observation, to);

        assertEquals("ApteekinHuomautus tulee olla: " + apteekinhuomautusteksti, apteekinhuomautusteksti,
                to.getApteekinHuomautus());
    }

    // puraLaakeVaihdettuTieto(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraLaakeVaihdettuTietoTest() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(true);
        observation.getValues().add(value);
        observation.setText(of.createED());

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraLaakeVaihdettuTieto", observation, to);

        assertTrue("isLaakeVaihdettu tulee olla true", to.isLaakeVaihdettu());
    }

    // puraKokonaanToimitettuTieto(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraKokonaanToimitettuTietoTest() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(true);
        observation.getValues().add(value);
        observation.setText(of.createED());

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraKokonaanToimitettuTieto", observation, to);

        assertTrue("isKokonaantoimitettu tulee olla true", to.isKokonaanToimitettu());
    }

    // puraOmavastuuosuuksienLukumaara(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraOmavastuuosuuksienLukumaaraTest() throws Exception {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        int testOmavastuuosuuksienLukumaara = 12;
        INT value = of.createINT();
        value.setValue(new BigInteger(String.valueOf(testOmavastuuosuuksienLukumaara)));
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraOmavastuuosuuksienLukumaara", observation, to);

        assertEquals("OmavastuuosuuksienLukumaara tulee olla: " + String.valueOf(testOmavastuuosuuksienLukumaara),
                testOmavastuuosuuksienLukumaara, to.getOmavastuuosuuksienLukumaara());
    }

    // puraToimituksenAnnosjakelu(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraToimituksenAnnosjakeluTest() throws Exception {
        String toimituksenannosjakeluteksti = "toimituksen annosjakelu teksti";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(true);
        observation.getValues().add(value);
        observation.setText(of.createED());
        observation.getText().getContent().add(toimituksenannosjakeluteksti);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraToimituksenAnnosjakelu", observation, to);

        assertTrue("isAnnosjakelu tulee olla true", to.isAnnosjakelu());
        assertEquals("AnnosjakeluTeksti tulee olla: " + toimituksenannosjakeluteksti, toimituksenannosjakeluteksti,
                to.getAnnosjakeluTeksti());
    }

    // puraLaakevaihtokielto(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraLaakevaihtokieltoTest() throws Exception {
        String laakevaihtokiellonsyycode = "syyCode";
        String laakevaihtokiellonselvitys = "lisäselvitys";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        CE value = of.createCE();
        value.setCode(laakevaihtokiellonsyycode);
        observation.getValues().add(value);
        observation.setText(of.createED());
        observation.getText().getContent().add(laakevaihtokiellonselvitys);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraLaakevaihtokielto", observation, to);

        assertEquals("LaakevaihtokiellonSyy tulee olla: " + laakevaihtokiellonsyycode, laakevaihtokiellonsyycode,
                to.getLaakevaihtokiellonSyy());
        assertEquals("LaakevaihtokiellonLisaselvitys tulee olla: " + laakevaihtokiellonselvitys,
                laakevaihtokiellonselvitys, to.getLaakevaihtokiellonLisaselvitys());
    }

    // puraToimituksenHinta(POCDMT000040Observation, LaakemaarayksenToimitusTO)
    @Test
    public void puraToimituksenHintaTest() throws Exception {
        int toimituksenhintavalue = 20;
        String toimituksenhintaunit = "CAPS";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        MO value = of.createMO();
        value.setValue(String.valueOf(toimituksenhintavalue));
        value.setCurrency(toimituksenhintaunit);
        observation.getValues().add(value);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraToimituksenHinta", observation, to);

        assertEquals("ToimituksenHintaValue tulee olla: " + String.valueOf(toimituksenhintavalue),
                toimituksenhintavalue, to.getToimituksenHintaValue(), 0);
        assertEquals("ToimituksenHintaUnit tulee olla: " + toimituksenhintaunit, toimituksenhintaunit,
                to.getToimituksenHintaUnit());
    }

    // puraToimitettuMaara(POCDMT000040Observation, LaakemaaraysTO)
    @Test
    public void puraToimitettuMaaraTest() throws Exception {
        BigDecimal toimitettumaara = BigDecimal.valueOf(20);
        String toimitettumaaraunit = "unitti";
        String toimitettumaaraoriginal = "500";
        String toimitettumaaratext = "toimitettumaaratext";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        PQ value = of.createPQ();
        PQR translation = of.createPQR();
        translation.setOriginalText(of.createED());
        translation.getOriginalText().getContent().add(toimitettumaaraoriginal);
        value.setValue(String.valueOf(toimitettumaara));
        value.setUnit(toimitettumaaraunit);
        value.getTranslations().add(translation);
        observation.getValues().add(value);
        observation.setText(of.createED());
        observation.getText().getContent().add(toimitettumaaratext);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraToimitettuMaara", observation, to);

        assertEquals("ToimitettuKokonaismaaraValue tulee olla: " + String.valueOf(toimitettumaara), toimitettumaara,
                to.getToimitettuKokonaismaaraValue());
        assertEquals("ToimitettuKokonaismaaraUnit tulee olla: " + toimitettumaaraunit, toimitettumaaraunit,
                to.getToimitettuKokonaismaaraUnit());
        assertEquals("ToimitettuKokonaismaaraOriginal tulee olla: " + toimitettumaaraoriginal, toimitettumaaraoriginal,
                to.getToimitettuKokonaismaaraOriginal());
        assertEquals("ToimitettuKokonaismaaraText tulee olla: " + toimitettumaaratext, toimitettumaaratext,
                to.getToimitettuKokonaismaaraText());
    }

    // puraJaljellaOlevaMaara(POCDMT000040Observation, LaakemaaraysTO)
    @Test
    public void puraJaljellaOlevaMaaraTest() throws Exception {
        BigDecimal jaljellaolevamaara = BigDecimal.valueOf(70);
        String jaljellaolevaunit = "unit";
        String jaljellaolevamaaraoriginal = "3X50";
        String jaljellaolevamaaratext = "jaljellaolevamaaratext";
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        PQ value = of.createPQ();
        PQR translation = of.createPQR();
        translation.setOriginalText(of.createED());
        translation.getOriginalText().getContent().add(jaljellaolevamaaraoriginal);
        value.setValue(String.valueOf(jaljellaolevamaara));
        value.setUnit(jaljellaolevaunit);
        value.getTranslations().add(translation);
        observation.getValues().add(value);
        observation.setText(of.createED());
        observation.getText().getContent().add(jaljellaolevamaaratext);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        Whitebox.invokeMethod(purkaja, "puraJaljellaOlevaMaara", observation, to);

        assertEquals("jaljellaOlevaMaaraValue tulee olla: " + String.valueOf(jaljellaolevamaara), jaljellaolevamaara,
                to.getJaljellaOlevaMaaraValue());
        assertEquals("jaljellaOlevaMaaraUnit tulee olla: " + jaljellaolevaunit, jaljellaolevaunit,
                to.getJaljellaOlevaMaaraUnit());
        assertEquals("jaljellaOlevaMaaraOriginal tulee olla: " + jaljellaolevamaaraoriginal, jaljellaolevamaaraoriginal,
                to.getJaljellaOlevaMaaraOriginal());
        assertEquals("jaljellaOlevaMaaraText tulee olla: " + jaljellaolevamaaratext, jaljellaolevamaaratext,
                to.getJaljellaOlevaMaaraText());
    }

    // puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(POCDMT000040EntryRelationship, LaakemaaraysTO)
    @Test
    public void puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipitTest_ToimitettuMaara()
            throws ConfigurationException {
        POCDMT000040EntryRelationship entryRelationship = of.createPOCDMT000040EntryRelationship();
        entryRelationship.setObservation(of.createPOCDMT000040Observation());
        entryRelationship.getObservation().setCode(of.createCD());
        entryRelationship.getObservation().getCode().setCode(KantaCDAConstants.Laakityslista.TOIMITETTU_MAARA);
        entryRelationship.getObservation().getValues().add(of.createPQ());

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(entryRelationship, to);

        assertEquals("Toimitettukokonaismäärä unit tulee olla: 1", "1", to.getToimitettuKokonaismaaraUnit());
    }

    @Test
    public void puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipitTest_JaljellaolevaMaara()
            throws ConfigurationException {
        POCDMT000040EntryRelationship entryRelationship = of.createPOCDMT000040EntryRelationship();
        entryRelationship.setObservation(of.createPOCDMT000040Observation());
        entryRelationship.getObservation().setCode(of.createCD());
        entryRelationship.getObservation().getCode().setCode(KantaCDAConstants.Laakityslista.JALJELLA_OLEVA_MAARA);
        entryRelationship.getObservation().getValues().add(of.createPQ());

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(entryRelationship, to);

        assertEquals("Jäljelläolevea määrä unit tulee olla: 1", "1", to.getJaljellaOlevaMaaraUnit());
    }

    // puraAsiakirjakohtaisetKokoJaMaaraTiedot(POCDMT000040Supply, LaakemaaraysTO)
    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest() throws ConfigurationException {
        String pakkausyksikko = "pakkausyksikkö";
        double pakkauskoko = 25.5;
        int pakkauksienLukumaara = 50;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setRepeatNumber(of.createIVLINT());
        supply.getRepeatNumber().setValue(new BigInteger(String.valueOf(pakkauksienLukumaara)));
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(pakkausyksikko);
        supply.getQuantity().setValue(String.valueOf(pakkauskoko));

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla: " + String.valueOf(pakkauksienLukumaara), pakkauksienLukumaara,
                to.getPakkauksienLukumaara().intValue());
        assertEquals("pakkausyksikön tulee olla: " + pakkausyksikko, pakkausyksikko,
                to.getValmiste().getYksilointitiedot().getPakkausyksikko());
        assertEquals("pakkauskoko tulee olla: " + String.valueOf(pakkauskoko), pakkauskoko,
                to.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_noSupply() throws ConfigurationException {
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(null, to);

        assertEquals("pakkauksien lukumäärän tulee olla 0", 0, to.getPakkauksienLukumaara().intValue());
        assertNull("valmisteen tulee olla null", to.getValmiste());
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_noRepeatNumber() throws ConfigurationException {
        String pakkausyksikko = "pakkausyksikkö";
        double pakkauskoko = 25.5;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(pakkausyksikko);
        supply.getQuantity().setValue(String.valueOf(pakkauskoko));

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla 0", 0, to.getPakkauksienLukumaara().intValue());
        assertEquals("pakkausyksikön tulee olla: " + pakkausyksikko, pakkausyksikko,
                to.getValmiste().getYksilointitiedot().getPakkausyksikko());
        assertEquals("pakkauskoko tulee olla: " + String.valueOf(pakkauskoko), pakkauskoko,
                to.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_norepeatNumberValue() throws ConfigurationException {
        String pakkausyksikko = "pakkausyksikkö";
        double pakkauskoko = 25.5;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setRepeatNumber(of.createIVLINT());
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(pakkausyksikko);
        supply.getQuantity().setValue(String.valueOf(pakkauskoko));

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla: 0", 0, to.getPakkauksienLukumaara().intValue());
        assertEquals("pakkausyksikön tulee olla: " + pakkausyksikko, pakkausyksikko,
                to.getValmiste().getYksilointitiedot().getPakkausyksikko());
        assertEquals("pakkauskoko tulee olla: " + String.valueOf(pakkauskoko), pakkauskoko,
                to.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_NoQuantity() throws ConfigurationException {
        int pakkauksienLukumaara = 50;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setRepeatNumber(of.createIVLINT());
        supply.getRepeatNumber().setValue(new BigInteger(String.valueOf(pakkauksienLukumaara)));

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla: " + String.valueOf(pakkauksienLukumaara), pakkauksienLukumaara,
                to.getPakkauksienLukumaara().intValue());
        assertNull("valmisteen tulee olla null", to.getValmiste());
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_NoQuantityUnit() throws ConfigurationException {
        double pakkauskoko = 25.5;
        int pakkauksienLukumaara = 50;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setRepeatNumber(of.createIVLINT());
        supply.getRepeatNumber().setValue(new BigInteger(String.valueOf(pakkauksienLukumaara)));
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setValue(String.valueOf(pakkauskoko));

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla: " + String.valueOf(pakkauksienLukumaara), pakkauksienLukumaara,
                to.getPakkauksienLukumaara().intValue());
        assertEquals("pakkauskoko tulee olla: " + String.valueOf(pakkauskoko), pakkauskoko,
                to.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        assertEquals("pakkausyksikön tulee olla: 1", "1", to.getValmiste().getYksilointitiedot().getPakkausyksikko());
    }

    @Test
    public void puraAsiakirjakohtaisetKokoJaMaaraTiedotTest_NoQuantityVlaue() throws ConfigurationException {
        String pakkausyksikko = "pakkausyksikkö";
        int pakkauksienLukumaara = 50;
        POCDMT000040Supply supply = of.createPOCDMT000040Supply();
        supply.setRepeatNumber(of.createIVLINT());
        supply.getRepeatNumber().setValue(new BigInteger(String.valueOf(pakkauksienLukumaara)));
        supply.setQuantity(of.createPQ());
        supply.getQuantity().setUnit(pakkausyksikko);

        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();

        purkaja.puraAsiakirjakohtaisetKokoJaMaaraTiedot(supply, to);

        assertEquals("pakkauksien lukumäärän tulee olla: " + String.valueOf(pakkauksienLukumaara), pakkauksienLukumaara,
                to.getPakkauksienLukumaara().intValue());
        assertNull("valmisteen tulee olla null", to.getValmiste());
    }

    // puraAsiakirjakohtainenSupplyEntryrelationsipObservation
    @Test
    public void puraAsiakirjakohtainenSupplyEntryrelationsipObservationTest() throws ConfigurationException {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(true);
        observation.getValues().add(value);
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();
        purkaja.puraAsiakirjakohtainenSupplyEntryrelationsipObservation(observation,
                KantaCDAConstants.Laakityslista.OSAPAKKAUS, to);

        assertTrue("Osapakkaus tiedon on oltava true", to.isOsapakkaus());
    }

    @Test
    public void puraAsiakirjakohtainenSupplyEntryrelationsipObservationTestFalse() throws ConfigurationException {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        BL value = of.createBL();
        value.setValue(false);
        observation.getValues().add(value);
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();
        purkaja.puraAsiakirjakohtainenSupplyEntryrelationsipObservation(observation,
                KantaCDAConstants.Laakityslista.OSAPAKKAUS, to);

        assertFalse("Osapakkaus tiedon on oltava false", to.isOsapakkaus());
    }

    @Test
    public void puraAsiakirjakohtainenSupplyEntryrelationsipObservationTest_NotOsapakkaus()
            throws ConfigurationException {
        POCDMT000040Observation observation = of.createPOCDMT000040Observation();
        ReseptinToimituksenPurkaja purkaja = new ReseptinToimituksenPurkaja();
        LaakemaarayksenToimitusTO to = new LaakemaarayksenToimitusTO();
        purkaja.puraAsiakirjakohtainenSupplyEntryrelationsipObservation(observation,
                KantaCDAConstants.Laakityslista.LAAKE_TAUOLLA, to);

        assertFalse("Osapakkaus tiedon on oltava false", to.isOsapakkaus());
    }
}
