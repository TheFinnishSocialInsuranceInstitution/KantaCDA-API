package fi.kela.kanta.cda;

import org.junit.Assert;
import org.junit.Test;

import fi.kela.kanta.to.UusimispyyntoTO;

public class UusimispyyntoPurkajaCDATest {

    private static final String testUusimispyyntoOmakantaXMLFile = "up_omakanta.xml";
    private static final String testUusimispyyntoKelainXMLFile = "up_Kelain.xml";

    @Test
    public void testPuraUusimispyyntoOmakanta() throws Exception {
        String cda = KantaCDATestUtils.lataa(testUusimispyyntoOmakantaXMLFile);
        UusimispyyntoPurkaja purkaja = new UusimispyyntoPurkaja();
        UusimispyyntoTO up = purkaja.puraUusimispyynto(cda);
        Assert.assertNotNull(up);
        Assert.assertNull(up.getAmmattihenkilo());
        Assert.assertNotNull(up.getPotilas());
    }

    @Test
    public void testPuraUusimispyynto() throws Exception {
        String cda = KantaCDATestUtils.lataa(testUusimispyyntoKelainXMLFile);
        UusimispyyntoPurkaja purkaja = new UusimispyyntoPurkaja();
        UusimispyyntoTO up = purkaja.puraUusimispyynto(cda);
        Assert.assertNotNull(up);
        Assert.assertNotNull(up.getAmmattihenkilo());
        Assert.assertNotNull(up.getPotilas());
    }
}
