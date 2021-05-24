<!--
  Copyright 2020 Kansaneläkelaitos
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License.  You may obtain a copy
  of the License at
  
    http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
  License for the specific language governing permissions and limitations under
  the License.
-->
package fi.kela.kanta.cda;

import java.math.BigInteger;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.hl7.v3.ActClinicalDocument;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.XActRelationshipDocument;

import fi.kela.kanta.cda.validation.Validoija;

public abstract class ArkistoKasaaja extends Kasaaja {

    protected static final String template_id = "templateId";
    protected static final String code = "%s.code";
    protected static final String title = "%s.title";
    protected static final String code_title = "%s.code.title";
    protected static final String code_system_name = "%s.code.codeSystemName";
    protected static final String code_display_name = "%s.code.displayName";
    protected static final String code_ = "%s.title";
    
	protected Validoija validoija;
	
	public ArkistoKasaaja(Properties properties) {
		super(properties);
		// Add some text here
	}
	
	protected void validoiAsiakirja() {
		validoija.validoi();
	}

	/**
     * Palauttaa CDA-asiakirjan, perustuen asiakirjakohtaisen kasaajan konstruktorissa annettuun TO:hon
     * 
     * @return CDA-asiakirja
     */
    abstract public String kasaaAsiakirja() throws JAXBException;
    
    /**
     * Palauttaa CDA-asiakirjan, perustuen asiakirjakohtaisen kasaajan konstruktorissa annettuun TO:hon
     * 
     * @return CDA-asiakirjan tiedot POCDMT00040ClinicalDocumentissä
     */
    abstract public POCDMT000040ClinicalDocument kasaaCDA();

	/* (non-Javadoc)
	 * @see fi.kela.kanta.cda.Kasaaja#getTypeKey()
	 */
	@Override
	protected String getTypeKey() {
		return Kasaaja.ARKISTO_PROPERTY_PREFIX;
	}

	/**
     * Lisää clinicalDocumentiin relatedDocument rakenteen johon sijoitetaan annetut oid ,setId ja versioNumber
     * <relatedDocument typeCode="RPLC/APND/..."> <parentDocument classCode="DOCCLIN" moodCode="EVN">
     * <id root="[oid]"/>  <setId root="[setId]"/> <versionNumber value="[version]" />
     * </parentDocument> </relatedDocument>
     *
     * @param clinicalDocument
     *            POCDMT00040ClinicalDocument johon relatedDocument elementti lisätään
     * @param oid
     *            String alkuperäisen dokumentin oid
     * @param setid
     *            String alkuperäisen dokumentin setId
     * @param version
     *            int alkuperäisen asiakirjan versionumero
     * @param relationType
     *            Relaation tyyppikoodi (RPLC korjaus)
     */
	protected void addRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument, String oid, String setid,
			int version, XActRelationshipDocument relationType) {
		POCDMT000040RelatedDocument relatedDocument = of.createPOCDMT000040RelatedDocument();

        relatedDocument.setTypeCode(relationType);
        relatedDocument.setParentDocument(of.createPOCDMT000040ParentDocument());
        relatedDocument.getParentDocument().setClassCode(ActClinicalDocument.DOCCLIN);
        relatedDocument.getParentDocument().getMoodCodes().add("EVN");
        relatedDocument.getParentDocument().getIds().add(of.createII());
        relatedDocument.getParentDocument().getIds().get(0).setRoot(oid);
        relatedDocument.getParentDocument().setSetId(of.createII());
        relatedDocument.getParentDocument().getSetId().setRoot(setid);
        relatedDocument.getParentDocument().setVersionNumber(of.createINT());
        relatedDocument.getParentDocument().getVersionNumber().setValue(BigInteger.valueOf(version));
        clinicalDocument.getRelatedDocuments().add(relatedDocument);
	}
}
