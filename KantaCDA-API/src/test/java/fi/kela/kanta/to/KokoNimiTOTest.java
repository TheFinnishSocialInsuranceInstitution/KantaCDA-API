package fi.kela.kanta.to;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class KokoNimiTOTest {

    private static String etuNimi1 = "EtuNimi1";
    private static String etuNimi2 = "EtuNimi2";
    private static String etuNimi3 = "EtuNimi3";
    private static String etuNimi4 = "EtuNimi4";
    private static String etuNimiCat = "Ees Nimesid On Palju";
    private static String sukuNimi = "SukuNimi";
    private static String sukuNimi2 = "SukuNimi2";

    @Test
    public void testKokoNimiTOConstructor() {
        KokoNimiTO tested = new KokoNimiTO();
        Assert.assertEquals(0, tested.getNimet().size());

        tested = new KokoNimiTO(etuNimi1, sukuNimi);
        Assert.assertEquals(3, tested.getNimet().size());

        tested = new KokoNimiTO(etuNimi1, sukuNimi, new ArrayList<String>());
        Assert.assertEquals(3, tested.getNimet().size());

        tested = new KokoNimiTO(etuNimi1, sukuNimi, Arrays.asList(etuNimi2, etuNimi3, etuNimi4));
        Assert.assertEquals(6, tested.getNimet().size());
    }

    // lisaa(String, String, String)
    @Test
    public void testLisaa() {
        KokoNimiTO tested = new KokoNimiTO(etuNimi1, sukuNimi);
        tested.lisaa("given", "CL", etuNimi2);
        Assert.assertEquals(4, tested.getNimet().size());
    }

    // getNimet()
    @Test
    public void testGetNimet() {
        KokoNimiTO tested = new KokoNimiTO();
        Assert.assertNotNull(tested.getNimet());
    }

    // getKokoNimi()
    @Test
    public void testGetKokoNimi() {
        KokoNimiTO tested = new KokoNimiTO(etuNimi1, sukuNimi);
        Assert.assertEquals(sukuNimi + ", " + etuNimi1, tested.getKokoNimi());

        tested.lisaa("suffix", null, "Jr.");
        Assert.assertEquals(sukuNimi + ", " + etuNimi1 + ", Jr.", tested.getKokoNimi());

        tested.lisaa("family", null, sukuNimi2);
        tested.lisaa("suffix", null, "II");
        tested.lisaa("diipadaapaTyyppi", "diipadaapaMääre", "Veijo");
        Assert.assertEquals(sukuNimi + " " + sukuNimi2 + ", " + etuNimi1 + ", Jr. II", tested.getKokoNimi());
    }

    // getKutsumanimi()
    @Test
    public void testGetKutsumanimi() {
        KokoNimiTO tested = new KokoNimiTO(etuNimi1, sukuNimi);
        Assert.assertEquals(etuNimi1, tested.getKutsumanimi());

        tested.lisaa("given", "CL", etuNimi2);
        Assert.assertEquals(etuNimi1, tested.getKutsumanimi());
    }

    // getEtunimi()
    @Test
    public void testGetEtunimi() {
        KokoNimiTO tested = new KokoNimiTO(etuNimi1, sukuNimi);
        Assert.assertEquals(etuNimi1 + " " + etuNimi1, tested.getEtunimi());

        tested.lisaa("given", "CL", etuNimi2);
        Assert.assertEquals(etuNimi1 + " " + etuNimi1 + " " + etuNimi2, tested.getEtunimi());
    }

    // getEtunimi()
    @Test
    public void testGetEtunimiTokenization() {
        KokoNimiTO tested = new KokoNimiTO(etuNimiCat, sukuNimi);
        Assert.assertEquals("Ees", tested.getKutsumanimi());
        // CL:n kanssa on yhtä suurempi kuin "oikeiden" nimien lukumäärä
        Assert.assertEquals(6, tested.getNimet().size());
        Assert.assertEquals("SukuNimi, Ees Nimesid On Palju", tested.getKokoNimi());
    }

    // getSukunimi()
    @Test
    public void testGetSukunimi() {
        KokoNimiTO tested = new KokoNimiTO(etuNimi1, sukuNimi);
        Assert.assertEquals(sukuNimi, tested.getSukunimi());

        tested.lisaa("family", null, sukuNimi2);
        Assert.assertEquals(sukuNimi + " " + sukuNimi2, tested.getSukunimi());
    }
}
