package fi.kela.kanta.cda.validation;

import java.util.Arrays;

import org.junit.Test;

import fi.kela.kanta.cda.KantaCDATestUtils;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OrganisaatioTO;

public class ValidoijaTest {

	private static KantaCDATestUtils utils;
	
	public class ValidoijaImpl extends Validoija {
		public ValidoijaImpl() {}

		@Override
		public void validoi() {}
	}
	
	@Test
    public void testValidoiAmmattihenkilo() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }
	
	@Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidAmmattihenkilo() {
	AmmattihenkiloTO ammattihenkiloTO = null;
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidRooli() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setRooli("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidSvNumero() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setSvNumero(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidRekisterointinumero() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setRekisterointinumero("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidAmmattioikeus() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setAmmattioikeus(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidKokoNimi() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setKokonimi(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiAmmattihenkilo_invalidNimi() {
	AmmattihenkiloTO ammattihenkiloTO = ValidoijaTest.utils.luoAmmattihenkilo();
	ammattihenkiloTO.setKokonimi(new KokoNimiTO());
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiAmmattihenkilo(ammattihenkiloTO);
    }


    @Test
    public void testValidoiOrganisaatio() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test
    public void testValidoiOrganisaatio_ehdollisestiPakollinen() {
	OrganisaatioTO organisaatioTO = null;
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, true, true);
    }


    @Test
    public void testValidoiOrganisaatio_PuhelinNotRequred() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.setPuhelinnumero("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, false);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidOrganisaatio() {
	OrganisaatioTO organisaatioTO = null;
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidYksilointitunnus() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.setYksilointitunnus("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidNimi() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.setNimi(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidPuhelin() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.setPuhelinnumero("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidOsoite() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.setOsoite(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidKatuosoite() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.getOsoite().setKatuosoite("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidPostinumero() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.getOsoite().setPostinumero(null);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiOrganisaatio_invalidPostitoimipaikka() {
	OrganisaatioTO organisaatioTO = ValidoijaTest.utils.luoOrganisaatio();
	organisaatioTO.getOsoite().setPostitoimipaikka("");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiOrganisaatio(organisaatioTO, false, true);
    }


    @Test
    public void testValidoiHenkilotiedot() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi), KantaCDATestUtils.testHetu);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test
    public void testValidoiHenkilotiedot_useampinimi() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi, Arrays.asList("EtuNimi2", "EtuNimi3", "EtuNimi4")), KantaCDATestUtils.testHetu);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidHenkilotiedot() {
	HenkilotiedotTO henkilotiedotTO = null;
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidHentu() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi), "");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidSynytmaaika() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi), null, 1);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidSukupuoli() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi), "20151011");
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidKokoNimi() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(null, KantaCDATestUtils.testHetu);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testValidoiHenkilotiedot_invalidNimi() {
	HenkilotiedotTO henkilotiedotTO = new HenkilotiedotTO(new KokoNimiTO(), KantaCDATestUtils.testHetu);
	ValidoijaImpl tested = new ValidoijaImpl();
	tested.validoiHenkilotiedot(henkilotiedotTO);
    }

}
