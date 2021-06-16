package fi.kela.kanta.cda;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040StructuredBody;

import org.apache.commons.configuration.ConfigurationException;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptiNayttomuodonPurkaja extends Purkaja {

    @Override
    protected String getCodeSystem() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReseptiNayttomuodonPurkaja() throws ConfigurationException {
        super();
    }

    /**
     * Purkaa lääkemääräyksen näyttömuodon tiedot LaakemaaraysTO luokkaan
     * 
     * @param String
     *            cda josta tiedot puretaan
     * @return LaakemaaraysTO johon näyttömuodon tiedot on sijoitetaan
     * @throws PurkuException
     */
    public LaakemaaraysTO puraLaakemaarays(String cda) throws PurkuException {
        try {
            POCDMT000040ClinicalDocument clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
            LaakemaaraysTO laakemaarays = luoLaakemaaraysTO(clinicalDocument);
            puraLeimakentat(clinicalDocument, laakemaarays);
            tarkistaAsiakirjaVersio(clinicalDocument, laakemaarays);
            POCDMT000040Component3 comp = getComponent(clinicalDocument.getComponent().getStructuredBody());
            puraSection(comp.getSection(), laakemaarays);
            return laakemaarays;
        } catch (JAXBException e) {
            throw new PurkuException(e);
        }
    }

    private void puraSection(POCDMT000040Section section, LaakemaaraysTO laakemaarays) {
        if ( section == null ) {
            return;
        }
        puraText(section, laakemaarays.getNayttomuoto());
        List<POCDMT000040Component5> components = section.getComponents();
        if ( components != null && !components.isEmpty() ) {
            for (POCDMT000040Component5 comp : components) {
                puraSection(comp.getSection(), laakemaarays);
            }
        }
    }

    private POCDMT000040Component3 getComponent(POCDMT000040StructuredBody body) {
        if ( body.getComponents() != null && !body.getComponents().isEmpty() ) {
            return body.getComponents().get(0);
        }
        return null;
    }
}
