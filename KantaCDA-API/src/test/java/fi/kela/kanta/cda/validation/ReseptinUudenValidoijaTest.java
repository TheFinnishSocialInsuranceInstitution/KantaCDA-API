package fi.kela.kanta.cda.validation;

import java.util.Date;
import org.junit.Test;

import fi.kela.kanta.to.KoodiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinUudenValidoijaTest extends LMTOKasaaja {

	

    @Test
    public void testValidoiLaakemaarays() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }
    
    @Test
    public void testValidoiLaakemaarays_ATCKoodi_null() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setATCkoodi(null);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }
    
    @Test
    public void testValidoiLaakemaarays_kokonaismaaralla() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setReseptintyyppi("2");
    	laakemaaraysTO.setLaakkeenKokonaismaaraUnit("kg");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }

	@Test
	public void testValidoiLaakemaarays_ajalle() throws Exception {
		LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
		laakemaaraysTO.setReseptintyyppi("3");
		laakemaaraysTO.setAjalleMaaratynReseptinAikamaaraUnit("a");
		laakemaaraysTO.setAjalleMaaratynReseptinAlkuaika(new Date());
		ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
		tested.validoi();
	}

	@Test
	public void testValidoiLaakemaarays_kauppanimella() throws Exception {
		LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
		laakemaaraysTO.setReseptintyyppi("reseptintyyppi");
		laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
		laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus("test");
		laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(LMTOKasaaja.testKauppanimi);
		ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
		tested.validoi();
	}

	@Test
	public void testValidoiLaakemaarays_uudistamiskiellolla() throws Exception {
		LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
		laakemaaraysTO.setUudistamiskielto(true);
		laakemaaraysTO.setUusimiskiellonSyy("8");
		laakemaaraysTO.setUusimiskiellonPerustelu("Potilas pakotti");
		ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
		tested.validoi();
	    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_null() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = null;
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidReseptinTyyppi() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setReseptintyyppi("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidValmiste() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setValmiste(null);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidValmisteYksilointitiedot() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().setYksilointitiedot(null);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidValmisteenLaji() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidLaaketietokannanVersio() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setLaaketietokannanVersio("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidPakkauskokoteksti() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setPakkauskokoteksti("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidLaakkeenkokonaismaaraUnit() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setReseptintyyppi("2");
    	laakemaaraysTO.setLaakkeenKokonaismaaraUnit("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidAjallemaaratynAikamaaraUnit() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setReseptintyyppi("3");
    	laakemaaraysTO.setAjalleMaaratynReseptinAikamaaraUnit("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidAjallemaaratynAlkuaika() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setReseptintyyppi("3");
    	laakemaaraysTO.setAjalleMaaratynReseptinAikamaaraUnit("a");
    	laakemaaraysTO.setAjalleMaaratynReseptinAlkuaika(null);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidValmisteenYksilointitunnus() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus("");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidValmisteenKauppanimi() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setValmisteenLaji("1");
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setYksilointitunnus("1234");
    	laakemaaraysTO.getValmiste().getYksilointitiedot().setKauppanimi(null);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidHoitolaji() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.getHoitolajit().clear();
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidUudistamiskiellonSyy() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setUudistamiskielto(true);
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_invalidUudistamiskiellonPerustelu() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setUudistamiskielto(true);
    	laakemaaraysTO.setUusimiskiellonSyy("5");
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }
    
    @Test
    public void testValidoi_UudistamiskiellonSyyMuuJaPerustelu() {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaarays();
    	laakemaaraysTO.setUudistamiskielto(true);
    	laakemaaraysTO.setUusimiskiellonSyy("5");
    	laakemaaraysTO.setUusimiskiellonPerustelu("uusimiskiellonPerustelu");
    	
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoi_FysikaalinenAnnos() {
    	LaakemaaraysTO laakemaaraysTO = luoLaakemaaraysRakenteinenAnnostusFysikaalinenAnnos();
    	// asetetaan fysikaalisen annoksen lisäksi annos
		laakemaaraysTO.getAnnokset().get(0).setHighAnnos(2.5);
		KoodiTO yksikko = new KoodiTO("1", null);
		laakemaaraysTO.getAnnokset().get(0).setAnnosyksikko(yksikko);;
   	
    	ReseptinUudenValidoija tested = new ReseptinUudenValidoija(laakemaaraysTO);
    	tested.validoi();
    }


	@Override
	protected void setupProperties() {
		// TODO Auto-generated method stub
		
	}

}
