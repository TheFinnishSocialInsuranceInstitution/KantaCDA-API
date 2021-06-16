package fi.kela.kanta.cda.validation;

import java.util.Date;

import org.junit.Test;

import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class ReseptinUusimisValidoijaTest extends LMTOKasaaja {

    private static final String testOid = "1.2.3456";


	/**
     * Testaa että uusittavan lääkemääräyksen tiedoissa on cda:n muodostuksen kannalta oleellinen tieto tarkistetaan.
     *
     * @throws Exception
     */
    @Test
    public void testValidoiUusittava() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setMaarayspaiva(new Date());
    	laakemaaraysTO.setPotilas(luoPotilas());
    	laakemaaraysTO.setOid(testOid);
    	ReseptinUusimisValidoija tested = new ReseptinUusimisValidoija(null, laakemaaraysTO);
    	tested.validoiLaakemaaraysUusiminen();
    }


    /**
     * Tarkistaa että uusittava lääkemääräys vaaditaan.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiUusiminen_nullUusittava() throws Exception {
    	ReseptinUusimisValidoija tested = new ReseptinUusimisValidoija(null, new LaakemaaraysTO());
    	tested.validoiLaakemaaraysUusiminen();
    }


    /**
     * Tarkistaa että alkuperäinen lääkemääräys vaaditaan.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidoiUusiminen_nullAlkuperainen() throws Exception {
    	LaakemaaraysTO uusittavaLM = new LaakemaaraysTO();
    	uusittavaLM.setMaarayspaiva(new Date());
    	uusittavaLM.setPotilas(luoPotilas());
    	ReseptinUusimisValidoija tested = new ReseptinUusimisValidoija(null, uusittavaLM);
    	tested.validoiLaakemaaraysUusiminen();
    }


	@Override
	protected void setupProperties() {
		// TODO Auto-generated method stub
		
	}
}
