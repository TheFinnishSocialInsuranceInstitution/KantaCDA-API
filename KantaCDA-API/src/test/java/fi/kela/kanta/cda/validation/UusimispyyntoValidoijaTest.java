package fi.kela.kanta.cda.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.UusimispyyntoTO;
import fi.kela.kanta.util.LMTOKasaaja;

public class UusimispyyntoValidoijaTest extends LMTOKasaaja{

	@Rule
    public ExpectedException exception = ExpectedException.none();
	
	private static final String testValmisteenNimi = "TESTVALMISTE";
	private static final String testEtunumi = "TESTETU";
	private static final String testSukunimi = "TESTSUKU";
	private static final String testPuhelinnumero = "123456678";
	private static final String testHetu = "123456-321O";
	private static final String testVastaanottajaId = "1.12.123.1234.12345";
	private static final String testVastaanottajaNimi = "Vastaanottaja.Nimi";
	private static final String testVastaanottajaKatu = "Vastaanottaja.Katu";
	private static final String testVastaanottajaPostinumero = "Vastaanottaja.Postinumero";
	private static final String testVastaanottajaKaupunki = "Vastaanottaja.Kaupunki";
	private static final String testVastaanottajaPuhelinnumero = "Vastaanottaja.Puhelinnumero";
	private static final String testMaaraajaEtunimi = "Määräjä.Etunimi";
	private static final String testMaaraajaSukunimi = "Määräjä.Sukunimi";
	private static final String testMaaraajaId = "1.11.111.1111.11111";
	private static final String testUusittavaLaakemaaraysOid = "1.23.456.7891.01112";
	private static final String testUusittavaLaakemaaraysSetId = "1.23.456.7891.011121";
	private final String testEffectiveTime = "20150828094530";
	
	private UusimispyyntoTO setupTestiUusimispyyntoTO() throws ParseException {
		UusimispyyntoTO to = new UusimispyyntoTO();
		to.setVastaanottajaId(testVastaanottajaId);
		to.setVastaanottajaNimi(testVastaanottajaNimi);
		to.setVastaanottajaKatu(testVastaanottajaKatu);
		to.setVastaanottajaPostinumero(testVastaanottajaPostinumero);
		to.setVastaanottajaKaupunki(testVastaanottajaKaupunki);
		to.setVastaanottajaPuhelinnumero(testVastaanottajaPuhelinnumero);
		
		to.setMaaraajanKokonimi(new KokoNimiTO(testMaaraajaEtunimi, testMaaraajaSukunimi));
		to.setMaaraajanId(testMaaraajaId);
		
		HenkilotiedotTO ht = new HenkilotiedotTO(new KokoNimiTO(testEtunumi, testSukunimi),testHetu);
		to.setHenkilotiedot(ht);
		to.setMatkapuhelinnumero(testPuhelinnumero);
		
		to.setUusittavaLaakemaaraysOid(testUusittavaLaakemaaraysOid);
		to.setUusittavaLaakemaaraysSetId(testUusittavaLaakemaaraysSetId);
		to.setValmisteenNimi(testValmisteenNimi);
		
		to.setAikaleima(new SimpleDateFormat("yyyyMMddhhmmss").parse(testEffectiveTime));
		
		//to.setOrgYTunnus(testOrgYTunnus);
		return to;
	}
	@Test
	public void testValidoiUusimispyynnonVastaanottaja() throws Exception {
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(setupTestiUusimispyyntoTO());
		tested.validoiUusimispyynnonVastaanottaja();
	}

	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidId() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan Id ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaId("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}
	
	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidNimi() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan nimi ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaNimi("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}
	
	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidKatu() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan katu ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaKatu("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}
	
	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidPostinumero() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan postinumero ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaPostinumero("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}

	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidKaupunki() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan kaupunki ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaKaupunki("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}
	
	@Test
	public void testValidoiUusimispyynnonVastaanottaja_invalidPuhelinnumero() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Vastaanottajan puhelinnumero ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setVastaanottajaPuhelinnumero("");
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonVastaanottaja();
	}
	
	@Test
	public void testValidoiUusimispyynnonMaaraja() throws Exception {
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(setupTestiUusimispyyntoTO());
		tested.validoiUusimispyynnonMaaraja();
	}

	@Test
	public void testValidoiUusimispyynnonMaaraja_invalidEtunimi() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Määrääjän etunimi ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setMaaraajanKokonimi(new KokoNimiTO(null, testMaaraajaSukunimi));
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonMaaraja();
	}
	
	@Test
	public void testValidoiUusimispyynnonMaaraja_invalidSukunimi() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Määrääjän sukunimi ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setMaaraajanKokonimi(new KokoNimiTO(testMaaraajaEtunimi, null));
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonMaaraja();
	}
	
	@Test
	public void testValidoiUusimispyynnonMaaraja_invalidId() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Määrääjän Id ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setMaaraajanId(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyynnonMaaraja();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO() throws Exception {
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(setupTestiUusimispyyntoTO());
		tested.validoiUusimispyyntoTO();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO_invalidUusimispyynto() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Uusimispyynto ei saa olla null.");
		
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(null);
		tested.validoiUusimispyyntoTO();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO_invalidHenkilotiedot() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Henkilotiedot cannot be null.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setHenkilotiedot(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyyntoTO();
	}

	@Test
	public void testValidoiUusimispyyntoTO_invalidHenkilotiedot_Sukupuoli_null() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Sukupuoli cannot be null.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setHenkilotiedot(luoPotilas());
		to.getHenkilotiedot().setSukupuoli(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyyntoTO();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO_invalidUusittavaLaakemaaraysOid() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Uusittava lääkemääräys oid ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setUusittavaLaakemaaraysOid(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyyntoTO();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO_invalidUusittavaLaakemaaraysSetId() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Uusittava lääkemääräys setId ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setUusittavaLaakemaaraysSetId(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyyntoTO();
	}
	
	@Test
	public void testValidoiUusimispyyntoTO_invalidValmisteenNimi() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Valmisteen nimi oid ei saa olla null eikä tyhjä.");
		
		UusimispyyntoTO to = setupTestiUusimispyyntoTO();
		to.setValmisteenNimi(null);
		UusimispyyntoValidoija tested = new UusimispyyntoValidoija(to);
		tested.validoiUusimispyyntoTO();
	}
	@Override
	protected void setupProperties() {
		// DONE

	}

}
