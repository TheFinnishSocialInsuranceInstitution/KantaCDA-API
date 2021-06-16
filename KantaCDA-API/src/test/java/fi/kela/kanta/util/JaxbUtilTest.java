package fi.kela.kanta.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.hl7.v3.ObjectFactory;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.junit.Assert;
import org.junit.Test;

public class JaxbUtilTest {

    private final String testDataXML = "<ClinicalDocument xmlns=\"urn:hl7-org:v3\" xmlns:ns2=\"urn:hl7finland\">\n" //
            + "    <id root=\"1.2.246.556.10.100000.1.1\"/>\n" //
            + "</ClinicalDocument>";

    @Test
    public void unmarshallerTest() throws JAXBException, IOException, SOAPException {
        POCDMT000040ClinicalDocument result = JaxbUtil.getInstance().unmarshaller(testDataXML);

        Assert.assertEquals("1.2.246.556.10.100000.1.1", result.getId().getRoot());
    }

    @Test
    public void marshallerTest() throws UnsupportedEncodingException, JAXBException {

        ObjectFactory factory = new ObjectFactory();
        POCDMT000040ClinicalDocument input = factory.createPOCDMT000040ClinicalDocument();
        input.setId(factory.createII());
        input.getId().setRoot("1.2.246.556.10.100000.1.1");

        String vastaus = JaxbUtil.getInstance().marshalloi(input);

        Assert.assertTrue(vastaus.contains("ClinicalDocument xmlns"));
        Assert.assertTrue(vastaus.contains("ClinicalDocument>"));
        Assert.assertTrue(vastaus.contains("\"urn:hl7-org:v3\""));
        Assert.assertTrue(vastaus.contains("id root=\"1.2.246.556.10.100000.1.1\"/>"));
    }
}
