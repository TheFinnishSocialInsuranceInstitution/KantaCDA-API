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
package fi.kela.kanta.cda;

import org.hl7.v3.ED;
import org.hl7.v3.POCDMT000040Act;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040ParentDocument;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.XActRelationshipDocument;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.LaakemaarayksenVarausTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinVarauksenPurkaja extends ReseptiPurkaja {

    @Override
    protected String getCodeSystem() {
        return "1.2.246.537.5.40105.2006";
    }

    public ReseptinVarauksenPurkaja() throws ConfigurationException {
        super();
    }

    /**
     * Pura Reseptin varausasiakirja (purkaa asiakirjan - ei pura varausta..)
     * 
     * @param cda Reseptin varausasiakirja XML-muodossa
     * @return Varausasiakirjan tiedot
     * @throws Exception
     */
    public LaakemaarayksenVarausTO puraReseptinVaraus(String cda) throws PurkuException {
        if ( cda == null ) {
            return null;
        }
        try {
            POCDMT000040ClinicalDocument clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
            LaakemaarayksenVarausTO varaus = new LaakemaarayksenVarausTO();
            puraLeimakentat(clinicalDocument, varaus);
            puraAuthor(clinicalDocument, varaus);
            puraComponentOf(clinicalDocument, varaus);
            puraRelatedDocument(clinicalDocument, varaus);
            puraVarauksenSelitys(clinicalDocument, varaus);
            return varaus;
        } catch (JAXBException e) {
            throw new PurkuException(e);
        }
    }

    /**
     * Purkaa headeristä relatedDocument tiedot
     * 
     * @param clinicalDocument
     * @param varaus
     */
    private void puraRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument, LaakemaarayksenVarausTO varaus) {
        if ( clinicalDocument == null ) {
            return;
        }
        for (POCDMT000040RelatedDocument relatedDocument : clinicalDocument.getRelatedDocuments()) {
            if ( relatedDocument.getTypeCode() == XActRelationshipDocument.APND ) {
                POCDMT000040ParentDocument parentDocument = relatedDocument.getParentDocument();
                varaus.setLaakemaarayksenYksilointitunnus(parentDocument.getIds().get(0).getRoot());
            }
        }
    }

    /**
     * Purkaa varuksen selityksen
     * 
     * @param clinicalDocument
     * @param varaus
     */
    private void puraVarauksenSelitys(POCDMT000040ClinicalDocument clinicalDocument, LaakemaarayksenVarausTO varaus) {
        POCDMT000040Component3 component3 = clinicalDocument.getComponent().getStructuredBody().getComponents().get(0);
        POCDMT000040Component5 component5a = component3.getSection().getComponents().get(0);
        POCDMT000040Component5 component5b = component5a.getSection().getComponents().get(0);
        POCDMT000040Section section = component5b.getSection();
        if ( !section.getEntries().isEmpty() ) {
            POCDMT000040Entry entry = section.getEntries().get(0);
            if ( entry != null && entry.getAct() != null && entry.getAct().getText() != null
                    && !entry.getAct().getText().getContent().isEmpty() ) {
                POCDMT000040Act act = entry.getAct();
                ED text = act.getText();
                varaus.setSelitys((String) text.getContent().get(0));
            }
        }
    }

}
