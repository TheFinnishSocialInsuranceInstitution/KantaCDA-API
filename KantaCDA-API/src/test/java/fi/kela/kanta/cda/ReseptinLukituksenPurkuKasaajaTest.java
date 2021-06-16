package fi.kela.kanta.cda;

import javax.xml.bind.JAXBException;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.XActRelationshipDocument;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import fi.kela.kanta.cda.KantaCDAConstants.ReseptisanomanTyyppi;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.LaakemaarayksenLukituksenPurkuTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinLukituksenPurkuKasaajaTest extends LMTOKasaaja {

    private static final String testOid = "1.2.3.4.5";
    private static final String testSetId = "1.12.123.1234.12345";
    private static final String testSelitys = "SELITYS";
    private static final String testEffectiveTimeValue = "20150915094835";

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }

    private LaakemaarayksenLukituksenPurkuTO luoLaakemaarayksenLukituksenPurkuTO() {
        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();
        purku.setPurkaja(new AmmattihenkiloTO());
        purku.getPurkaja().setOrganisaatio(new OrganisaatioTO());
        purku.setLukitussanomanOid("lukitussanomanOid");
        purku.setLukitussanomanSetId("lukitussanomanSetId");
        purku.setSelitys("selitys");
        return purku;
    }

    private ReseptinLukituksenPurkuKasaaja setupReseptinLukituksenPurkuKasaaja(
            LaakemaarayksenLukituksenPurkuTO lukituksenPurku, LaakemaaraysTO alkuperainenLaakemaarays) {
        KantaCDATestUtils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        // Tarvitsee laittaa kaksi kertaa. En tiedä miksi
        // LMTOKasaaja.utils.mockProperty(Kasaaja.BASE_OID_PROPERTY, KantaCDATestUtils.testBaseId);
        return new ReseptinLukituksenPurkuKasaaja(KantaCDATestUtils.getProperties(), lukituksenPurku,
                alkuperainenLaakemaarays);
    }

    @Test
    public void testKasaaReseptinLukituksenPurku() throws JAXBException {

        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        laakemaarays.setOid(testOid);
        laakemaarays.setSetId(testSetId);
        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();
        purku.setLukitussanomanOid(ReseptinLukituksenPurkuKasaajaTest.testOid);
        purku.setLukitussanomanSetId(ReseptinLukituksenPurkuKasaajaTest.testSetId);
        purku.setSelitys(ReseptinLukituksenPurkuKasaajaTest.testSelitys);
        purku.setPurkaja(laakemaarays.getAmmattihenkilo());
        // purku.setOrgRootOid("537");
        // purku.setOrgYTunnus("1234567-8");
        ReseptinLukituksenPurkuKasaaja tested = setupReseptinLukituksenPurkuKasaaja(purku, laakemaarays);
        String cda = tested.kasaaReseptiAsiakirja();
        Assert.assertNotNull("cda ei voi olla null", cda);
        // System.out.println(cda);
    }

    @Test
    public void testLuoLukituksenPurunSelitys() throws Exception {

        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();
        purku.setSelitys(ReseptinLukituksenPurkuKasaajaTest.testSelitys);
        // purku.setOrgRootOid("537");
        // purku.setOrgYTunnus("1234567-8");
        ReseptinLukituksenPurkuKasaaja tested = setupReseptinLukituksenPurkuKasaaja(purku, null);
        POCDMT000040Component5 component = Whitebox.invokeMethod(tested, "luoLukituksenPurunSelitys", purku,
                ReseptinLukituksenPurkuKasaajaTest.testEffectiveTimeValue);
        Assert.assertNotNull("palautettu component ei voi olla null", component);
        Assert.assertEquals("componentin sectionilla tulee olla 1 entry", 1,
                component.getSection().getEntries().size());
        Assert.assertEquals("entry/act/text tulee olla : " + ReseptinLukituksenPurkuKasaajaTest.testSelitys,
                ReseptinLukituksenPurkuKasaajaTest.testSelitys,
                component.getSection().getEntries().get(0).getAct().getText().getContent().get(0));
        Assert.assertEquals(
                "entry/act/effectiveTime tulee olla : " + ReseptinLukituksenPurkuKasaajaTest.testEffectiveTimeValue,
                ReseptinLukituksenPurkuKasaajaTest.testEffectiveTimeValue,
                component.getSection().getEntries().get(0).getAct().getEffectiveTime().getValue());
    }

    @Test
    public void testAddRelatedDocument() throws Exception {

        POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
        LaakemaaraysTO laakemaarays = luoLaakemaarays();
        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();
        purku.setLukitussanomanOid(ReseptinLukituksenPurkuKasaajaTest.testOid);
        purku.setLukitussanomanSetId(ReseptinLukituksenPurkuKasaajaTest.testSetId);
        ReseptinLukituksenPurkuKasaaja tested = setupReseptinLukituksenPurkuKasaaja(purku, laakemaarays);
        Whitebox.invokeMethod(tested, "addRelatedDocument", cda, laakemaarays, purku);

        Assert.assertEquals("clinicalDocument/relatedDocument elemettejä täytyy olla 2", 2,
                cda.getRelatedDocuments().size());
    }

    @Test
    public void testLuoLinkitys() throws Exception {
        ReseptinLukituksenPurkuKasaaja tested = setupReseptinLukituksenPurkuKasaaja(null, null);
        POCDMT000040RelatedDocument relatedDocument = Whitebox.invokeMethod(tested, "luoLinkitys",
                XActRelationshipDocument.APND, ReseptinLukituksenPurkuKasaajaTest.testOid,
                ReseptinLukituksenPurkuKasaajaTest.testSetId, ReseptisanomanTyyppi.LAAKEMAARAYS);
        Assert.assertNotNull("palautettu elementti ei voi olla null", relatedDocument);
        Assert.assertNotNull("relatedDocument/parentDocument ei voi olla null", relatedDocument.getParentDocument());
        Assert.assertEquals("parentDocument/id tulee olla : " + ReseptinLukituksenPurkuKasaajaTest.testOid,
                ReseptinLukituksenPurkuKasaajaTest.testOid,
                relatedDocument.getParentDocument().getIds().get(0).getRoot());
        Assert.assertEquals("parentDocument/code[@code] tulee olla : " + ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi(),
                String.valueOf(ReseptisanomanTyyppi.LAAKEMAARAYS.getTyyppi()),
                relatedDocument.getParentDocument().getCode().getCode());
        Assert.assertEquals("parentDocument/setId tulee olla : " + ReseptinLukituksenPurkuKasaajaTest.testSetId,
                ReseptinLukituksenPurkuKasaajaTest.testSetId, relatedDocument.getParentDocument().getSetId().getRoot());
    }
}
