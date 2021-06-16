package fi.kela.kanta.cda.validation;

import java.util.Date;

import org.junit.Test;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.LaakemaarayksenKorjausTO;
import fi.kela.kanta.to.LaakemaaraysTO;

public class ReseptinKorjausValidoijaTest {

	
    private static final String testAlkuperainenOid = "1.2.3.4.5";
	private static final String testAlkuperainenSetId = "1.2.3.4.5.6";
	private static final int testAlkuperainenVersio = 3;
	private static final int testAlkuperainenCdaTyyppi = 1;

	@Test
    public void testValidoi() throws Exception {
    	LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setOid(testAlkuperainenOid);
    	laakemaaraysTO.setSetId(testAlkuperainenSetId);
    	laakemaaraysTO.setVersio(testAlkuperainenVersio);
    	laakemaaraysTO.setCdaTyyppi(testAlkuperainenCdaTyyppi);
		laakemaaraysTO.setMaarayspaiva(new Date());
		LaakemaarayksenKorjausTO laakemaarayksenKorjausTO = new LaakemaarayksenKorjausTO();
		laakemaarayksenKorjausTO.setKorjaaja(new AmmattihenkiloTO());
		laakemaarayksenKorjausTO.setKorjauksenSyyKoodi("korjauksenSyyKoodi");
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(laakemaarayksenKorjausTO, laakemaaraysTO);
		tested.validoi();
    }
	
	@Test
	public void testValidoiKorjaus () {
		LaakemaarayksenKorjausTO laakemaarayksenKorjaus = new LaakemaarayksenKorjausTO();
		laakemaarayksenKorjaus.setKorjaaja(new AmmattihenkiloTO());
		laakemaarayksenKorjaus.setKorjauksenSyyKoodi("korjauksenSyyKoodi");
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(laakemaarayksenKorjaus, null);
		tested.validoiKorjaus();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiKorjaus_nullKorjaus () {
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, null);
		tested.validoiKorjaus();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiKorjaus_nullKorjaaja () {
		LaakemaarayksenKorjausTO laakemaarayksenKorjaus = new LaakemaarayksenKorjausTO();
		laakemaarayksenKorjaus.setKorjauksenSyyKoodi("korjauksenSyyKoodi");
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(laakemaarayksenKorjaus, null);
		tested.validoiKorjaus();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiKorjaus_tyhjaSyyKoodi () {
		LaakemaarayksenKorjausTO laakemaarayksenKorjaus = new LaakemaarayksenKorjausTO();
		laakemaarayksenKorjaus.setKorjaaja(new AmmattihenkiloTO());
		laakemaarayksenKorjaus.setKorjauksenSyyKoodi("");
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(laakemaarayksenKorjaus, null);
		tested.validoiKorjaus();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiKorjaus_nullKojruksenPerustelu () {
		LaakemaarayksenKorjausTO laakemaarayksenKorjaus = new LaakemaarayksenKorjausTO();
		laakemaarayksenKorjaus.setKorjaaja(new AmmattihenkiloTO());
		laakemaarayksenKorjaus.setKorjauksenSyyKoodi("5");
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(laakemaarayksenKorjaus, null);
		tested.validoiKorjaus();
	}
	
	@Test
	public void testValidoiAlkuperainenLaakemaarays() {
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setOid(testAlkuperainenOid);
    	laakemaaraysTO.setSetId(testAlkuperainenSetId);
    	laakemaaraysTO.setVersio(testAlkuperainenVersio);
    	laakemaaraysTO.setCdaTyyppi(testAlkuperainenCdaTyyppi);
		laakemaaraysTO.setMaarayspaiva(new Date());
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, laakemaaraysTO);
		tested.validoiAlkuperainenLaakemaarays();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiAlkuperainenLaakemaarays_nullAlkuperainenLaakemaarays() {
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, null);
		tested.validoiAlkuperainenLaakemaarays();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiAlkuperainenLaakemaarays_nullMaarayspaiva() {
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setOid(testAlkuperainenOid);
    	laakemaaraysTO.setSetId(testAlkuperainenSetId);
    	laakemaaraysTO.setVersio(testAlkuperainenVersio);
    	laakemaaraysTO.setCdaTyyppi(testAlkuperainenCdaTyyppi);
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, laakemaaraysTO);
		tested.validoiAlkuperainenLaakemaarays();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiAlkuperainenLaakemaarays_tyhjaOid() {
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setOid("");
    	laakemaaraysTO.setSetId(testAlkuperainenSetId);
    	laakemaaraysTO.setVersio(testAlkuperainenVersio);
    	laakemaaraysTO.setCdaTyyppi(testAlkuperainenCdaTyyppi);
		laakemaaraysTO.setMaarayspaiva(new Date());
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, laakemaaraysTO);
		tested.validoiAlkuperainenLaakemaarays();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testValidoiAlkuperainenLaakemaarays_emptySetId() {
		LaakemaaraysTO laakemaaraysTO = new LaakemaaraysTO();
    	laakemaaraysTO.setOid(testAlkuperainenOid);
    	laakemaaraysTO.setSetId("");
    	laakemaaraysTO.setVersio(testAlkuperainenVersio);
    	laakemaaraysTO.setCdaTyyppi(testAlkuperainenCdaTyyppi);
		laakemaaraysTO.setMaarayspaiva(new Date());
		ReseptinKorjausValidoija tested = new ReseptinKorjausValidoija(null, laakemaaraysTO);
		tested.validoiAlkuperainenLaakemaarays();
	}
}
