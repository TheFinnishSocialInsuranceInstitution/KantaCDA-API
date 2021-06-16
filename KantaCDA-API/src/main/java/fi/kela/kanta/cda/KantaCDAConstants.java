package fi.kela.kanta.cda;

public final class KantaCDAConstants {

    public static final String TEL_PREFIX = "tel:";
    public static final String EMAIL_PREFIX = "mailto:";
    public static final int VNR_tunnus = 1;

    public final static class Laakityslista {
        private Laakityslista() {
        }

        public static final String CODESYSTEM = "1.2.246.537.6.12.2002.126";
        public static final String VAIKUTTAVAT_AINESOSAT = "4";
        public static final String MUUT_AINESOSAT = "10";
        public static final String LAAKEMUOTO = "24";
        public static final String TEKSTIMUOTOINEN_ANNOSTUSOHJE = "29";
        public static final String SYOTTOKOODI = "30";
        public static final String ANNOSOSIO_JA_JATKOOSIOT = "32";
        public static final String SIC_MERKINTA = "56";
        public static final String KAYTTOTARKOITUS_TEKSTINA = "58";
        public static final String LAAKKEEN_MUUTOKSEN_TAI_LOPETUKSEN_SYY = "65";
        public static final String HOITOLAJI = "67";
        public static final String PYSYVA_LAAKITYS = "68";
        public static final String ERILLISSELVITYS = "69";
        public static final String UUSIMISKIELTO = "75";
        public static final String LAAKEVAIHTOKIELTO = "81";
        public static final String LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT = "83";
        public static final String ANNOSTELU_VAIN_TEKSTINA = "87";
        public static final String RESEPTIN_MUUT_TIEDOT = "88";
        public static final String ALLE_12VUOTIAAN_PAINO = "89";
        public static final String ANNOSJAKELU = "91";
        public static final String VIESTI_APTEEKILLE = "92";
        public static final String LAAKETIETOKANNAN_LAJI = "94";
        public static final String LAAKEMAARAYKSEN_MITATOINNIN_SYY = "95";
        public static final String LAAKEMAARAYKSEN_MITATOINNIN_TYYPPI = "96";
        public static final String LAAKEMAARAYKSEN_MITATOINNIN_OSAPUOLI = "96.1";
        public static final String LAAKEMAARAYKSEN_MITATOINNIN_SUOSTUMUS = "96.2";
        public static final String KORJAUKSEN_PERUSTELU = "97";
        public static final String LAAKEMAARAYKSEN_MITATOINNIN_MUUT_TIEDOT = "98";
        public static final String LAAKEMAARAYKSEN_KORJAUKSEN_MUUT_TIEDOT = "99";
        public static final String LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT_TOIMITUSSANOMASSA = "100";
        public static final String OSAPAKKAUS = "101";
        public static final String TOIMITETTU_MAARA = "102";
        public static final String JALJELLA_OLEVA_MAARA = "103";
        public static final String TOIMITUKSEN_MUUT_TIEDOT = "104";
        public static final String LAAKE_VAIHDETTU = "105";
        public static final String APTEEKIN_HUOMAUTUS = "106";
        public static final String LISASELVITYS_KELALLE = "107";
        public static final String TOIMITUKSEN_HINTA = "108";
        public static final String TOIMITETTU_HINTAPUTKEEN_KUULUMATONTA_LAAKETTA = "109";
        public static final String OMAVASTUUOSUUKSIEN_LUKUMAARA = "110";
        public static final String UUSIMISPYYNNON_TILA = "111";
        public static final String UUSIMISPYYNNON_KIRJAAJAN_ANTAMA_VIESTI = "113";
        public static final String LAAKARIN_PERUSTELU_UUSIMISPYYNNOSSA = "114";
        public static final String OHJE_KANSALAISELLE = "115";
        public static final String LAAKITYKSEN_MUUT_TIEDOT = "116";
        public static final String POTILAAN_TUNNISTAMINEN = "117";
        public static final String HUUMAUSAINE_PKV_LAAKEMAARAYS = "119";
        public static final String UUSIMISPYYNNON_SUOSTUMUSTYYPPI = "120";
        public static final String ITEROINTI = "121";
        public static final String KOKONAAN_TOIMITETTU = "122";
        public static final String POTILAS_KIELTAYTYNYT_POTILASOHJEEN_TULOSTAMISESTA = "123";
        public static final String APTEEKISSA_VALMISTETTAVAN_LAAKKEEN_OSOITIN = "124";
        public static final String PAKKAUSKOON_KERROIN = "125";
        public static final String PAKKAUSKOKO_TEKSTIMUODOSSA = "126";
        public static final String LAITE = "127";
        public static final String SAILYTYSASTIA = "128";
        public static final String KYSEESSA_LAAKKEEN_KAYTON_ALOITUS = "129";
        public static final String LAAKARIN_ANTAMA_VIESTI_APTEEKILLE = "130";
        public static final String TIETO_POTILAAN_INFORMOINNISTA = "131";
        public static final String HUUME = "132";
        public static final String SUORITTAJAN_ROOLI = "150";// (EI KAYTOSSA VERSIOSTA 3.0 ALKAEN)
        public static final String AMMATTIOIKEUS = "151";
        public static final String APTEEKIN_AUKIKIRJOITTAMA_ANNOSTUSOHJE = "152";

        public static final String MAARATYN_LAAKKEEN_YKSILOIVA_TUNNUS = "160";
        public static final String LAAKKEEN_TYYPPI = "161";
        public static final String LAAKEMERKINNAN_SYNKRONOINTI = "162";
        public static final String LAAKKEEN_ALKUPERAINEN_ALOITUSPAIVA = "163";
        public static final String LAAKKEEN_LAJI = "164";
        public static final String AINEEN_KOODATTU_TARKENNE = "165";// KOODIN MUKAINEN NIMI JA KOODISTO
        public static final String AINEEN_LAAKEMUOTO = "166";
        public static final String APTEEKISSA_VALMISTETTAVAN_LAAKKEEN_OHJEENMUKAINEN_KOKONAISMAARA_JA_MAARAN_YKSIKKO = "167";
        public static final String LISATIETO = "168";
        public static final String RESEPTIN_LAJI = "169";
        public static final String ANNOSJAKELUN_OSOITTIMEN_TARKENNE = "171";
        public static final String LAAKKEEN_KAYTTOTARKOITUS_RAKENTEISENA = "172";
        public static final String LAAKKEEN_PAATTYMISPAIVA = "173";
        public static final String LAAKETIETOKANNAN_VERSIO = "174";
        public static final String VALMISTEEN_LISATIETO = "175";
        public static final String LISASEURANTAA_VAATIVA_LAAKE = "176";
        public static final String VERI_TAI_PLASMAPERAINEN_VALMISTE = "177";
        public static final String LAAKKEENANTOREITTI = "178";
        public static final String LAAKKEENANTOTAPA = "179";
        public static final String ANNOSTUSOHJEEN_LYHYT_ESITYSMUOTO = "180";
        public static final String RAKENTEISEN_ANNOSTUSOHJEEN_KAYTTO = "181";
        public static final String RAKENTEINEN_ANNOSTUSOHJE = "182";
        public static final String ANNOSTELUUN_KUULUVIEN_KAUSIEN_MAARA = "183";
        public static final String ANNOSTELUKAUSI_JA_ANNOKSET = "184";
        public static final String ANNOSTUS_TARVITTAESSA = "185";
        public static final String LAAKE_TAUOLLA = "186";
        public static final String TASA_ANNOSTUS = "187";
        public static final String ANNOSTUKSEN_VASTEOHJE = "188";
        public static final String ANNOSJAKSON_PAIVA = "189";
        public static final String ANNOSTEN_MAARA_ANNOSJAKSOSSA = "190";
        public static final String INFUUSIOLAAKKEEN_ERITYISTIEDOT = "191";
        public static final String KIRJALLISEN_JA_PUHELINRESEPTIN_PERUSTELU = "192";
        public static final String KIRJALLISEN_JA_PUHELINRESEPTIN_PERUSTELUN_TARKENNE = "193";
        public static final String UUDISTAMISKIELLON_SYY = "194";
        public static final String LAAKKEEN_TARKASTUSTIEDOT = "200";
        public static final String LAAKKEEN_ASIANMUKAISUUDEN_TARKASTUSPAIVAMAARA = "201";
        public static final String LAAKKEEN_AJANTASAISUUDEN_TARKASTUSPAIVAMAARA = "202";
        public static final String TARKASTUSMERKINNAN_LISATIETO = "203";
        public static final String POTILAAN_TUOTTAMA_LISATIETO = "204";
        public static final String LAAKKEEN_ANTOKIRJAUSTEN_TIEDOT = "210";
        public static final String VIITTAUS_ANTOKIRJAUKSELTA_MAARAYKSEN_TIETOIHIN = "211";
        public static final String APTEEKISSA_TALLENNETTU_LAAKEMAARAYS = "212";
        public static final String APTEEKISSA_TALLENNETTU_LAAKEMAARAYS_PERUSTELU = "213";
        public static final String LAAKARINPALKKIO = "214";
        public static final String LAAKARINPALKKIO_ERIKOISLAAKARINA = "215";
        public static final String TARTUNTATAUTILAIN_MUKAINEN_LAAKE = "216";
        public static final String ANNOSTELUKAUSI_CODE = "230";
        public static final String ANNOSTELUKAUSI_ID = "annostelukausiId";
        public static final String KAYTTOOHJEEN_LISATIETO_CODE = "234";
        public static final String ANNOSTELUKAUDEN_KESTO_CODE = "235";
        public static final String LAAKE_TAUOLLA_CODE = "236";
        public static final String ANNOSTUS_TARVITTAESSA_CODE = "237";
		public static final String ANNOSJAKSON_PITUUS_CODE = "238";
        public static final String ANNOKSET_CODE = "239";
        public static final String ANNOS_ID = "annosId";
		public static final String ANNOSAIKA_CODE = "240";
        public static final String FYS_ANNOKSET_CODE = "241";
        public static final String ANNOS_TARVITTAESSA_CODE = "243";
		public static final String ANNOSAJANKOHTA_CODE = "244";
		public static final String ANNOSJAKSON_PAIVA_CODE = "245";
		public static final String ANNOSTUKSEN_LISATIEDOT_CODE = "250";
    }
    
    public final static class OstopalvelunValtuutus {
        private OstopalvelunValtuutus() {
        }

        public static final String CODESYSTEM = "1.2.246.537.6.12.2002.362";
        public static final String OSVA = "0";
        public static final String ASIAKIRJAN_TUNNISTE = "1";
        public static final String ASIAKIRJAN_YKSILOIVA_TUNNISTE = "2";
        public static final String PALVELUN_TUOTTAJA = "12";
        public static final String PALVELUN_TUOTTAJAN_YKSILOINTITUNNUS = "13";
        public static final String PALVELUN_TUOTTAJAN_NIMI = "14";
        public static final String OSTOP_TUOTT_OIKEUS_HAKEA_PALVELUN_JARJ_REK = "15";
        public static final String REKISTERINPITAJA_HAKU = "16";
        public static final String REKISTERI_HAKU = "17";
        public static final String REKISTERIN_TARKENNE_HAKU = "18";
        public static final String REKISTERIN_TARKENTIMEN_NIMI_HAKU = "19";
        public static final String OSTOP_TUOTT_OIKEUS_TALLENTAA_PALV_JARJ_REK = "20";
        public static final String REKISTERINPITAJA_TALLENNUS = "21";
        public static final String REKISTERI_TALLENNUS = "22";
        public static final String REKISTERIN_TARKENNE_TALLENNUS = "23";
        public static final String REKISTERIN_TARKENTIMEN_NIMI_TALLENNUS = "24";
        public static final String POTILAS = "25";
        public static final String HENKILOTUNNUS = "26";
        public static final String SUKU_JA_ETUNIMET = "27";
        public static final String SYNTYMAAIKA = "28";
        public static final String LUOVUTETTAVAT_ASIAKIRJAT = "29";
        public static final String KAIKKI_ASIAKIRJAT = "30";
        public static final String LUOVUTETTAVAN_AINEISTON_AIKAVALI = "31";
        public static final String LUOVUTETTAVAT_PALVELUTAPAHTUMAT = "32";
        public static final String OSTOPALVELUN_TYYPPI = "3";
        public static final String OSTOPALVELUN_TYYPPI_KOODI = "4";
        public static final String ASIAKIRJAN_TALLENTAJA = "33";
        public static final String AMMATTIHENKILON_TUNNISTE = "34";
        public static final String AMMATTIHENKILON_NIMI = "35";
        public static final String AMMATTIHENKILON_PALVELUYKSIKKO = "36";
        public static final String ASIAKIRJAN_TEKEMISEN_AJANKOHTA = "37";
        public static final String LOMAKKEEN_METATIEDOT = "38";
        public static final String TEMPLATEID = "39";
        public static final String MAARITTELYVERSIO = "40";
        public static final String PALVELUN_JARJESTAJAN_ARKISTOIMA_PALVELUTAPAHTUMA = "41";
        public static final String PALVELUTAPAHTUMAN_TUNNISTE = "42";
        public static final String OSTOPALVELUN_VALTUUTUKSEN_VOIMASSAOLO = "5";
        public static final String ASIAKIRJA_VOIMASSA = "6";
        public static final String PALVELUN_JARJESTAJA = "7";
        public static final String PALVELUN_JARJESTAJA_PALVELUYKSIKKO = "10";
        public static final String PALVELUN_JARJESTAJA_PALVELUYKSIKON_NIMI = "11";
        public static final String PALVELUN_JARJESTAJA_YKSILOINTITUNNUS = "8";
        public static final String PALVELUN_JARJESTAJAN_NIMI = "9";


    }

    public enum ReseptisanomanTyyppi {
        LAAKEMAARAYS(1),
        LAAKEMAARAYKSEN_MITATOINTI(2),
        LAAKEMAARAYKSEN_KORJAUS(3),
        LAAKEMAARAYKSEN_LUKITUS(4),
        LAAKEMAARAYKSEN_LUKITUKSEN_PURKU(5),
        LAAKEMAARAYKSEN_VARAUS(6),
        LAAKEMAARAYKSEN_VARAUKSEN_PURKU(7),
        UUSIMISPYYNTO(8),
        UUSIMISPYYNNON_VASTAUS(9),
        LAAKEMAARAYKSEN_TOIMITUS(10),
        LAAKEMAARAYKSEN_TOIMITUKSEN_MITATOINTI(11),
        LAAKEMAARAYKSEN_TOIMITUKSEN_KORJAUS(12),
        MUODOSTA_POTILASOHJE(13),
        MUODOSTA_YHTEENVETO_SAHKOISISTA_LAAKEMAARAYKSISTA(14),
        UUSIMISPYYNTOJEN_KASITTELYN_TILANNE(15),
        ANNOSJAKELU(16),
        ANNOSJAKELUN_PURKU(17),
        TOIMITUSVARAUKSEN_PURKU(18),
        KATSELUYHTEYDEN_LOKI(19),
        TIETOSUOJAVASTAAVAN_LOKI(20);

        private final int tyyppi;

        private ReseptisanomanTyyppi(int tyyppi) {
            this.tyyppi = tyyppi;
        }

        public int getTyyppi() {
            return tyyppi;
        }
    }

    public enum ReseptikyselynSyy {
        HOITO(1),
        POTILASOHJE(10),
        UUSIMISPYYNTÖJEN_KÄSITTELYN_TILANNE(11),
        KANSALAISEN_KATSELUYHTEYS(12),
        KANSALAISEN_KATSELUYHTEYS_LOKITIEDOT(13),
        RESEPTIKESKUKSEN_TIETOJEN_KÄYTÖN_VALVONTA(14),
        RESEPTIKESKUKSEEN_LIITTYVÄT_ERÄAJOT(15),
        UUSIMISPYYNTÖJEN_HAKU(16),
        KORJATUT_JA_MITÄTÖIDYT_ANNOSJAKELULÄÄKEMÄÄRÄYKSET(17),
        APTEEKIN_ANNOSJAKELUPOTILAIDEN_UUDET_LÄÄKEMÄÄRÄYKSET(18),
        KIIREELLINEN_HOITO(2),
        PKV_JA_HUUMAUSAINERESEPTIEN_HAKU(20),
        RESEPTIN_HAKU_UUSIMISPYYNNÖN_KÄSITTELYÄ_VARTEN(21),
        ULKOMAILLA_TAPAHTUVAA_LÄÄKKEEN_OSTOA_VARTEN_ANNETTAVA(22),
        ULKOMAAN_MATKAA_VARTEN_ANNETTAVA_JÄLJENNÖS(23),
        UUSIMISPYYNNÖN_LÄHETTÄMINEN(24),
        TEKNISEN_HAIRION_SELVITTAMINEN(25),
        KIELLETYN_RESEPTIN_HAKEMINEN_KORJAAMISTA_VARTEN(26),
        KIELTOJEN_YLLAPITO(27),
        POTILASOHJEEN_TULOSTUS_JALKIKATEEN(28),
        APTEEKIN_ANNOSJAKELUPOTILAIDEN_LAAKEMAARAYKSET(29),
        MITÄTÖINTI(3),
        KORJAUS(4),
        YHTEENVETO_SÄHKÖISISTÄ_LÄÄKEMÄÄRÄYKSISTÄ(5),
        TOIMITUS(6),
        TOIMITUKSEN_KORJAUS_TAI_MITÄTÖINTI(7),
        SV_KORVAUS(8),
        // MUU(9), 01.01.2006 09.07.2009
        MUU(99);

        private final int syy;

        private ReseptikyselynSyy(int syy) {
            this.syy = syy;
        }

        public String getSyy() {
            return String.valueOf(syy);
        }

        public int getSyyValue() {
            return syy;
        }
    }

    public enum SuostumusTyyppi {
        SUULLINEN_SUOSTUMUS(1),
        ALLEKIRJOITETTU_SUOSTUMUS(2),
        SUULLINEN_PYYNTÖ(3),
        POTILAS_EI_ANNA_SUOSTUMUSTA(4),
        SUOSTUMUSTA_EI_TARVITA(5);

        private final int tyyppi;

        private SuostumusTyyppi(int tyyppi) {
            this.tyyppi = tyyppi;
        }

        public String getTyyppi() {
            return String.valueOf(tyyppi);
        }

        public int getTyyppiValue() {
            return tyyppi;
        }
    }
    
    public enum OstopalvelunTyyppi {
    	POTILASKOHTAINEN_OSTOPALVELU(1),
    	VAESTOTASOINEN_OSTOPALVELU(2);
    	
    	private final int tyyppi;
    	
    	private OstopalvelunTyyppi(int tyyppi) {
    		this.tyyppi = tyyppi;
    	}
    	
    	public String getTyyppi() {
    		return String.valueOf(tyyppi);
    	}
    	
    	public int getTyyppiValue() {
    		return tyyppi;
    	}
    }
    
    public enum PotilasasiakirjanRekisteritunnus {
    	ERILLISSAILYTYS(1),
    	AMMATINHARJOITTAJA(10),
    	KAYTOSTA_POISTETUT_ASIAKIRJAT(11),
    	TIEDONHALLINTAPALVELUN_ASIAKIRJAT(12),
    	ARKISTOASIAKIRJAT(13),
    	JULKINEN_TERVEYDENHUOLTO(2),
    	YKSITYINEN_TERVEYDENHUOLTO(3),
    	TYOTERVEYSHUOLTO(4),
    	ILMOITUKSET_JA_TILASTOREKISTERIT(6),
    	POTILAAN_OMAT_ASIAKIRJAT(7),
    	TUTKIMUSREKISTERIT(8);
    	
    	private final int rekisteritunnus;
    	
    	private PotilasasiakirjanRekisteritunnus(int rekisteritunnus) {
    		this.rekisteritunnus = rekisteritunnus;
    	}
    	
    	public String getRekisteritunnus() {
    		return String.valueOf(rekisteritunnus);
    	}
    	
    	public int getRekisteritunnusValue() {
    		return rekisteritunnus;
    	}
    }

    public enum NullFlavor {
        NI("NI"), // NoInformation
        INV("INV"), // invalid
        DER("DER"), // deriver
        OTH("OTH"), // other
        NINF("NINF"), // negative infinity
        PINF("PINF"), // positive infinity
        UNC("UNC"), // un-encoded
        MSK("MSK"), // masked
        NA("NA"), // not applicable
        UNK("UNK"), // unknown
        ASKU("ASKU"), // asked but unknown
        NAV("NAV"), // temporarily unavailable
        NASK("NASK"), // not asked
        QS("QS"), // Sufficient Quality
        TRC("TRC"), // trace
        NP("NP");// not present

        private final String code;

        private NullFlavor(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * Asiakirjan määritysversion yhteensopivuus suhteessa järjestelmässä käytettyyn määritysversioon
     *
     */
    public enum AsiakirjaVersioYhteensopivuus {
        TAAKSEPAIN("1"),
        JARJESTELMA_VERSIO("2"),
        ETEENPAIN("3"),
        EI_TUETTU("4");

        private String koodi;

        private AsiakirjaVersioYhteensopivuus(String koodi) {
            this.koodi = koodi;
        }

        public String getKoodi() {
            return koodi;
        }

    }

    private KantaCDAConstants() {
    }
}
