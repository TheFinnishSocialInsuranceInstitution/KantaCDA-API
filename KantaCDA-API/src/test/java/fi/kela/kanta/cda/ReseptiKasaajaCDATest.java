package fi.kela.kanta.cda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.hl7.v3.EN;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040ManufacturedProduct;
import org.hl7.v3.POCDMT000040Material;
import org.hl7.v3.POCDMT000040Section;
import org.junit.Ignore;
import org.junit.Test;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.ApteekissaValmistettavaLaakeTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.to.MuuAinesosaTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;
import fi.kela.kanta.to.VaikuttavaAineTO;
import fi.kela.kanta.to.VaikuttavaAinesosaTO;
import fi.kela.kanta.to.ValmisteTO;
import fi.kela.kanta.to.ValmisteenKayttotapaTO;
import fi.kela.kanta.to.ValmisteenMuutTiedotTO;
import fi.kela.kanta.to.ValmisteenYksilointitiedotTO;
import fi.kela.kanta.util.JaxbUtil;
import fi.kela.kanta.util.LMTOKasaaja;

/**
 * ReseptiKasaajaCDATest Testiluokka jonka avulla voidaan generoida lääkemääräyksen CDA Poista @Ignore tag kun halutaan
 * geneoida CDA CDA.xml tallennetaan target kansioon
 */
public class ReseptiKasaajaCDATest extends LMTOKasaaja {

    protected AmmattihenkiloTO teeAmmattihenkilo() {
        AmmattihenkiloTO to = new AmmattihenkiloTO();
        to.setAmmattioikeus("034");
        to.setAmmattioikeusName("laillistettu erikoislääkäri");
        to.setErikoisala("86173-680");
        to.setErikoisalaName("erikoislääkäri yleislääketiede");
        to.setKokonimi(new KokoNimiTO("TestiEtu", "SukuTesti", Arrays.asList("Mikael", "Eetvartti")));
        to.setOppiarvo("Lääketieteen lisensiaatti");
        to.setRekisterointinumero("01234567890");
        to.setRooli("LAL");
        to.setSvNumero("123455");
        to.setVirkanimike("Ylilääkäri");

        to.setOrganisaatio(teeOrganisaatio());
        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotLaaketietokannanUlkopuolinenValmiste() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        to.setVahvuus("10 mikrog");
        to.setPakkausyksikko("kpl");
        to.setPakkauskoko(60);
        to.setPakkauskokokerroin(1);
        to.setPakkauskokoteksti("60  kpl");
        to.setValmisteenLaji("6");
        to.setValmisteenLajiNimi("Lääketietokannan ulkopuolinen valmiste");

        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotVaikuttavallaAineella() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        to.setATCkoodi("M01AE01");
        to.setATCnimi("Ibuprofeeni");
        to.setVahvuus("600 mg");
        to.setPakkausyksikko("fol");
        to.setPakkauskoko(100);
        to.setPakkauskokokerroin(1);
        to.setPakkauskokoteksti("100 fol");
        to.setValmisteenLaji("9");
        to.setValmisteenLajiNimi("Vaikuttavan aineen nimellä määrätty lääke");

        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotKokonaismaaralla() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        // ei ATC koodia
        to.setKauppanimi("DECUBAL CLINIC CREME");
        to.setValmisteenLaji("2");
        to.setValmisteenLajiNimi("Lääketietokannassa oleva perusvoide");

        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotYhdValmAjalleAnnos() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        to.setVahvuus("20/12,5 mg");
        to.setATCkoodi("C09BA02");
        to.setATCnimi("Enalapriili ja diureetit");
        to.setKauppanimi("RENITEC COMP");
        to.setYksilointitunnus("446393");
        to.setMyyntiluvanHaltija("MSD");
        to.setValmisteenLaji("1");
        to.setValmisteenLajiNimi("Myyntiluvallinen lääkevalmiste");
        to.setSailytysastia("läpipainopakkaus");
        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotAptRakentKoodTehdas() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        to.setValmisteenLaji("7");
        // to.setValmisteenLajiNimi("Oksikodonilius");

        return to;
    }

    protected ValmisteenYksilointitiedotTO teeValmisteenYksilointitiedotPKV_iteroitu_pienempi_pakkaus() {
        ValmisteenYksilointitiedotTO to = new ValmisteenYksilointitiedotTO();
        to.setVahvuus("10 mg");
        to.setATCkoodi("N05BA01");
        to.setATCnimi("Diatsepaami");
        to.setYksilointitunnus("469452");
        to.setKauppanimi("DIAPAM");
        to.setMyyntiluvanHaltija("ORION OYJ");
        to.setSailytysastia("läpipainopakkaus");
        to.setValmisteenLaji("1");
        to.setValmisteenLajiNimi("Myyntiluvallinen lääkevalmiste");

        return to;
    }

    protected VaikuttavaAineTO teeVaikuttavaAine() {
        VaikuttavaAineTO to = new VaikuttavaAineTO();
        return to;
    }

    protected VaikuttavaAineTO teeVaikuttavaAine(String laakeaine) {
        VaikuttavaAineTO to = new VaikuttavaAineTO();
        to.setLaakeaine(laakeaine);
        return to;
    }

    protected VaikuttavaAinesosaTO teeVaikuttavaAinesosa(String ainesosanMaaraUnit, int ainesosanMaaraValue,
            String ATCkoodi, String ATCnimi, String kauppanimiVahvuusJaLaakemuoto) {
        VaikuttavaAinesosaTO to = new VaikuttavaAinesosaTO();
        to.setAinesosanMaaraUnit(ainesosanMaaraUnit);
        to.setAinesosanMaaraValue(ainesosanMaaraValue);
        to.setATCkoodi(ATCkoodi);
        to.setATCnimi(ATCnimi);
        to.setKauppanimiVahvuusJaLaakemuoto(kauppanimiVahvuusJaLaakemuoto);
        return to;
    }

    protected MuuAinesosaTO teeMuuAinesosa(String ainesosanMaaraUnit, int ainesosanMaaraValue, String nimi) {
        MuuAinesosaTO to = new MuuAinesosaTO();
        to.setAinesosanMaaraUnit(ainesosanMaaraUnit);
        to.setAinesosanMaaraValue(ainesosanMaaraValue);
        to.setNimi(nimi);
        return to;
    }

    protected Collection<? extends VaikuttavaAineTO> teeVaikuttavatAineetYhdValmAjalleAnnos() {
        Collection<VaikuttavaAineTO> vaikuttavatAineet = new ArrayList<VaikuttavaAineTO>();
        vaikuttavatAineet.add(teeVaikuttavaAine("enalapriilimaleaatti"));
        vaikuttavatAineet.add(teeVaikuttavaAine("hydroklooritiatsidi"));
        return vaikuttavatAineet;
    }

    protected ValmisteenKayttotapaTO teeValmisteenKayttotapaYhdValmAjalleAnnos() {
        return teeValmisteenKaytotapaVaikuttavallaAineella();
    }

    protected ValmisteenKayttotapaTO teeValmisteenKaytotapaVaikuttavallaAineella() {
        ValmisteenKayttotapaTO to = new ValmisteenKayttotapaTO();
        to.setLaakemuoto("tabletti");
        return to;
    }

    protected ValmisteenKayttotapaTO teeValmisteenKayttotapaKokonaisMaaralla() {
        ValmisteenKayttotapaTO to = new ValmisteenKayttotapaTO();
        to.setLaakemuoto("EMULS VOIDE");
        return to;
    }

    protected ValmisteenKayttotapaTO teeValmisteenKaytotapaPKV_iteroitu_pienempi_pakkaus() {
        ValmisteenKayttotapaTO to = new ValmisteenKayttotapaTO();
        to.setLaakemuoto("tabletti");
        return to;
    }

    protected ValmisteenKayttotapaTO teeValmisteenKaytotapaLaaketietokannanUlkopuolinenValmiste() {
        ValmisteenKayttotapaTO to = new ValmisteenKayttotapaTO();
        to.setLaakemuoto("purutabletti");
        return to;
    }

    protected ValmisteenMuutTiedotTO teeMuutTiedot() {
        ValmisteenMuutTiedotTO to = new ValmisteenMuutTiedotTO();
        return to;
    }

    protected ValmisteTO teeValmistePKV_iteroitu_pienempi_pakkaus() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotPKV_iteroitu_pienempi_pakkaus());
        to.getVaikuttavatAineet().add(teeVaikuttavaAine("diatsepaami"));

        to.getKayttotavat().add(teeValmisteenKaytotapaPKV_iteroitu_pienempi_pakkaus());
        // to.setMuutTiedot(teeMuutTiedot());
        return to;
    }

    protected ValmisteTO teeValmisteLaaketietokannanUlkopuolinenValmiste() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotLaaketietokannanUlkopuolinenValmiste());
        to.getVaikuttavatAineet().add(teeVaikuttavaAine());
        to.getKayttotavat().add(teeValmisteenKaytotapaLaaketietokannanUlkopuolinenValmiste());
        // to.setMuutTiedot(teeMuutTiedot());
        return to;
    }

    protected ValmisteTO teeValmisteVaikuttavallaAineella() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotVaikuttavallaAineella());
        to.getVaikuttavatAineet().add(teeVaikuttavaAine());
        to.getKayttotavat().add(teeValmisteenKaytotapaVaikuttavallaAineella());
        to.setMuutTiedot(teeMuutTiedot());
        return to;
    }

    protected ValmisteTO teeValmisteKokonaismaaralla() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotKokonaismaaralla());
        to.getKayttotavat().add(teeValmisteenKayttotapaKokonaisMaaralla());
        return to;
    }

    protected ValmisteTO teeValmisteYhdValmAjalleAnnos() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotYhdValmAjalleAnnos());
        to.getKayttotavat().add(teeValmisteenKayttotapaYhdValmAjalleAnnos());
        to.getVaikuttavatAineet().addAll(teeVaikuttavatAineetYhdValmAjalleAnnos());
        return to;
    }

    protected ValmisteTO teeValmisteAptRakentKoodTehdas() {
        ValmisteTO to = new ValmisteTO();
        to.setYksilointitiedot(teeValmisteenYksilointitiedotAptRakentKoodTehdas());
        return to;
    }

    protected ApteekissaValmistettavaLaakeTO teeApteekissaValmistettavaRakentkoodTehdas() {
        ApteekissaValmistettavaLaakeTO to = new ApteekissaValmistettavaLaakeTO();
        to.setValmistusohje("M.f.ungt.d.s.");
        to.getVaikuttavatAinesosat()
                .add(teeVaikuttavaAinesosa("g", 30, "D07AC03", "Desoksimetasoni", "Ibaril 0,25 % emulsiovoide"));
        to.getMuutAinesosat().add(teeMuuAinesosa("g", 70, "Novalan"));
        return to;
    }

    private LaakemaaraysTO teeApteekissaValmistettava_valmiste() {
        LaakemaaraysTO to = new LaakemaaraysTO();
        to.setPotilas(teePotilas());
        to.setLaatimispaikka(teeOrganisaatio());
        to.setAmmattihenkilo(teeAmmattihenkilo());
        to.setCDAOidBase("1.2.245.2123");
        to.setLaakevaihtokielto(false);
        to.setAnnosjakelu(false);
        to.setAnnosjakeluTeksti("");
        to.setSICmerkinta(false);
        to.setHuume(false);
        to.setPysyvaislaakitys(false);
        to.setKyseessaLaakkeenkaytonAloitus(true);
        to.setUudistamiskielto(false);
        to.setIterointiTeksti("");
        to.setIterointienMaara(0);
        to.setIterointienValiUnit("");
        to.setIterointienValiValue(0);
        to.getHoitolajit().add("S");
        to.setMaarayspaiva(null);
        // TODO: voimassaolon loppuaika
        to.setReseptintyyppi("2"); // 1:Pakkaus 2:kokonaismäärä 3:aika
        to.setLaakkeenKokonaismaaraUnit("ml");
        to.setLaakkeenKokonaismaaraValue(500);
        to.setAnnostusohje("5 ml 6 kertaa vuorokaudessa.");
        to.setKayttotarkoitusTeksti("Kovaan kipuun.");
        to.setUudistamiskielto(false);
        to.setUusimiskiellonSyy("");
        to.setUusimiskiellonPerustelu("");
        to.setViestiApteekille("");
        to.setErillisselvitysteksti("");
        to.setReseptinLaji("1");

        to.setLaaketietokannanVersio("2014.008");

        to.setApteekissaValmistettavaLaake(teeApteekissaValmistettavaRakentkoodTehdas());
        to.setApteekissaValmistettavaLaake(true);
        // Apteekissa valmistettava valmiste on lääketietokannan ulkopuolinen valmiste
        to.setLaaketietokannanUlkopuolinenValmiste("Oksikodonilius");

        to.setValmiste(teeValmisteAptRakentKoodTehdas());

        return to;
    }

    protected OsoiteTO teeOsoite() {
        OsoiteTO to = new OsoiteTO();
        to.setKatuosoite("Potilastie 2");
        to.setMaa("");
        to.setPostinumero("50600");
        to.setPostitoimipaikka("Kotka");
        return to;
    }

    protected OrganisaatioTO teeOrganisaatio() {
        OrganisaatioTO to = new OrganisaatioTO();
        to.setNimi("Testi terveysasema");
        to.setOsoite(teeOsoite());
        to.setPuhelinnumero("tel:0201234567");
        to.setYksilointitunnus("1.2.246.10.1602257.10.1");
        return to;
    }

    protected HenkilotiedotTO teePotilas() {
        HenkilotiedotTO to = new HenkilotiedotTO(
                new KokoNimiTO("PotilasKutsuma", "PotilasSuku", Arrays.asList("PotilasToinenEtunimi")),
                KantaCDATestUtils.testHetu);
        return to;
    }

    protected HenkilotiedotTO teePotilas_Ei_Hetua() {
        HenkilotiedotTO to = new HenkilotiedotTO(
                new KokoNimiTO("PotilasKutsuma", "PotilasSuku", Arrays.asList("PotilasToinenEtunimi")), "21.03.1968",
                new Integer(1));
        return to;
    }

    @Test
    public void testApteekissavalmistettavaLaake() throws IOException {
        Properties properties = KantaCDATestUtils.loadProperties("testi_properties.properties");
        ReseptiKasaaja tested = new ReseptinUudenKasaaja(properties, teeApteekissaValmistettava_valmiste());
        POCDMT000040ClinicalDocument cda = tested.kasaaReseptiCDA();
        assertNotNull("cda ei voi olla null", cda);
        POCDMT000040Section section = cda.getComponent().getStructuredBody().getComponents().get(0).getSection()
                .getComponents().get(0).getSection().getComponents().get(0).getSection();
        assertNotNull(
                "ClinicalDocument/component/structuredBody/component/section/component/section/component/section ei voi olla null",
                section);
        boolean laakevalmisteenJaPakkausenTiedot_loytyi = false;
        for (POCDMT000040Entry entry : section.getEntries()) {
            if ( null != entry.getOrganizer() && null != entry.getOrganizer().getCode()
                    && null != entry.getOrganizer().getCode().getCode() ) {
                if ( "83".equals(entry.getOrganizer().getCode().getCode()) ) {
                    laakevalmisteenJaPakkausenTiedot_loytyi = true;
                    POCDMT000040Consumable consumable = entry.getOrganizer().getComponents().get(0)
                            .getSubstanceAdministration().getConsumable();
                    assertNotNull(
                            "entry/organizer[/code/@code=83]/component/substanceAdministration/consumable ei voi olla null",
                            consumable);
                    assertNotNull("consumable/manufacturedProduct ei voi olla null",
                            consumable.getManufacturedProduct());
                    POCDMT000040ManufacturedProduct manufacturedProduct = consumable.getManufacturedProduct();
                    assertNotNull("manufacturedProduct/manufacturedMaterial ei voi olla null",
                            manufacturedProduct.getManufacturedMaterial());
                    POCDMT000040Material manufacturedMaterial = manufacturedProduct.getManufacturedMaterial();
                    assertNotNull("manufacturedMaterial/name ei voi olla null", manufacturedMaterial.getName());
                    EN name = manufacturedMaterial.getName();
                    assertNotNull("name content ei voi olla null", name.getContent().get(0));
                    String apteekissaValmistettavanValmisteenNimi = (String) name.getContent().get(0);
                    assertEquals("valmisteen nimen tulee olla: Oksikodonilius", "Oksikodonilius",
                            apteekissaValmistettavanValmisteenNimi);
                }
            }
        }
        assertTrue("Lääkevalmisteen ja pakkauksen tiedot pitää löytyä asiakirjalta",
                laakevalmisteenJaPakkausenTiedot_loytyi);

        /// ClinicalDocument/component/structuredBody/component/section/component/section/component/section/entry[1]/organizer/component/substanceAdministration/consumable
    }

    @Ignore
    @Test
    public void test1() throws JAXBException {
        String CDA = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ClinicalDocument xmlns=\"urn:hl7-org:v3\" xmlns:hl7fi=\"urn:hl7finland\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:hl7-org:v3 CDA_Fi.xsd\"> <realmCode code=\"FI\"/> <typeId extension=\"POCD_HD00040\" root=\"2.16.840.1.113883.1.3\"/> <templateId root=\"1.2.246.777.11.2007.17\"/> <id root=\"1.2.246.10.11423723.93.2009.13723852644625832\"/> <code code=\"1\" codeSystem=\"1.2.246.537.5.40105.2006\" codeSystemName=\"Reseptisanoman tyyppi\" displayName=\"Lääkemääräys\"/> <title>Lääkemääräys</title> <effectiveTime value=\"20150212085054\"/> <confidentialityCode code=\"5\" codeSystem=\"1.2.246.777.5.99902.2006\" codeSystemName=\"Luottamuksellisuus\" displayName=\"Salassapidettävä\"/> <languageCode code=\"fi\"/> <setId root=\"1.2.246.10.11423723.93.2009.13723852644625832\"/> <versionNumber value=\"1\"/> <recordTarget> <patientRole> <id extension=\"210887-222L\" root=\"1.2.246.21\"/> <patient> <name><given>Henna</given><given>Marketta</given><family>Leonerpee</family></name> <administrativeGenderCode code=\"2\" codeSystem=\"1.2.246.537.5.1\"/> <birthTime value=\"19870821\"/> </patient> </patientRole> </recordTarget> <author> <functionCode code=\"LAL\" codeSystem=\"1.2.246.537.5.40006.2003\" codeSystemName=\"Lääkärin funktio\" displayName=\"Lääkityksen aloittanut lääkäri\"/> <time/> <assignedAuthor> <id extension=\"100693\" root=\"1.2.246.537.25.1\"/> <id extension=\"198705477\" root=\"1.2.246.537.26\"/> <code code=\"20G\" codeSystem=\"1.2.246.537.6.24.2003\" codeSystemName=\"Hilmo - Terveydenhuollon erikoisalat\" displayName=\"Astrologia\"> <translation> <qualifier> <name code=\"1.2\" codeSystem=\"1.2.246.537.6.12.999.2003\" displayName=\"Virkanimike\"/> <value> <originalText>Tohtori</originalText> </value> </qualifier> <qualifier> <name code=\"1.3\" codeSystem=\"1.2.246.537.6.12.999.2003\" displayName=\"Oppiarvo\"/> <value> <originalText>Med Dr</originalText> </value> </qualifier> <qualifier> <name code=\"151\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Ammattioikeus\"/> <value code=\"034\" codeSystem=\"1.2.246.537.6.140.2008\" codeSystemName=\"Valvira - Ammattioikeudet\" displayName=\"Laillistettu erikoislääkäri\"/> </qualifier> </translation> </code> <assignedPerson> <name><given>Vilhelmiina Adelmiina</given><family>Heinänenä</family><suffix>Tohtori</suffix></name> </assignedPerson> <representedOrganization> <id root=\"1.2.246.10.10694480.10.1\"/> <name>Nimi reseptille</name> <telecom value=\"020 23456789\"/> <addr> <streetAddressLine>Hatanpäänvaltatie 1</streetAddressLine> <postalCode>12340</postalCode> <city>Testikaupunki</city> </addr> <asOrganizationPartOf> <wholeOrganization> <id root=\"1.2.246.10.10694480.10.0\"/> <name>Lääkärikeskus Oy</name> <addr> <streetAddressLine>Hatanpäänvaltatie 1</streetAddressLine> <postalCode>12340</postalCode> <city>Testikaupunki</city> </addr> </wholeOrganization> </asOrganizationPartOf> </representedOrganization> </assignedAuthor> </author> <custodian> <assignedCustodian> <representedCustodianOrganization> <id root=\"1.2.246.10.2462460.19.1\"/> <name>Kansaneläkelaitos</name> <addr use=\"PST\"> <postBox>PL 450</postBox> <city>Helsinki</city> <postalCode>00101</postalCode> </addr> </representedCustodianOrganization> </assignedCustodian> </custodian> <componentOf> <encompassingEncounter> <id root=\"1.2.246.10.11409047.93.2009.19047881555518874\"/> <effectiveTime operator=\"A\" value=\"20150212085054\"/> <location> <healthCareFacility> <id root=\"1.2.246.10.10694591.10.1\"/> <location> <name>Nimi reseptille</name> <addr> <streetAddressLine>Hatanpäänvaltatie 1</streetAddressLine> <postalCode>12340</postalCode> <city>Testikaupunki</city> <country>Finland</country> </addr> </location> <serviceProviderOrganization> <id root=\"1.2.246.10.10694591.10.0\"/> <name>Lääkärikeskus Oy</name> <addr> <streetAddressLine>Hatanpäänvaltatie 1</streetAddressLine> <postalCode>12340</postalCode> <city>Testikaupunki</city> </addr> </serviceProviderOrganization> </healthCareFacility> </location> </encompassingEncounter> </componentOf> <hl7fi:localHeader> <hl7fi:declaredTime value=\"20150212085054\"/> <hl7fi:softwareSupport>Skripti 1.0</hl7fi:softwareSupport> <hl7fi:signatureCollection><hl7fi:signature ID=\"OID1.2.246.10.11423723.93.2009.13723852644625832.20150212.085054284\"><hl7fi:signatureDescription code=\"4\" codeSystem=\"1.2.246.537.5.40127.2006\" codeSystemName=\"Sähköisen allekirjoituksen tyyppi\" displayName=\"Järjestelmäallekirjoitus / KanTa\"/><hl7fi:signatureTimestamp ID=\"ID2015.02.12.08.50.54.0286\">2015-02-12T08:50:54+02:00</hl7fi:signatureTimestamp><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"MPS_ee78fb8a5d84-e70e20a7-f07f-4e68-a9d6-c4d4ddc0a18a-252e51331e97\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform Algorithm=\"http://www.w3.org/TR/1999/REC-xpath-19991116\"><ds:XPath xmlns:hl7=\"urn:hl7-org:v3\">ancestor-or-self::hl7:structuredBody</ds:XPath></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>N65sWp2umAcECnJgcqRMUmnC0bw=</ds:DigestValue></ds:Reference><ds:Reference URI=\"#ID2015.02.12.08.50.54.0286\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>qcqFlzFourCO8X48C3FhBNhx0k8=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>GVSAUiN6Nxtfdn7BU1m+shYdyvEKqtZnVhZZ7bj1YS6oS1CbfHyaOhsUaiDK3bfnel6GvLsPOamP ZFRQ5+VyPFrPQwsi5VeIFL5WeyICVQO32dbuKMy4SAobdA26eWMftBjDeEsPSfutgz2/56WHpYg6 f5SXZtOBugozTiH9xnqbvKwDppmGxWmc9xZNa3ToGaDZru8hva6BO2gTRWJ0MtPAKChaTcaSV399 Sumwp7BRq1ylbZaVKw1BHZ7l2pN6Bv+GhMiXnLFNrdWcxwOSliOmp+smevIAR4oV+S1SumlqpP0I fHvWmuVpeDHpLw4RHpSIVC4OiFi3SrVxXC889Q==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIGTDCCBTSgAwIBAgIEF9eRLTANBgkqhkiG9w0BAQUFADCBrTELMAkGA1UEBhMCRkkxEDAOBgNV BAgTB0ZpbmxhbmQxIzAhBgNVBAoTGlZhZXN0b3Jla2lzdGVyaWtlc2t1cyBURVNUMTAwLgYDVQQL EydUZXJ2ZXlkZW5odW9sbG9uIHRlc3RpcGFsdmVsdXZhcm1lbnRlZXQxNTAzBgNVBAMTLFZSSyBU RVNUIENBIGZvciBIZWFsdGhjYXJlIFNlcnZpY2UgUHJvdmlkZXJzMB4XDTEzMDUyMjA2MTUwMFoX DTE4MDQxMDIwNTkwMFowejELMAkGA1UEBhMCRkkxGjAYBgNVBAoTEUthbnNhbmVsYWtlbGFpdG9z MRUwEwYDVQQLDAxJTlRfZVJlc2VwdGkxHDAaBgNVBAUTEzEuMi4yNDYuNTU2LjEwMDAxLjExGjAY BgNVBAMMEUlOVF9SZXNlcHRpa2Vza3VzMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA iifTDy6uQvecqfpGDqQdfcZnTH1SlNYdJoONcF3OOx8TmaZdELluvrKgPiNU81tyJImY6qAhSLXQ x9hgLbnoXWGUWjk7cOHp6CzhokZe9cOWRiIBDx3ynf2tUd3qqG5TCzvKGhL+SPmIREcmvcvi5IxK ZRbqEjstgK5MqRcL3Qx5R/B5SMR6zBChDZdDQG/F46Gpzbe3EUqh817X35fGj17DqlYnokChpuOD yH51WxpdAFYlpjiCEUpMNqcYMUGxnjGZCf6YfiSXF2ZL5N3715v4A8oiAe4KwIQIQ9hgE1rlORK+ 7mopNjDfR6NLT3QE4nN9DJWxT3OfT/pWNS1MqQIDAQABo4ICpDCCAqAwDAYDVR0TAQH/BAIwADCB 1wYDVR0gBIHPMIHMMAgGBgQAj3oBAzCBvwYJKoF2hAVjCgkCMIGxMIGFBggrBgEFBQcCAjB5GndW YXJtZW5uZXBvbGl0aWlra2Egb24gc2FhdGF2aWxsYSAtIENlcnRpZmlrYXQgcG9saWN5IGZpbm5z IC0gQ2VydGlmaWNhdGUgcG9saWN5IGlzIGF2YWlsYWJsZSBodHRwOi8vd3d3LmZpbmVpZC5maS9j cHM5OTAnBggrBgEFBQcCARYbaHR0cDovL3d3dy5maW5laWQuZmkvY3BzOTkvMEEGCCsGAQUFBwEB BDUwMzAxBggrBgEFBQcwAoYlaHR0cDovL3Byb3h5LmZpbmVpZC5maS9jYS92cmt0aHNwLmNydDAO BgNVHQ8BAf8EBAMCBsAwHwYDVR0jBBgwFoAUnpFu0k8QBMs8s1UtDWc/LtXgb2EwggEhBgNVHR8E ggEYMIIBFDAtoCugKYYnaHR0cDovL3Byb3h5LmZpbmVpZC5maS9jcmwvdnJrdGhzcGMuY3JsMIHi oIHfoIHchoHZbGRhcDovL2xkYXAuZmluZWlkLmZpOjM4OS9jbiUzZFZSSyUyMFRFU1QlMjBDQSUy MGZvciUyMEhlYWx0aGNhcmUlMjBTZXJ2aWNlJTIwUHJvdmlkZXJzLG91JTNkVGVydmV5ZGVuaHVv bGxvbiUyMHRlc3RpcGFsdmVsdXZhcm1lbnRlZXQsbyUzZFZhZXN0b3Jla2lzdGVyaWtlc2t1cyUy MFRFU1QsZG1kTmFtZSUzZEZJTkVJRCxjJTNkRkk/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDAd BgNVHQ4EFgQUynJSv12lqOjRx8JqEbge7Ht/FWowDQYJKoZIhvcNAQEFBQADggEBAJh9Q4k538cS FRaw9MTWUjHbvbixMpZC2hmpwBrpmT3cqOyAideXHzswxth0AFCOURj6fwcRctJGQvYSj9s1rP2Q U3ppNycCvJx+6juKEugDxYa+k2wrCAXTsMpvpglIs/Dn23Tn0zWdVtTfscv49dC3XeNAlpEaX8Di OVM7Athe6qHoT6C7shdAFi6Vy4MkZybarSDfFvTAXeblPTUOB4EKzHJMNEo+DJr++Xxz8/NFSnaD N5I7APVTabllPlpAVoDRxrEL2I5o5Z78jLCO0iqyR+fZOXE2jox+ilVwup/6KFKPWomNv8ajQd21 QUmmbAKL+44qbVsiyNQxNPzcUgk=</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></hl7fi:signature></hl7fi:signatureCollection><hl7fi:sender> <hl7fi:senderCode code=\"1\" codeSystem=\"1.2.246.10.1234567.18\" codeSystemName=\"Osapuolitunnukset organisaatiossa 1234567\"/> <hl7fi:senderName>musti.xx.satshp.fi</hl7fi:senderName> </hl7fi:sender> </hl7fi:localHeader> <component> <structuredBody> <component> <templateId root=\"1.2.246.777.11.2007.18\"/> <section ID=\"OID1.2.246.10.98765432.93.2007.2.1\"> <id root=\"1.2.246.10.98765432.93.2007.2.1\"/> <code code=\"1\" codeSystem=\"1.2.246.537.5.40105.2006\" codeSystemName=\"Reseptisanoman tyyppi\" displayName=\"Lääkemääräys\"/> <title>Lääkemääräys</title> <component> <section ID=\"OID1.2.246.10.98765432.93.2007.2.2\"> <id root=\"1.2.246.10.98765432.93.2007.2.2\"/> <text> <paragraph> <content>Lääkärikeskus Oy</content> </paragraph> <paragraph> <content/> </paragraph> <paragraph> <content>Tohtori Vilhelmiina Adelmiina Heinänenä</content> </paragraph> </text> <component> <section ID=\"OID1.2.246.10.98765432.93.2007.2.3\"><id root=\"1.2.246.10.98765432.93.2007.2.3\"/><text> <paragraph> <content>RENITEC COMP</content> </paragraph> <paragraph> <content>1 tabletti aamuisin.</content> </paragraph> <paragraph> <content>RENITEC COMP</content> </paragraph> </text><entry> <organizer classCode=\"CLUSTER\" moodCode=\"EVN\"> <code code=\"83\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Lääkevalmisteen ja pakkauksen tiedot\"/> <statusCode code=\"completed \"/> <component> <substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\"><effectiveTime value=\"20150212085054\"/><doseQuantity><translation> <originalText>20/12,5 mg</originalText> </translation></doseQuantity><consumable> <manufacturedProduct><manufacturedLabeledDrug><code code=\"C09BA02\" codeSystem=\"1.2.246.537.6.32.2007\" codeSystemName=\"Lääkelaitos - ATC Luokitus\" codeSystemVersion=\"2007.023\" displayName=\"Enalapriili ja diureetit\"/></manufacturedLabeledDrug></manufacturedProduct> </consumable><entryRelationship typeCode=\"COMP\"> <supply classCode=\"SPLY\" moodCode=\"EVN\"><code code=\"2\" codeSystem=\"1.2.246.537.5.40100.2006\" codeSystemName=\"Määrätyn määrän esittämistapa\" displayName=\"Kokonaismäärä\"/><independentInd value=\"false\"/><quantity unit=\"fol\" value=\"400\"/><subject typeCode=\"SBJ\"> <relatedSubject classCode=\"PAT\"> <code code=\"210887-222L\" codeSystem=\"1.2.246.21\"/> <subject classCode=\"PSN\"> <name><given>Henna</given><given>Marketta</given><family>Leonerpee</family></name> <birthTime value=\"19870821\"/> </subject> </relatedSubject> </subject><product> <manufacturedProduct> <manufacturedLabeledDrug> <code code=\"446393\" codeSystem=\"1.2.246.537.6.55.2004\" codeSystemName=\"VNR\" codeSystemVersion=\"2007.023\" displayName=\"RENITEC COMP\"> <translation> <qualifier> <name code=\"94\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"VNR-numeroon liittyvä laji\"/> <value code=\"1\" codeSystem=\"1.2.246.537.5.40126.2006\" codeSystemName=\"Lääketietokannasta saatava valmisteen laji\" displayName=\"Lääke\"/> </qualifier> </translation> </code> <name/> </manufacturedLabeledDrug> </manufacturedProduct> </product><author> <time/> <assignedAuthor> <id extension=\"100693\" root=\"1.2.246.537.25.1\"/> <code code=\"20G\" codeSystem=\"1.2.246.537.6.24.2003\" codeSystemName=\"Hilmo - Terveydenhuollon erikoisalat\" displayName=\"Astrologia\"> <translation> <qualifier> <name code=\"1.2\" codeSystem=\"1.2.246.537.6.12.999.2003\" displayName=\"Virkanimike\"/> <value> <originalText>Tohtori</originalText> </value> </qualifier> <qualifier> <name code=\"1.3\" codeSystem=\"1.2.246.537.6.12.999.2003\" displayName=\"Oppiarvo\"/> <value> <originalText>Med Dr</originalText> </value> </qualifier> </translation> </code> <assignedPerson> <name><given>Vilhelmiina Adelmiina</given><family>Heinänenä</family></name> </assignedPerson> <representedOrganization> <id root=\"1.2.246.10.10694480.10.1\"/> <name>Nimi reseptille</name> <telecom value=\"020 23456789\"/> <addr> <streetAddressLine>Hatanpäänvaltatie 1</streetAddressLine> <postalCode>12340</postalCode> <city>Testikaupunki</city> </addr> </representedOrganization> </assignedAuthor> </author><entryRelationship typeCode=\"COMP\"> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"24\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Lääkemuoto\"/> <value xsi:type=\"ST\">tabletti</value> </observation> </entryRelationship><entryRelationship typeCode=\"COMP\"> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"128\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"säilytysastia\"/> <value xsi:type=\"SC\">läpipaino-pakkaus</value> </observation> </entryRelationship><entryRelationship typeCode=\"COMP\"> <observation classCode=\"OBS\" moodCode=\"EVN\"><code code=\"164\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Valmisteen laji\"/></observation> </entryRelationship><reference typeCode=\"SPRT\"> <externalDocument> <id root=\"1.2.246.10.11423723.93.2009.13723852644625832\"/> <code code=\"1\" codeSystem=\"1.2.246.537.5.40105.2006\" codeSystemName=\"Reseptisanoman tyyppi\" displayName=\"Lääkemääräys\"/> <setId root=\"1.2.246.10.11423723.93.2009.13723852644625832\"/> </externalDocument> </reference></supply> </entryRelationship></substanceAdministration> </component> </organizer> </entry><entry> <organizer classCode=\"CLUSTER\" moodCode=\"EVN\"><code code=\"4\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Lääkkeen vaikuttava aine\"/><statusCode code=\"completed\"/><component> <substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\"><consumable> <manufacturedProduct> <manufacturedLabeledDrug> <code codeSystem=\"1.2.246.537.6.32.2007\" codeSystemName=\"Lääkelaitos - ATC Luokitus\" codeSystemVersion=\"2007.023\"/> <name>enalapriilimaleaatti</name> </manufacturedLabeledDrug> </manufacturedProduct> </consumable></substanceAdministration> </component><component> <substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\"><consumable> <manufacturedProduct> <manufacturedLabeledDrug> <code codeSystem=\"1.2.246.537.6.32.2007\" codeSystemName=\"Lääkelaitos - ATC Luokitus\" codeSystemVersion=\"2007.023\"/> <name>hydroklooritiatsidi</name> </manufacturedLabeledDrug> </manufacturedProduct> </consumable></substanceAdministration> </component></organizer> </entry><entry> <organizer classCode=\"CLUSTER\" moodCode=\"EVN\"> <code code=\"32\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Annososio ja jatko-osiot\"/> <statusCode code=\"completed\"/> <component> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"87\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Annostelu tekstimuodossa\"/> <value value=\"true\" xsi:type=\"BL\"/> </observation> </component> <component> <substanceAdministration classCode=\"SBADM\" moodCode=\"EVN\"><text>1 tabletti aamuisin.</text><effectiveTime operator=\"A\" xsi:type=\"PIVL_TS\"> </effectiveTime><doseQuantity/><maxDoseQuantity> <numerator/> <denominator/> </maxDoseQuantity><consumable nullFlavor=\"NI\"> <manufacturedProduct> <manufacturedLabeledDrug> </manufacturedLabeledDrug> </manufacturedProduct> </consumable></substanceAdministration> </component> </organizer> </entry><entry> <organizer classCode=\"CLUSTER\" moodCode=\"EVN\"><code code=\"88\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Lääkityksen muut tiedot\"/><statusCode code=\"completed\"/><component> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"58\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Käyttötarkoitus tekstinä\"/> <value xsi:type=\"ST\">Verenpainelääke.</value> </observation> </component><component> <observation classCode=\"OBS\" moodCode=\"EVN\"><code code=\"75\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Uudistamiskielto\"/></observation> </component><component> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"67\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"Hoitolaji\"/> <value code=\"S\" codeSystem=\"1.2.246.537.5.40101.2006\" codeSystemName=\"Hoitolaji\" displayName=\"Sairauden hoito\" xsi:type=\"CE\"/> </observation> </component><component> <observation classCode=\"OBS\" moodCode=\"EVN\"> <code code=\"68\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"pysyvä lääkitys\"/> <value value=\"true\" xsi:type=\"BL\"/> </observation> </component><component> <observation classCode=\"OBS\" moodCode=\"EVN\"><code code=\"169\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"THL - Reseptin laji\"/></observation> </component><component> <observation classCode=\"OBS\" moodCode=\"EVN\"><code code=\"194\" codeSystem=\"1.2.246.537.6.12.2002.126\" codeSystemName=\"Lääkityslista\" displayName=\"uudistamiskiellon syy ja perustelu\"/></observation> </component></organizer> </entry></section> </component> </section> </component> </section> </component> </structuredBody> </component> </ClinicalDocument>";

        POCDMT000040ClinicalDocument cda = null;
        cda = JaxbUtil.getInstance().unmarshaller(CDA);

        if ( cda != null ) {
            System.out.println("UnMarshallointi onnistui!");
        }
        String uusiCDA = "NA";
        try {
            uusiCDA = JaxbUtil.getInstance().marshalloi(cda);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("uusi cda:" + uusiCDA);
    }

    @Override
    protected void setupProperties() {
        KantaCDATestUtils.setupProperties();
    }
}
