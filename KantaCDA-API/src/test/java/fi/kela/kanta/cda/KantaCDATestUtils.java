package fi.kela.kanta.cda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;

import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.HenkilotiedotTO;
import fi.kela.kanta.to.KokoNimiTO;
import fi.kela.kanta.to.OrganisaatioTO;
import fi.kela.kanta.to.OsoiteTO;

/**
 * Kanta CDA TestUtils Testauksen apu luokka sisältää mm. - property lataus
 */
public class KantaCDATestUtils {

    private static final String defaultFile = "target\\CDA.xml";
    private static Properties mockProperties;
    public static String testBaseId = "1.2.246";
    public static String testIdRoot = "1.2.3.4.5";
    public static String testExtension = "TestExtension";
    public static String testEtunimi = "TestiEtu";
    public static String testSukunimi = "TestiSuku";
    public static String testHetu = "010495-901E";
    public static String testSvNumero = "3214321";
    public static String testRekisterointiNro = "12345678012";
    public static String testAmmattioikeus = "031";
    public static String testAmmattioikeusName = "laillistettu yleislääkäri";
    public static String testPuhNro = "tel:020556556";
    public static String testKatuosoite = "TestiKatu";
    public static String testRooli = "TestRooli";
    public static String testYksilointitunnus = "1.2.246.10.1602257.10.1";
    public static String testNimi = "TestiNimi";
    public static String testPostiNro = "010100";
    public static String testPostitoimipaikka = "TestiPaikka";
    public static String testCode = "TestCode";
    public static String testCodeSystem = "1.12.123.1234";
    public static String testCodeSystemName = "Test Code System";
    public static String testDisplayName = "Test Display Name";
    public static String testCodeSystemVersion = "1.2.3";
    public static String testPotilasEtunimi = "PotilasEtu";
    public static String testPotilasSukunimi = "PotilasSuku";
    public static String testPotilasHetu = "250675-665S";

    private KantaCDATestUtils() {
    }

    public static AmmattihenkiloTO luoAmmattihenkilo() {
        AmmattihenkiloTO ammattihenkiloTO = new AmmattihenkiloTO();
        ammattihenkiloTO.setRooli(KantaCDATestUtils.testRooli);
        ammattihenkiloTO.setSvNumero(KantaCDATestUtils.testSvNumero);
        ammattihenkiloTO.setRekisterointinumero(KantaCDATestUtils.testRekisterointiNro);
        ammattihenkiloTO.setAmmattioikeus(KantaCDATestUtils.testAmmattioikeus);
        ammattihenkiloTO.setAmmattioikeusName(KantaCDATestUtils.testAmmattioikeusName);
        ammattihenkiloTO.setKokonimi(new KokoNimiTO(KantaCDATestUtils.testEtunimi, KantaCDATestUtils.testSukunimi));
        ammattihenkiloTO.setOrganisaatio(luoOrganisaatio());
        return ammattihenkiloTO;
    }

    public static OrganisaatioTO luoOrganisaatio() {
        OrganisaatioTO organisaatioTO = new OrganisaatioTO();
        organisaatioTO.setYksilointitunnus(KantaCDATestUtils.testYksilointitunnus);
        organisaatioTO.setNimi(KantaCDATestUtils.testNimi);
        organisaatioTO.setPuhelinnumero(KantaCDATestUtils.testPuhNro);
        OsoiteTO osoite = new OsoiteTO();
        osoite.setKatuosoite(KantaCDATestUtils.testKatuosoite);
        osoite.setPostinumero(KantaCDATestUtils.testPostiNro);
        osoite.setPostitoimipaikka(KantaCDATestUtils.testPostitoimipaikka);
        organisaatioTO.setOsoite(osoite);
        return organisaatioTO;
    }

    public static HenkilotiedotTO luoPotilas() {
        HenkilotiedotTO potilas = new HenkilotiedotTO(new KokoNimiTO(testPotilasEtunimi, testPotilasSukunimi),
                testPotilasHetu);
        return potilas;
    }

    public static void mockProperty(String key, String value) {
        if ( null != key && !key.isEmpty() && null != value ) {
            mockProperties.setProperty(key, value);
        }
    }

    public static void mockCodeProperty(String key, String codeValue, String codeSystemValue, String codeSystemNameValue,
            String displayNameValue) {
        mockProperty(key + ".code", codeValue);
        mockProperty(key + ".codeSystem", codeSystemValue);
        mockProperty(key + ".codeSystemName", codeSystemNameValue);
        mockProperty(key + ".displayName", displayNameValue);
    }

    public static void mockCodeProperty(String key, String rootValue, String extensionValue) {
        mockProperty(key + ".root", rootValue);
        mockProperty(key + ".extension", extensionValue);
    }

    public static void setupProperties() {
        mockProperties = new Properties();
    }

    public static Properties getProperties() {
        return mockProperties;
    }

    public static Properties loadProperties(String propertyFile) throws IOException {
        InputStream input = null;
        Properties properties = new Properties();
        try {
            input = ReseptiKasaajaCDATest.class.getClassLoader().getResourceAsStream(propertyFile);
            properties.load(new InputStreamReader(input, "UTF-8"));
        }
        catch (FileNotFoundException fnfe) {
            System.out.println(propertyFile + " FILE NOT FOUND! :" + fnfe.getMessage());
            throw fnfe;
        }
        finally {
            input.close();
        }
        return properties;
    }

    public static void tallenna(String cda) {
        tallenna(cda, defaultFile);
    }

    public static void tallenna(String cda, String filename) {
        File file = new File(filename);
        Writer out;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.append(cda);
            out.flush();
            out.close();
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String lataa(String fileName) {
        if ( null == fileName || fileName.isEmpty() ) {
            fileName = defaultFile;
        }
        String retval = null;
        try {
            URL resourceStream = KantaCDATestUtils.class.getResource("/" + fileName);
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
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return retval;
    }

}
