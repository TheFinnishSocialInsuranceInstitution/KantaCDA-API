package fi.kela.kanta.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fi.kela.kanta.cda.KantaCDATestUtils;
import fi.kela.kanta.cda.KantaCDAConstants.AsiakirjaVersioYhteensopivuus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AsiakirjaVersioUtil.class })
public class AsiakirjaVersioUtilTest {

    Properties props;

    @Before
    public void alusta() {
        try {
            props = KantaCDATestUtils.loadProperties("testi_properties.properties");
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testGetKelainAsiakirjaVersio() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        String jarjestelmaVersio = tested.getJarjestelmaAsiakirjaVersio();
        Assert.assertNotNull(jarjestelmaVersio);
        Assert.assertEquals("1.2.246.777.11.2015.11", jarjestelmaVersio);
    }

    @Test
    public void testGetAsiakirjaVersionYhteensopivuusTaaksepain() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        AsiakirjaVersioYhteensopivuus sopivuus = tested.getAsiakirjaVersionYhteensopivuus("1.2.246.777.11.2011.17");
        Assert.assertNotNull(sopivuus);
        Assert.assertEquals(AsiakirjaVersioYhteensopivuus.TAAKSEPAIN, sopivuus);
    }

    @Test
    public void testGetAsiakirjaVersionYhteensopivuusEteenpain() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        AsiakirjaVersioYhteensopivuus sopivuus = tested.getAsiakirjaVersionYhteensopivuus("1.2.246.777.11.2016.2");
        Assert.assertNotNull(sopivuus);
        Assert.assertEquals(AsiakirjaVersioYhteensopivuus.ETEENPAIN, sopivuus);
    }

    @Test
    public void testGetAsiakirjaVersionYhteensopivuusKelainVersio() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        AsiakirjaVersioYhteensopivuus sopivuus = tested
                .getAsiakirjaVersionYhteensopivuus(tested.getJarjestelmaAsiakirjaVersio());
        Assert.assertNotNull(sopivuus);
        Assert.assertEquals(AsiakirjaVersioYhteensopivuus.JARJESTELMA_VERSIO, sopivuus);
    }

    @Test
    public void testGetAsiakirjaVersionYhteensopivuusEiTuettu() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        AsiakirjaVersioYhteensopivuus sopivuus = tested.getAsiakirjaVersionYhteensopivuus("1111");
        Assert.assertNotNull(sopivuus);
        Assert.assertEquals(AsiakirjaVersioYhteensopivuus.EI_TUETTU, sopivuus);
    }

    @Test
    public void testGetAsiakirjaVersiot() {
        AsiakirjaVersioUtil tested = new AsiakirjaVersioUtil(props);
        List<String> versiot = tested.getAsiakirjaVersiot();
        Assert.assertNotNull(versiot);
        Assert.assertTrue(versiot.size() == 8);
    }
}
