package fi.kela.kanta.to;

import org.junit.Assert;
import org.junit.Test;

import fi.kela.kanta.cda.KantaCDAConstants.AsiakirjaVersioYhteensopivuus;

public class LaakemaaraysTOTest {

    @Test
    public void testIsTaaksepainYhteensopivaOk() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.TAAKSEPAIN);
        Assert.assertTrue(lm.isAsiakirjaTaaksepainYhteensopiva());
    }

    @Test
    public void testIsEteenpainYhteensopivaOk() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.ETEENPAIN);
        Assert.assertTrue(lm.isAsiakirjaEteenpainYhteensopiva());
    }

    @Test
    public void testIsKelainVersioOk() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO);
        Assert.assertTrue(lm.isAsiakirjaJarjestelmaVersio());
    }

    @Test
    public void testIsVersioTuettuOk() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO);
        Assert.assertTrue(lm.isAsiakirjaVersioTuettu());
    }

    @Test
    public void testIsTaaksepainYhteensopivaEiOle() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.ETEENPAIN);
        Assert.assertFalse(lm.isAsiakirjaTaaksepainYhteensopiva());
    }

    @Test
    public void testIsEteenpainYhteensopivaEiOle() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO);
        Assert.assertFalse(lm.isAsiakirjaEteenpainYhteensopiva());
    }

    @Test
    public void testIsKelainVersioEiOle() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.ETEENPAIN);
        Assert.assertFalse(lm.isAsiakirjaJarjestelmaVersio());
    }

    @Test
    public void testIsVersioTuettuEiOle() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(AsiakirjaVersioYhteensopivuus.EI_TUETTU);
        Assert.assertFalse(lm.isAsiakirjaVersioTuettu());
    }

    @Test
    public void testIsTaaksepainYhteensopivaNull() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(null);
        Assert.assertFalse(lm.isAsiakirjaTaaksepainYhteensopiva());
    }

    @Test
    public void testIsEteenpainYhteensopivaNull() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(null);
        Assert.assertFalse(lm.isAsiakirjaEteenpainYhteensopiva());
    }

    @Test
    public void testIsKelainVersioNull() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(null);
        Assert.assertFalse(lm.isAsiakirjaJarjestelmaVersio());
    }

    @Test
    public void testIsVersioTuettuNull() {
        LaakemaaraysTO lm = new LaakemaaraysTO();
        lm.setAsiakirjaYhteensopivuus(null);
        Assert.assertFalse(lm.isAsiakirjaVersioTuettu());
    }

    @Test
    public void testAnnosteluPelkastaanTekstimuodossa() {
        LaakemaaraysTO lm = new LaakemaaraysTO();

        // Oletuksena pitäisi olla vain tekstimuodossa, muuta ei vielä tueta
        Assert.assertTrue(lm.isAnnosteluPelkastaanTekstimuodossa());

    }
}
