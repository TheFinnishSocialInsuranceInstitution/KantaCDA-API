package fi.kela.kanta.cda.validation;

import org.junit.Test;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaarayksenLukituksenPurkuTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinLukituksenPurkuValidoijaTest extends LMTOKasaaja {

	private static final String testOid = "1.2.3.4.5";
	private static final String testSetId = "1.12.123.1234.12345";
	
	private LaakemaarayksenLukituksenPurkuTO luoLaakemaarayksenLukituksenPurkuTO() {
		LaakemaarayksenLukituksenPurkuTO purku = new LaakemaarayksenLukituksenPurkuTO();
		purku.setPurkaja(new AmmattihenkiloTO());
		purku.getPurkaja().setOrganisaatio(new OrganisaatioTO());
		purku.setLukitussanomanOid("lukitussanomanOid");
		purku.setLukitussanomanSetId("lukitussanomanSetId");
		purku.setSelitys("selitys");
		return purku;
	    }
	
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullLukituksenPurkuTO() throws Exception {
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, null);
    	tested.validoiLukituksenPurku();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullPurkaja() throws Exception {
		LaakemaarayksenLukituksenPurkuTO purku = luoLaakemaarayksenLukituksenPurkuTO();
		purku.setPurkaja(null);
		ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(purku, null);
		tested.validoiLukituksenPurku();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullPurkajaOrganisaatio() throws Exception {
    	LaakemaarayksenLukituksenPurkuTO purku = luoLaakemaarayksenLukituksenPurkuTO();
    	purku.getPurkaja().setOrganisaatio(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(purku, null);
		tested.validoiLukituksenPurku();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullLukitussanomaOid() throws Exception {
    	LaakemaarayksenLukituksenPurkuTO purku = luoLaakemaarayksenLukituksenPurkuTO();
    	purku.setLukitussanomanOid(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(purku, null);
		tested.validoiLukituksenPurku();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullLukitussanomaSetId() throws Exception {
    	LaakemaarayksenLukituksenPurkuTO purku = luoLaakemaarayksenLukituksenPurkuTO();
    	purku.setLukitussanomanSetId(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(purku, null);
		tested.validoiLukituksenPurku();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiLukituksenPurku_nullSelitys() throws Exception {
    	LaakemaarayksenLukituksenPurkuTO purku = luoLaakemaarayksenLukituksenPurkuTO();
    	purku.setSelitys(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(purku, null);
		tested.validoiLukituksenPurku();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_nullAlkuperainenLaakemaarays() throws Exception {
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, null);
		tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_nullPotilas() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setPotilas(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidPotilasHetu() throws Exception {
        LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
        alkuperainenLaakemaarays.getPotilas().setHetu(null);
        ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidPotilasNimi() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.getPotilas().setNimi(new KokoNimiTO());
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidPotilasSukupuoli() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.getPotilas().setSukupuoli(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidOid() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid("");
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidSetId() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid(testOid);
    	alkuperainenLaakemaarays.setSetId("");
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_nullAmmattihenkilo() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid(testOid);
    	alkuperainenLaakemaarays.setSetId(testSetId);
    	alkuperainenLaakemaarays.setAmmattihenkilo(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_nullAmmattihenkilonNimi() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid(testOid);
    	alkuperainenLaakemaarays.setSetId(testSetId);
    	alkuperainenLaakemaarays.getAmmattihenkilo().setKokonimi(null);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAlkuperainenLaakemaarays_invalidAmmattihenkilonNimi() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid(testOid);
    	alkuperainenLaakemaarays.setSetId(testSetId);
    	alkuperainenLaakemaarays.getAmmattihenkilo().setKokonimi(new KokoNimiTO());
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }
    
    @Test
    public void testValidoiAlkuperainenLaakemaarays() throws Exception {
    	LaakemaaraysTO alkuperainenLaakemaarays = luoLaakemaarays();
    	alkuperainenLaakemaarays.setOid(testOid);
    	alkuperainenLaakemaarays.setSetId(testSetId);
    	ReseptinLukituksenPurkuValidoija tested = new ReseptinLukituksenPurkuValidoija(null, alkuperainenLaakemaarays);
    	tested.validoiAlkuperainenLaakemaarays();
    }

	@Override
	protected void setupProperties() {
		// DONE
	}

}
