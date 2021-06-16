package fi.kela.kanta.cda;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040ClinicalDocument;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.AmmattihenkiloTO;
import fi.kela.kanta.to.UusimispyyntoTO;
import fi.kela.kanta.util.JaxbUtil;

public class UusimispyyntoPurkaja extends ReseptiPurkaja {

    @Override
    protected String getCodeSystem() {
        return "1.2.246.537.5.40105.2006";
    }

    public UusimispyyntoPurkaja() throws ConfigurationException {
        super();
    }

    public UusimispyyntoTO puraUusimispyynto(String cda) throws PurkuException {
        if ( null == cda ) {
            return null;
        }
        try {
            POCDMT000040ClinicalDocument clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
            UusimispyyntoTO up = new UusimispyyntoTO();
            puraLeimakentat(clinicalDocument, up);
            puraAuthor(clinicalDocument, up);
            puraPotilas(clinicalDocument, up);
            puraComponentOf(clinicalDocument, up);
            return up;
        } catch (JAXBException e) {
            throw new PurkuException(e);
        }
    }

    @Override
    protected AmmattihenkiloTO luoAmmattihenkilo(POCDMT000040Author author) throws PurkuException {
        if ( !author.getNullFlavors().isEmpty() ) { // Omakannan uusimispyynnöissä ei ole author-tietoa
            return null;
        }
        else {
            return super.luoAmmattihenkilo(author);
        }

    }

}
