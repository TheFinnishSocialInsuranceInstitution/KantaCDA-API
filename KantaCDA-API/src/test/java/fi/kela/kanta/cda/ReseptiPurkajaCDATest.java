package fi.kela.kanta.cda;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fi.kela.kanta.interfaces.Osoite;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.VaikuttavaAinesosaTO;
import fi.kela.kanta.util.AsiakirjaVersioUtil;

public class ReseptiPurkajaCDATest {

    private static final String testCDAFilePrefix = "testCDA";
    private static final String testCDAExtension = ".xml";
    private static final String testCDAFileName = "testCDA.xml";
	private static final String pkvlaake_iteroitu_pienempi_pakkaus = "LM1_pkvlaake_iteroitu_pienempi_pakkaus.xml";
	private static final String yhdistelmavalmiste_maaratty_ajalle_annosjakelu = "LM2_yhdistelmavalmiste_maaratty_ajalle_annosjakelu.xml";
	private static final String maaraaikainen_erityislupavalmiste = "LM3_ maaraaikainen_erityislupavalmiste.xml";
	private static final String perusvoide_kokonaismaaralla = "LM4_perusvoide_kokonaismaaralla.xml";
    private static final String kliininen_ravintovalmiste_kertoimellinen_pakkauskoko = "LM5_kliininen_ravintovalmiste_kertoimellinen_pakkauskoko.xml";
    private static final String vaikuttavalla_aineella_sic = "LM6_vaikuttavalla_aineella_sic.xml";
	private static final String hoitotarvike_tyotapaturma = "LM7_hoitotarvike_tyotapaturma.xml";
    private static final String ex_tempore_rakenteinen_koodattu_ainesosa = "LM8_ex_tempore_rakenteinen_koodattu_ainesosa.xml";
    private static final String ex_tempore_rakenteinen_koodaamaton_ainesosa = "LM9_ex_tempore_rakenteinen_koodaamaton_ainesosa.xml";
    private static final String ex_tempore_rakenteinen_koodattu_tehdasvalmisteesta = "LM10_ex_tempore_rakenteinen_koodattu_tehdasvalmisteesta.xml";
    private static final String ex_tempore_tekstimuodossa = "LM11_ex_tempore_tekstimuodossa.xml";
    private static final String potilaskohtainen_erityislupavalmiste = "LM12_potilaskohtainen_erityislupavalmiste.xml";
	private static final String laaketietokannan_ulkopuolinen_valmiste = "LM13_laaketietokannan_ulkopuolinen_valmiste.xml";
    private static final String mitatoitavaksi_lm__testi_hallintakalista = "LM_testiasiakirja_mitatointiin.xml";
	private static final String perusvoide_kokonaismaaralla_ei_nayttomuotoa = "LM4_perusvoide_kokonaismaaralla_ei_nayttomuotoa.xml";
    private static final String apteekissa_tallennettu_laakemaarays_puhelin = "apteekissa_tallennettu_laakemaarays_puhelin.xml";
    private static final String v4_0_kaikki_tiedot = "ESIM lääkemääräys_laaja_kaikki_tietokentat.xml";
   

    private String lataa(String fileName) throws IOException {
        if ( null == fileName || fileName.isEmpty() ) {
            fileName = ReseptiPurkajaCDATest.testCDAFileName;
        }
        String retval = null;
        URL resourceStream = getClass().getResource(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream.openStream(), "UTF-8"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            retval = sb.toString();
        }
        finally {
            br.close();
        }
        return retval;
    }

    private String getCDAResource(String cdaFile) {
        Assert.assertNotNull("Test file missing", getClass().getResource("/" + cdaFile));
        return "/" + cdaFile;
    }

    private List<String> findTestFiles() {
        List<String> files = new ArrayList<String>();
        for (int i = 0; i < 150; i++) {
            if ( null != getClass().getResource(
                    "/" + ReseptiPurkajaCDATest.testCDAFilePrefix + i + ReseptiPurkajaCDATest.testCDAExtension) ) {
                files.add(ReseptiPurkajaCDATest.testCDAFilePrefix + i + ReseptiPurkajaCDATest.testCDAExtension);
            }
        }
        return files;
    }

    @Test
    public void testPuraPKVLaake() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.pkvlaake_iteroitu_pienempi_pakkaus)));
        Assert.assertEquals("Z", laakemaarays.getPKVlaakemaarays());
        Assert.assertEquals(3, laakemaarays.getIterointienMaara().intValue());
        Assert.assertEquals("d", laakemaarays.getIterointienValiUnit());
        Assert.assertEquals(7, laakemaarays.getIterointienValiValue().intValue());
        Assert.assertEquals("10 mg", laakemaarays.getValmiste().getYksilointitiedot().getVahvuus());
        Assert.assertEquals("2", laakemaarays.getReseptintyyppi());
        Assert.assertEquals("fol", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals(21, laakemaarays.getLaakkeenKokonaismaaraValue().intValue());
    }

    @Test
    public void testPuraYhdistelmavalmiste_maaratty_ajalle_annosjakelu() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.yhdistelmavalmiste_maaratty_ajalle_annosjakelu)));
        Assert.assertEquals("3", laakemaarays.getReseptintyyppi());
        Assert.assertEquals("a", laakemaarays.getAjalleMaaratynReseptinAikamaaraUnit());
        Assert.assertEquals(1, laakemaarays.getAjalleMaaratynReseptinAikamaaraValue().intValue());
        Assert.assertNotNull(laakemaarays.getAjalleMaaratynReseptinAlkuaika());
        Assert.assertEquals(2, laakemaarays.getValmiste().getVaikuttavatAineet().size());
        Assert.assertTrue(laakemaarays.isAnnosjakelu());
    }

    @Test
    public void testPuraMaaraaikainen_erityislupavalmiste() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.maaraaikainen_erityislupavalmiste)));

        Assert.assertEquals(1, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals(100, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        Assert.assertEquals("kpl", laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals("DESITIN ARZNEIMITTEL GMBH",
                laakemaarays.getValmiste().getYksilointitiedot().getMyyntiluvanHaltija());
        Assert.assertEquals(1, laakemaarays.getValmiste().getKayttotavat().size());
        Assert.assertEquals("kapseli, pehmeä",
                laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(1, laakemaarays.getPakkauksienLukumaara().intValue());
        Assert.assertEquals("100 kpl", laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
        Assert.assertEquals(true, laakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Ohjeen mukaan.", laakemaarays.getAnnostusohje());
    }

    @Test
    public void testPuraPerusvoide_kokonaismaaralla() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.perusvoide_kokonaismaaralla)));
        Assert.assertEquals("2", laakemaarays.getReseptintyyppi());
        Assert.assertEquals(3000, laakemaarays.getLaakkeenKokonaismaaraValue().intValue());
        Assert.assertEquals("g", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("DECUBAL CLINIC CREME", laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi());
        Assert.assertEquals("2014.008", laakemaarays.getLaaketietokannanVersio());
        Assert.assertEquals("EMULS VOIDE",
                laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertTrue(laakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Käytetään säännöllisesti ihon rasvaukseen.", laakemaarays.getAnnostusohje());
        Assert.assertEquals("Pitkäaikaisen ihotaudin hoitoon.", laakemaarays.getKayttotarkoitusTeksti());
        Assert.assertTrue(laakemaarays.isPysyvaislaakitys());
    }

    @Test
    public void testPuraKliininen_ravintovalmiste_kertoimellinen_pakkauskoko() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.kliininen_ravintovalmiste_kertoimellinen_pakkauskoko)));
        Assert.assertNull(laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi());
        Assert.assertEquals("1", laakemaarays.getReseptintyyppi());
        Assert.assertEquals(2, laakemaarays.getPakkauksienLukumaara().intValue());
        Assert.assertEquals("RESOURCE PROTEIN KAAKAO",
                laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi());
        Assert.assertEquals(200, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        Assert.assertEquals("ml", laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals("täydennysravintovalmiste",
                laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(4, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals("4X200 ml", laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
    }

    @Test
    public void testPuraTestiasiakirjaMitatointiin() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.mitatoitavaksi_lm__testi_hallintakalista)));
        Assert.assertNotNull(laakemaarays);
        Assert.assertNotNull(laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi());
        Assert.assertEquals("RENITEC COMP", laakemaarays.getValmiste().getYksilointitiedot().getKauppanimi());

        // testataan palveluyksikön lukemisen onnistuminen
        // XXX voidaanko irrottaa palveluyksikkö ammattihenkilön organisaatiosta?
        /*
         * Assert.assertNotNull(laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko());
         * Assert.assertEquals("Kotkan kaupunki, Terveydenhuollon toimintayksikkö",
         * laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getNimi());
         * Assert.assertNotNull(laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getOsoite());
         * Assert.assertEquals("Haukkatie 2",
         * laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getOsoite().getKatuosoite());
         * Assert.assertEquals("50700",
         * laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getOsoite().getPostinumero());
         * Assert.assertEquals("Kotka",
         * laakemaarays.getAmmattihenkilo().getOrganisaatio().getPalveluYksikko().getOsoite().getPostitoimipaikka());
         */
        Assert.assertNotNull(laakemaarays.getLaatimispaikka());
        Assert.assertEquals("Kotkan terveyskeskus", laakemaarays.getLaatimispaikka().getNimi());
        Assert.assertNotNull(laakemaarays.getLaatimispaikka().getOsoite());
        Assert.assertEquals("Kotkantie 2", laakemaarays.getLaatimispaikka().getOsoite().getKatuosoite());
        Assert.assertEquals("50600", laakemaarays.getLaatimispaikka().getOsoite().getPostinumero());
        Assert.assertEquals("Kotka", laakemaarays.getLaatimispaikka().getOsoite().getPostitoimipaikka());
    }

    @Test
    public void testPuraVaikuttavalla_aineella_sic() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.vaikuttavalla_aineella_sic)));
        Assert.assertEquals("600 mg", laakemaarays.getValmiste().getYksilointitiedot().getVahvuus());
        Assert.assertEquals("M01AE01", laakemaarays.getValmiste().getYksilointitiedot().getATCkoodi());
        Assert.assertEquals("Ibuprofeeni", laakemaarays.getValmiste().getYksilointitiedot().getATCnimi());
        Assert.assertEquals("2014.008", laakemaarays.getLaaketietokannanVersio());
        Assert.assertEquals(2, laakemaarays.getPakkauksienLukumaara().intValue());
        Assert.assertEquals(100, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        Assert.assertEquals("fol", laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals("tabletti", laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(1, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals("100 fol", laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
        Assert.assertEquals("1 tabletti korkeintaan 6 kertaa vuorokaudessa.", laakemaarays.getAnnostusohje());
        Assert.assertTrue(laakemaarays.isSICmerkinta());
        Assert.assertTrue(laakemaarays.isKyseessaLaakkeenkaytonAloitus());

    }

    @Test
    public void testPuraHoitotarvike_tyotapaturma() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.hoitotarvike_tyotapaturma)));
        Assert.assertEquals("DUODERM EXTRA THIN 10X10 CM", laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("2", laakemaarays.getReseptintyyppi());
        Assert.assertEquals(10, laakemaarays.getLaakkeenKokonaismaaraValue().intValue());
        Assert.assertEquals("kpl", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertEquals("Raksa Oy", laakemaarays.getTyonantaja());
        Assert.assertEquals("Vakuus Oy", laakemaarays.getVakuutuslaitos());
        Assert.assertEquals("Ohjeen mukaan.", laakemaarays.getAnnostusohje());
        Assert.assertEquals("Haavan hoitoon.", laakemaarays.getKayttotarkoitusTeksti());
        Assert.assertEquals("T", laakemaarays.getHoitolajit().iterator().next());
        Assert.assertEquals("1", laakemaarays.getReseptinLaji());

    }

    @Test
    public void testPuraEx_tempore_rakenteinen_koodattu_ainesosa() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodattu_ainesosa)));
        Assert.assertEquals("M.d.s.", laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
        Assert.assertEquals("Oksikodonilius", laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("2", laakemaarays.getReseptintyyppi());
        Assert.assertEquals(500, laakemaarays.getLaakkeenKokonaismaaraValue(), 0);
        Assert.assertEquals("ml", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertTrue(laakemaarays.isApteekissaValmistettavaLaake());
        Assert.assertEquals("7", laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji());
        Assert.assertEquals("Apteekissa valmistettava lääke",
                laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLajiNimi());
        VaikuttavaAinesosaTO va = laakemaarays.getApteekissaValmistettavaLaake().getVaikuttavatAinesosat().iterator()
                .next();
        Assert.assertEquals(3, va.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("mg", va.getAinesosanMaaraUnit());
        Assert.assertEquals("N02AA05", va.getATCkoodi());
        Assert.assertEquals("Oksikodoni", va.getATCNimi());
        Assert.assertEquals("Oksikodonihydrokloridi", va.getKoodamatonNimi());
        MuuAinesosaTO ma = laakemaarays.getApteekissaValmistettavaLaake().getMuutAinesosat().iterator().next();
        Assert.assertEquals("ad 1 ml", ma.getAinesosanMaaraTekstina());
        Assert.assertEquals("Aqua menth. piper. c. cons.", ma.getNimi());
        Assert.assertEquals("5 ml 6 kertaa vuorokaudessa.", laakemaarays.getAnnostusohje());
        Assert.assertTrue(laakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Kovaan kipuun.", laakemaarays.getKayttotarkoitusTeksti());
        Assert.assertTrue(laakemaarays.isHuume());
        Assert.assertEquals("9", laakemaarays.getPotilaanTunnistaminen());
        Assert.assertEquals("potilas tunnettu", laakemaarays.getPotilaanTunnistaminenTeksti());
    }

    @Test
    public void testPuraEx_tempore_rakenteinen_koodaamaton_ainesosa() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodaamaton_ainesosa)));
        Assert.assertEquals("M.d.s.", laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
        Assert.assertEquals("Syyläliuos", laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("2", laakemaarays.getReseptintyyppi());
        Assert.assertEquals(50, laakemaarays.getLaakkeenKokonaismaaraValue(), 0);
        Assert.assertEquals("ml", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertTrue(laakemaarays.isApteekissaValmistettavaLaake());
        Iterator<VaikuttavaAinesosaTO> itva = laakemaarays.getApteekissaValmistettavaLaake().getVaikuttavatAinesosat()
                .iterator();
        VaikuttavaAinesosaTO va1 = itva.next();
        VaikuttavaAinesosaTO va2 = itva.next();
        Assert.assertEquals(7.5, va1.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("g", va1.getAinesosanMaaraUnit());
        Assert.assertEquals("Acid.salic", va1.getKoodamatonNimi());
        Assert.assertEquals(7.5, va2.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("g", va2.getAinesosanMaaraUnit());
        Assert.assertEquals("Acid.lact.", va2.getKoodamatonNimi());
        Iterator<MuuAinesosaTO> itma = laakemaarays.getApteekissaValmistettavaLaake().getMuutAinesosat().iterator();
        MuuAinesosaTO ma1 = itma.next();
        MuuAinesosaTO ma2 = itma.next();
        Assert.assertEquals(15, ma1.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("g", ma1.getAinesosanMaaraUnit());
        Assert.assertEquals("Collodium", ma1.getNimi());
        Assert.assertEquals("q.s", ma2.getAinesosanMaaraTekstina());
        Assert.assertEquals("aether", ma2.getNimi());

    }

    @Test
    public void testPuraEx_tempore_rakenteinen_koodattu_tehdasvalmisteesta() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodattu_tehdasvalmisteesta)));
        Assert.assertEquals("M.f.ungt.d.s.", laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
        Assert.assertEquals("Voideseos", laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals(100, laakemaarays.getLaakkeenKokonaismaaraValue(), 0);
        Assert.assertEquals("g", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertTrue(laakemaarays.isApteekissaValmistettavaLaake());
        VaikuttavaAinesosaTO va = laakemaarays.getApteekissaValmistettavaLaake().getVaikuttavatAinesosat().iterator()
                .next();
        Assert.assertEquals(30, va.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("g", va.getAinesosanMaaraUnit());
        Assert.assertEquals("D07AC03", va.getATCkoodi());
        Assert.assertEquals("Desoksimetasoni", va.getATCNimi());
        Assert.assertEquals("Ibaril 0,25 % emulsiovoide", va.getKoodamatonNimi());
        MuuAinesosaTO ma = laakemaarays.getApteekissaValmistettavaLaake().getMuutAinesosat().iterator().next();
        Assert.assertEquals(70, ma.getAinesosanMaaraValue(), 0);
        Assert.assertEquals("g", ma.getAinesosanMaaraUnit());
        Assert.assertEquals("Novalan", ma.getNimi());
    }

    @Test
    public void testPuraEx_tempore_tekstimuodossa() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_tekstimuodossa)));
        Assert.assertEquals("Hydrocortison 1 g Sulfur medic. 2 g Novalan ad 100 g M.f.ungt.d.s.",
                laakemaarays.getApteekissaValmistettavaLaake().getValmistusohje());
        Assert.assertEquals("Voideseos", laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals(100, laakemaarays.getLaakkeenKokonaismaaraValue(), 0);
        Assert.assertEquals("g", laakemaarays.getLaakkeenKokonaismaaraUnit());
        Assert.assertTrue(laakemaarays.isApteekissaValmistettavaLaake());
        Assert.assertTrue(laakemaarays.isAnnosteluPelkastaanTekstimuodossa());
        Assert.assertEquals("Levitetään ohuelti ihottuma-alueelle 2 kertaa päivässä 2 viikon ajan.",
                laakemaarays.getAnnostusohje());
        Assert.assertEquals("Ihottuman hoitoon.", laakemaarays.getKayttotarkoitusTeksti());

    }

    @Test
    public void testPuraPotilaskohtainen_erityislupavalmiste() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.potilaskohtainen_erityislupavalmiste)));
        Assert.assertEquals("Morfin Special 0.4 mg/ml injektioneste, liuos",
                laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("0.4 mg/ml", laakemaarays.getValmiste().getYksilointitiedot().getVahvuus());
        Assert.assertEquals(1, laakemaarays.getPakkauksienLukumaara().intValue());
        Assert.assertEquals(2.5, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        Assert.assertEquals("ml", laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals("injektioneste, liuos",
                laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(10, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals("10X2,5 ml", laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
        Assert.assertTrue(laakemaarays.isHuume());
        Assert.assertEquals("9", laakemaarays.getPotilaanTunnistaminen());
        Assert.assertEquals("potilas tunnettu", laakemaarays.getPotilaanTunnistaminenTeksti());
    }

    @Test
    public void testPuraLaaketietokannan_ulkopuolinen_valmiste() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.laaketietokannan_ulkopuolinen_valmiste)));
        Assert.assertEquals("RELATABS +D3 10 mikrog purutabletti",
                laakemaarays.getLaaketietokannanUlkopuolinenValmiste());
        Assert.assertEquals("10 mikrog", laakemaarays.getValmiste().getYksilointitiedot().getVahvuus());
        Assert.assertEquals(1, laakemaarays.getPakkauksienLukumaara().intValue());
        Assert.assertEquals(60, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskoko(), 0);
        Assert.assertEquals("kpl", laakemaarays.getValmiste().getYksilointitiedot().getPakkausyksikko());
        Assert.assertEquals("purutabletti",
                laakemaarays.getValmiste().getKayttotavat().iterator().next().getLaakemuoto());
        Assert.assertEquals(1, laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokokerroin());
        Assert.assertEquals("60  kpl", laakemaarays.getValmiste().getYksilointitiedot().getPakkauskokoteksti());
        Assert.assertEquals("Yksi purutabletti kerran päivässä.", laakemaarays.getAnnostusohje());
        Assert.assertEquals("Turistiripulin ehkäisyyn.", laakemaarays.getKayttotarkoitusTeksti());
    }

    // Aputesti jolla voi testata yksittäisen cdan purkamista
    // Laita cdan sisältö tetsCDAFileName tidostoon
    @Ignore
    @Test
    public void testPura() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.testCDAFileName)));
        tarkistaLaakemaarays(laakemaarays);
    }

    // Apu testi jolla voi testata useiden reseptiasiakirjojen purkamista
    // Laita cda tiedostot test/resources kansioon nimettyinä testCDA[1-150].xml
    @Ignore
    @Test
    public void testPuraUseita() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));

        List<String> testFiles = findTestFiles();
        for (String filename : testFiles) {
            System.out.println("Testfile: " + filename);
            LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(lataa(getCDAResource(filename)));
            tarkistaLaakemaarays(laakemaarays);
        }
    }

    @Test
    public void testPuraNayttomuoto() throws IOException, Exception {
        ReseptiNayttomuodonPurkaja tested = new ReseptiNayttomuodonPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.perusvoide_kokonaismaaralla)));
        Assert.assertNotNull(laakemaarays.getNayttomuoto());
        Assert.assertTrue(laakemaarays.getNayttomuoto().size() > 0);
        Assert.assertEquals("1.2.246.777.11.2015.1", laakemaarays.getAsiakirjaVersio());
        Assert.assertTrue(laakemaarays.isAsiakirjaTaaksepainYhteensopiva());
    }

    @Test
    public void testPuraNayttomuotoEiNayttomuotoa() throws IOException, Exception {
        ReseptiNayttomuodonPurkaja tested = new ReseptiNayttomuodonPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.perusvoide_kokonaismaaralla_ei_nayttomuotoa)));
        Assert.assertNotNull(laakemaarays.getNayttomuoto());
        Assert.assertTrue(laakemaarays.getNayttomuoto().size() == 0);
        Assert.assertEquals("1.2.246.777.11.2015.1", laakemaarays.getAsiakirjaVersio());
        Assert.assertTrue(laakemaarays.isAsiakirjaTaaksepainYhteensopiva());
    }

    @Test
    public void testPuraNayttomuotoUseita() throws IOException, Exception {
        ReseptiNayttomuodonPurkaja tested = new ReseptiNayttomuodonPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.perusvoide_kokonaismaaralla)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodaamaton_ainesosa)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodattu_ainesosa)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.ex_tempore_rakenteinen_koodattu_tehdasvalmisteesta)));
        laakemaarays = tested.puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.hoitotarvike_tyotapaturma)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.kliininen_ravintovalmiste_kertoimellinen_pakkauskoko)));
        laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.laaketietokannan_ulkopuolinen_valmiste)));
        laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.maaraaikainen_erityislupavalmiste)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.mitatoitavaksi_lm__testi_hallintakalista)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.perusvoide_kokonaismaaralla_ei_nayttomuotoa)));
        laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.pkvlaake_iteroitu_pienempi_pakkaus)));
        laakemaarays = tested
                .puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.potilaskohtainen_erityislupavalmiste)));
        laakemaarays = tested.puraLaakemaarays(lataa(getCDAResource(ReseptiPurkajaCDATest.vaikuttavalla_aineella_sic)));
        laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.yhdistelmavalmiste_maaratty_ajalle_annosjakelu)));
    }

    private void tarkistaLaakemaarays(LaakemaaraysTO laakemaarays) {
        Assert.assertNotNull("laakemaarays ei voi olla null", laakemaarays);
        Assert.assertNotNull("Oid ei voi olla null", laakemaarays.getOid());
        Assert.assertNotNull("setId ei voi olla null", laakemaarays.getSetId());
        // Assert.assertNotNull("aikaleima ei voi olla null", laakemaarays.getAikaleima());
        Assert.assertNotNull("versio ei voi olla null", laakemaarays.getVersio());
        tarkistaAmmattihenkilo(laakemaarays.getAmmattihenkilo());
        Assert.assertNotNull("reseptisanoman tyyppi ei voi olla null", laakemaarays.getReseptintyyppi());
        Assert.assertNotNull("maaraysaika ei voi olla null", laakemaarays.getMaarayspaiva());
        tarkistaPotilas(laakemaarays.getPotilas());
        Assert.assertNotNull("", laakemaarays);

    }

    private void tarkistaPotilas(HenkilotiedotTO potilas) {
        Assert.assertNotNull("potilas ei voi olla null", potilas);
        // Assert.assertNotNull("potilaan hetu ei voi olla null",potilas.getHetu());
        Assert.assertNotNull("poitlaan nimi ei voi olla null", potilas.getNimi());
    }

    private void tarkistaAmmattihenkilo(AmmattihenkiloTO ammattihenkilo) {
        Assert.assertNotNull("ammattihenkilo ei voi olla null", ammattihenkilo);
        Assert.assertNotNull("kokonimi ei voi olla null", ammattihenkilo.getKokonimi());
        // Assert.assertFalse("svnumero ja rekisterointinumero ei voi olla null",null ==
        // ammattihenkilo.getRekisterointinumero() && null == ammattihenkilo.getSvNumero());
        // Assert.assertNotNull("rekisterointinumero ei voi olla null", ammattihenkilo.getRekisterointinumero());
        // Assert.assertNotNull("svnumero ei voi olla null", ammattihenkilo.getSvNumero());
        if ( null != ammattihenkilo.getErikoisala() ) {
            Assert.assertNotNull("erikoisalan nimi", ammattihenkilo.getErikoisalaName());
            // Assert.assertNotNull("ammattioikeus ei voi olla null", ammattihenkilo.getAmmattioikeus());
            // Assert.assertNotNull("ammattioikeus name ei voi olla null", ammattihenkilo.getAmmattioikeusName());
        }
        trkistaOrganisaatio(ammattihenkilo.getOrganisaatio());
    }

    private void trkistaOrganisaatio(OrganisaatioTO organisaatio) {
        Assert.assertNotNull("organisaatio ei voi olla null", organisaatio);
        Assert.assertNotNull("yksilointitunnus ei voi olla null", organisaatio.getYksilointitunnus());
        Assert.assertNotNull("nimi ei voi olla null", organisaatio.getNimi());
        Assert.assertNotNull("puhelin ei voi olla null", organisaatio.getPuhelinnumero());
        tarkistaOsoite(organisaatio.getOsoite());
    }

    private void tarkistaOsoite(Osoite osoite) {
        Assert.assertNotNull("osoite ei voi olla null", osoite);
    }

    @Test
    public void testPura_apteekissa_tallennettu_laakemaarays_puhelinresepti() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.apteekissa_tallennettu_laakemaarays_puhelin)));
        Assert.assertEquals("Apteekissa tallennettun laakemaarayksen koodin tulisi olla 2 (puhelinresepti)", "2",
                laakemaarays.getApteekissaTallennettuLaakemaarays());
        Assert.assertEquals("3", laakemaarays.getApteekissaTallennettuLaakemaaraysPerustelu());
        Assert.assertEquals("Muun syyn perusteluteksti", laakemaarays.getApteekissaTallennettuLaakemaaraysMuuSyy());
        Assert.assertNotNull("Apteekissa tallennettun laakemaarayksen kirjaaja puuuttuu", laakemaarays.getKirjaaja());
    }

    @Test
    public void testPura_laaja_kaikki_tiedot() throws IOException, Exception {
        ReseptiPurkaja tested = new ReseptiPurkaja();
        tested.setVersioUtil(new AsiakirjaVersioUtil(KantaCDATestUtils.loadProperties("testi_properties.properties")));
        LaakemaaraysTO laakemaarays = tested.puraLaakemaarays(
                lataa(getCDAResource(ReseptiPurkajaCDATest.v4_0_kaikki_tiedot)));
        assertNotNull(laakemaarays);
    }

}
