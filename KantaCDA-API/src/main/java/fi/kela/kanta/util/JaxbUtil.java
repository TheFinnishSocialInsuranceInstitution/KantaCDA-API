package fi.kela.kanta.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.v3.POCDMT000040ClinicalDocument;

public class JaxbUtil {

    private static final Logger LOGGER = LogManager.getLogger(JaxbUtil.class);

    private JAXBContext jaxbContext = null;

    private JaxbUtil() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
    }

    private static JaxbUtil instance = null;

    public static JaxbUtil getInstance() throws JAXBException {
        if ( instance == null ) {
            instance = new JaxbUtil();
        }
        return instance;
    }

    public String marshalloi(POCDMT000040ClinicalDocument clinicalDocument) throws JAXBException {
        return marshalloi(clinicalDocument, null);
    }

    public String marshalloi(POCDMT000040ClinicalDocument clinicalDocument, String schemaLocation)
            throws JAXBException {
        long start = 0;
        if ( LOGGER.isDebugEnabled() ) {
            start = System.currentTimeMillis();
        }

        StringWriter out = new StringWriter();
        String s = null;
        try {
            JAXBElement<POCDMT000040ClinicalDocument> jaxbElement = new JAXBElement<POCDMT000040ClinicalDocument>(
                    new QName("urn:hl7-org:v3", "ClinicalDocument"), POCDMT000040ClinicalDocument.class,
                    clinicalDocument);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            if ( schemaLocation != null ) {
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
            }
            m.marshal(jaxbElement, out);
            s = out.toString();
        }
        catch (JAXBException e) {
            LOGGER.error("Marshallointi epäonnistui.", e);
            throw e;
        }

        if ( LOGGER.isDebugEnabled() ) {
            long end = System.currentTimeMillis();
            LOGGER.debug("Unmarshalling took " + (end - start) + " ms.");
        }

        return s;
    }
    
    public String arkistomarshalloi(POCDMT000040ClinicalDocument clinicalDocument, String schemaLocation)
            throws JAXBException {
        long start = 0;
        if ( LOGGER.isDebugEnabled() ) {
            start = System.currentTimeMillis();
        }

        StringWriter out = new StringWriter();
        String s = null;
        try {
            JAXBElement<POCDMT000040ClinicalDocument> jaxbElement = new JAXBElement<POCDMT000040ClinicalDocument>(
                    new QName("urn:hl7-org:v3", "ClinicalDocument"), POCDMT000040ClinicalDocument.class,
                    clinicalDocument);
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new ArkistoNamespacePrefixMapper());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            if ( schemaLocation != null ) {
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
            }
            m.marshal(jaxbElement, out);
            s = out.toString();
        }
        catch (JAXBException e) {
            LOGGER.error("Marshallointi epäonnistui.", e);
            throw e;
        }

        if ( LOGGER.isDebugEnabled() ) {
            long end = System.currentTimeMillis();
            LOGGER.debug("Unmarshalling took " + (end - start) + " ms.");
        }

        return s;
    }

    public POCDMT000040ClinicalDocument unmarshaller(String xml) throws JAXBException {
        long start = 0;
        if ( LOGGER.isDebugEnabled() ) {
            start = System.currentTimeMillis();
        }

        POCDMT000040ClinicalDocument result = null;
        try {
            StringReader reader = new StringReader(xml);
            Unmarshaller u = jaxbContext.createUnmarshaller();
            result = u.unmarshal(new StreamSource(reader), POCDMT000040ClinicalDocument.class).getValue();
        }
        catch (JAXBException e) {
            LOGGER.error("UnMarshallointi epäonnistui.", e.getMessage());
            throw e;
        }

        if ( LOGGER.isDebugEnabled() ) {
            long end = System.currentTimeMillis();
            LOGGER.debug("Unmarshalling took " + (end - start) + " ms.");
        }

        return result;
    }

}
