/*******************************************************************************
 * Copyright 2017 Kansaneläkelaitos
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package fi.kela.kanta.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

    public String marshalloi(Object o) throws JAXBException {
        return marshalloi(o, null);
    }

    public String marshalloi(Object o, String schemaLocation) throws JAXBException {
        LOGGER.entry();

        long start = 0;
        if ( LOGGER.isDebugEnabled() ) {
            start = System.currentTimeMillis();
        }

        StringWriter out = new StringWriter();
        String s = null;
        try {
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            if ( schemaLocation != null ) {
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
            }
            m.marshal(o, out);
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

        return LOGGER.exit(s);
    }

    public POCDMT000040ClinicalDocument unmarshaller(String xml) throws JAXBException {
        LOGGER.entry();

        long start = 0;
        if ( LOGGER.isDebugEnabled() ) {
            start = System.currentTimeMillis();
        }

        POCDMT000040ClinicalDocument result = null;
        try {
            StringReader reader = new StringReader(xml);
            Unmarshaller u = jaxbContext.createUnmarshaller();
            result = (POCDMT000040ClinicalDocument) u.unmarshal(reader);
        }
        catch (JAXBException e) {
            LOGGER.error("UnMarshallointi epäonnistui.", e.getMessage());
            throw e;
        }

        if ( LOGGER.isDebugEnabled() ) {
            long end = System.currentTimeMillis();
            LOGGER.debug("Unmarshalling took " + (end - start) + " ms.");
        }

        return LOGGER.exit(result);
    }

}
