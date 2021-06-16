package fi.kela.kanta.cda;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040InfrastructureRootTemplateId;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fi.kela.kanta.to.LaakemaarayksenLukituksenPurkuTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;

/**
 * Testiluokka reseptin lukituksen purku kasaajan cdan muodostamisen testaamiseen
 * 
 */
public class ReseptinLukituksenPurkuKasaajaCDATest {

    private static final String testSelitys = "TESTI SELITYS";
    private static final String testLukitusOid = "1.3.5.678.91011";
    private static final String testLukitusSetId = "1.3.5.678.91012";
    private static final String testAlkupOid = "1.12.123.1234.12345.123456";
    private static final String testAlkupSetId = "1.12.123.1234.12345.1234567";

    private ReseptinLukituksenPurkuKasaaja setupReseptinLukituksenPurkuKasaaja(
            LaakemaarayksenLukituksenPurkuTO lukituksenPurku, LaakemaaraysTO alkuperainenLaakemaarays)
            throws IOException {
        return new ReseptinLukituksenPurkuKasaaja(KantaCDATestUtils.loadProperties("testi_properties.properties"),
                lukituksenPurku, alkuperainenLaakemaarays);
    }

    @Ignore
    @Test
    public void testReseptinLukituksenPurku() throws IOException, JAXBException {
        LaakemaaraysTO laakemaarays = new LaakemaaraysTO();
        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();

        taytaPakollisetTiedot(laakemaarays);
        taytaPakollisetTiedot(purku);
        laakemaarays.setOid(testAlkupOid);
        laakemaarays.setSetId(testAlkupSetId);
        ReseptinLukituksenPurkuKasaaja kasaaja = setupReseptinLukituksenPurkuKasaaja(purku, laakemaarays);
        String cda = kasaaja.kasaaReseptiAsiakirja();
        System.out.println(cda);
    }

    private void taytaPakollisetTiedot(LaakemaarayksenLukituksenPurkuTO purku) {
        purku.setPurkaja(KantaCDATestUtils.luoAmmattihenkilo());
        purku.getPurkaja().setRooli(null);
        purku.getPurkaja().setVirkanimike("Ylilääkäri");
        purku.getPurkaja().setOppiarvo("Lääketieteen lisensiaatti");
        purku.getPurkaja().setErikoisala("86173-680");
        purku.getPurkaja().setErikoisalaName("erikoislääkäri yleislääketiede");
        purku.setSelitys(testSelitys);
        purku.setLukitussanomanOid(testLukitusOid);
        purku.setLukitussanomanSetId(testLukitusSetId);
        purku.setLukitussanomanVersio(1);
    }

    private void taytaPakollisetTiedot(LaakemaaraysTO laakemaarays) {
        laakemaarays.setAmmattihenkilo(KantaCDATestUtils.luoAmmattihenkilo());
        laakemaarays.getAmmattihenkilo().setOrganisaatio(laakemaarays.getAmmattihenkilo().getOrganisaatio());
        laakemaarays.setPotilas(KantaCDATestUtils.luoPotilas());
        laakemaarays.setReseptintyyppi("1");
        ValmisteenYksilointitiedotTO yksilointitiedot = new ValmisteenYksilointitiedotTO();
        yksilointitiedot.setValmisteenLaji("valmisteenLaji");
        yksilointitiedot.setPakkauskokoteksti("pakkauskokoteksti");
        ValmisteTO valmiste = new ValmisteTO();
        valmiste.setYksilointitiedot(yksilointitiedot);
        laakemaarays.setValmiste(valmiste);
        laakemaarays.getHoitolajit().add("3");
    }

    @Test
    public void testReseptiLukituksenPurkuTemplateId() throws IOException {
        LaakemaaraysTO laakemaarays = new LaakemaaraysTO();
        LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();

        taytaPakollisetTiedot(laakemaarays);
        taytaPakollisetTiedot(purku);
        laakemaarays.setOid(testAlkupOid);
        laakemaarays.setSetId(testAlkupSetId);
        purku.getTemplateIds().add("1.2.246.777.11.2015.11");
        purku.getTemplateIds().add("1.2.246.777.11.2014.17");
        ReseptinLukituksenPurkuKasaaja kasaaja = setupReseptinLukituksenPurkuKasaaja(purku, laakemaarays);
        POCDMT000040ClinicalDocument cda = kasaaja.kasaaReseptiCDA();
        ArrayList<String> cdaTemplateIds = new ArrayList<String>();
        for (POCDMT000040InfrastructureRootTemplateId templateId : cda.getTemplateIds()) {
            cdaTemplateIds.add(templateId.getRoot());
            Assert.assertTrue(purku.getTemplateIds().contains(templateId.getRoot()));
        }
        Assert.assertTrue(cdaTemplateIds.contains("1.2.246.777.11.2015.11"));
        Assert.assertTrue(cdaTemplateIds.contains("1.2.246.777.11.2014.17"));
    }
}
