package fi.kela.kanta.cda.validation;

import java.util.Date;

import org.junit.Test;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.LaakemaarayksenMitatointiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinMitatointiValidoijaTest extends LMTOKasaaja  {

	@Test
    public void testValidoi() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
    	laakemaarayksenMitatointiTO.setPotilas(luoPotilas());
    	LaakemaaraysTO laakemaarays = new LaakemaaraysTO();
    	laakemaarays.setMaarayspaiva(new Date());
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(laakemaarays, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }
	
    @Test
    public void testValidoiMitatointi() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
    	laakemaarayksenMitatointiTO.setPotilas(luoPotilas());
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }

    @Test
    public void testValidoiMitatointi_perustelulla() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
    	laakemaarayksenMitatointiTO.setMitatoinninPerustelu("Mitätöinnin Perustelu");
    	laakemaarayksenMitatointiTO.setPotilas(luoPotilas());
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidoiMitatointi_muusyy_ilman_perustelua() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("5");
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidoiMitatointi_nullMitatointi() throws Exception {
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, null);
    	tested.validoiMitatointi();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidoiMitatointi_nullMitatoija() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidoiMitatointi_nullSyyKoodi() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }

    @Test
    public void testValidoiMitatointi_nullPerustelu() throws Exception {
    	LaakemaarayksenMitatointiTO laakemaarayksenMitatointiTO = new LaakemaarayksenMitatointiTO();
    	laakemaarayksenMitatointiTO.setMitatoija(new AmmattihenkiloTO());
    	laakemaarayksenMitatointiTO.setMitatoinninSyyKoodi("1");
    	laakemaarayksenMitatointiTO.setPotilas(luoPotilas());
    	ReseptinMitatointiValidoija tested = new ReseptinMitatointiValidoija(null, laakemaarayksenMitatointiTO);
    	tested.validoiMitatointi();
    }


	@Override
	protected void setupProperties() {
		// DONE
		
	}
}
