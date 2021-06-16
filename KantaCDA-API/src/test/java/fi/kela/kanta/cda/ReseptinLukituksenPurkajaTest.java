package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hl7.v3.ED;
import org.hl7.v3.II;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.POCDMT000040Act;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.POCDMT000040StructuredBody;
import org.hl7.v3.XActRelationshipDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.to.LaakemaarayksenLukitusTO;

public class ReseptinLukituksenPurkajaTest {
    
    private ObjectFactory of;

    @Before
    public void setup() {
        of = new ObjectFactory();
    }

    @Test
    public void testPuraRelatedDocument() throws Exception {
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

        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraRelatedDocument", cda, to);

        assertEquals("LaakemaarayksenYksilointitunnus tulee olla : " + testId, testId,
                to.getLaakemaarayksenYksilointitunnus());
    }

    @Test
    public void testPuraRelatedDocument_NoReleatedDocument() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraRelatedDocument", cda, to);

        assertNull("LaakemaarayksenYksilointitunnus tulee olla null", to.getLaakemaarayksenYksilointitunnus());
    }

    @Test
    public void testPuraLukituksenSelitys() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        createComponents(cda);
        POCDMT000040Component5 component5b = getComponent5b(cda);
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        POCDMT000040Act act = of.createPOCDMT000040Act();
        ED text = of.createED();
        text.getContent().add("selitys");
        component5b.getSection().getEntries().add(entry);
        entry.setAct(act);
        act.setText(text);
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraLukituksenSelitys", cda, to);
        Assert.assertNotNull(to.getSelitys());
        Assert.assertEquals("selitys", to.getSelitys());
    }

    @Test
    public void testPuraLukituksenSelitysEntryNull() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        createComponents(cda);
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraLukituksenSelitys", cda, to);
        Assert.assertNull(to.getSelitys());
    }

    @Test
    public void testPuraLukituksenSelitysActNull() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        createComponents(cda);
        POCDMT000040Component5 component5b = getComponent5b(cda);
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        component5b.getSection().getEntries().add(entry);
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraLukituksenSelitys", cda, to);
        Assert.assertNull(to.getSelitys());
    }

    @Test
    public void testPuraLukituksenSelitysTextNull() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        createComponents(cda);
        POCDMT000040Component5 component5b = getComponent5b(cda);
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        POCDMT000040Act act = of.createPOCDMT000040Act();
        component5b.getSection().getEntries().add(entry);
        entry.setAct(act);
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraLukituksenSelitys", cda, to);
        Assert.assertNull(to.getSelitys());
    }

    @Test
    public void testPuraLukituksenSelitysTextEmpty() throws Exception {
        POCDMT000040ClinicalDocument cda = of.createPOCDMT000040ClinicalDocument();
        createComponents(cda);
        POCDMT000040Component5 component5b = getComponent5b(cda);
        POCDMT000040Entry entry = of.createPOCDMT000040Entry();
        POCDMT000040Act act = of.createPOCDMT000040Act();
        ED text = of.createED();
        component5b.getSection().getEntries().add(entry);
        entry.setAct(act);
        act.setText(text);
        ReseptinLukituksenPurkaja purkaja = new ReseptinLukituksenPurkaja();
        LaakemaarayksenLukitusTO to = new LaakemaarayksenLukitusTO();

        Whitebox.invokeMethod(purkaja, "puraLukituksenSelitys", cda, to);
        Assert.assertNull(to.getSelitys());
    }

    private void createComponents(POCDMT000040ClinicalDocument cda) {
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
    }

    private POCDMT000040Component5 getComponent5b(POCDMT000040ClinicalDocument cda) {
        POCDMT000040StructuredBody body = cda.getComponent().getStructuredBody();
        POCDMT000040Component3 component3 = body.getComponents().get(0);
        POCDMT000040Component5 component5a = component3.getSection().getComponents().get(0);
        POCDMT000040Component5 component5b = component5a.getSection().getComponents().get(0);
        return component5b;
    }
}
