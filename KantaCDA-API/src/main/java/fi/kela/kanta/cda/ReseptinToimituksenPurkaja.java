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

import org.hl7.v3.BL;
import org.hl7.v3.CE;
import org.hl7.v3.INT;
import org.hl7.v3.MO;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Component4;
import org.hl7.v3.POCDMT000040Component5;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040EntryRelationship;
import org.hl7.v3.POCDMT000040Observation;
import org.hl7.v3.POCDMT000040Organizer;
import org.hl7.v3.POCDMT000040ParentDocument;
import org.hl7.v3.POCDMT000040RelatedDocument;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040Supply;
import org.hl7.v3.PQ;
import org.hl7.v3.XActRelationshipDocument;

import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;

import fi.kela.kanta.exceptions.PurkuException;
import fi.kela.kanta.to.LaakemaarayksenToimitusTO;
import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.JaxbUtil;

public class ReseptinToimituksenPurkaja extends ReseptiPurkaja {

    @Override
    protected String getCodeSystem() {
        return "1.2.246.537.5.40105.2006.10";
    }

    public ReseptinToimituksenPurkaja() throws ConfigurationException {
        super();
    }

    /**
     * Purkaa Lääkemääräyksen toimitus cda:n tiedot LaakemaarayksenToimitusTO:n
     * 
     * @param cda
     *            String Lääkemääräyksen toimitus asiakirja
     * @return LaakemaarayksenToimitusTO johon asiakirjan tiedot on sijoitettu
     * @throws PurkuException
     */
    public LaakemaarayksenToimitusTO puraLaakemaarayksenToimitus(String cda) throws PurkuException {
        if ( null == cda ) {
            return null;
        }
        try {
            POCDMT000040ClinicalDocument clinicalDocument = JaxbUtil.getInstance().unmarshaller(cda);
            LaakemaarayksenToimitusTO toimitus = new LaakemaarayksenToimitusTO();

            puraLeimakentat(clinicalDocument, toimitus);
            puraAuthor(clinicalDocument, toimitus);
            puraComponentOf(clinicalDocument, toimitus);
            puraRelatedDocument(clinicalDocument, toimitus);
            puraToimitusTiedot(clinicalDocument, toimitus);
            return toimitus;
        }
        catch (JAXBException e) {
            throw new PurkuException(e);
        }
    }

    /**
     * Purkaa headeristä relatedDocument tiedot
     * 
     * @param clinicalDocument
     * @param toimitus
     */
    private void puraRelatedDocument(POCDMT000040ClinicalDocument clinicalDocument,
            LaakemaarayksenToimitusTO toimitus) {
        if ( null == clinicalDocument ) {
            return;
        }
        for (POCDMT000040RelatedDocument relatedDocument : clinicalDocument.getRelatedDocuments()) {
            if ( relatedDocument.getTypeCode() == XActRelationshipDocument.APND ) {
                POCDMT000040ParentDocument parentDocument = relatedDocument.getParentDocument();
                toimitus.setLaakemaarayksenYksilointitunnus(parentDocument.getIds().get(0).getRoot());
            }
        }

    }

    /**
     * Purkaa lääkemääräyksen toimituskohtaiset tiedot
     * 
     * @param clinicalDocument
     *            POCDMT00040ClinicalDocument josta tiedot puretaan
     * @param toimitus
     *            LaakemaareyksenToimitusTO johon tiedot sijoitetaan
     * @throws Exception
     */
    private void puraToimitusTiedot(POCDMT000040ClinicalDocument clinicalDocument, LaakemaarayksenToimitusTO toimitus)
            throws PurkuException {
        POCDMT000040Component3 component3 = clinicalDocument.getComponent().getStructuredBody().getComponents().get(0);
        POCDMT000040Component5 component5a = component3.getSection().getComponents().get(0);
        POCDMT000040Component5 component5b = component5a.getSection().getComponents().get(0);
        puraEntryt(component5b.getSection(), toimitus);
    }

    /**
     * Purkaa laakemaaräyksen toimituksen entry tiedot laakemaarayksentoimitusTO:n
     * 
     * @param section
     * @param toimitus
     * @throws Exception
     */
    private void puraEntryt(POCDMT000040Section section, LaakemaarayksenToimitusTO toimitus) throws PurkuException {
        for (POCDMT000040Entry entry : section.getEntries()) {
            if ( null != entry.getOrganizer() && null != entry.getOrganizer().getCode()
                    && null != entry.getOrganizer().getCode().getCode() ) {
                String code = entry.getOrganizer().getCode().getCode();
                if ( KantaCDAConstants.Laakityslista.LAAKEVALMISTEEN_JA_PAKKAUKSEN_TIEDOT_TOIMITUSSANOMASSA
                        .equals(code) ) {
                    puraLaakevalmisteenJaPakkauksenTiedot(entry.getOrganizer().getComponents().get(0), toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.TOIMITUKSEN_MUUT_TIEDOT.equals(code) ) {
                    puraToimituksenMuutTiedot(entry.getOrganizer(), toimitus);
                }
            }
        }
    }

    /**
     * Purkaa Lääkemääräyksen toimituksen muut tiedot
     * 
     * @param organizer
     * @param toimitus
     */
    private void puraToimituksenMuutTiedot(POCDMT000040Organizer organizer, LaakemaarayksenToimitusTO toimitus) {
        for (POCDMT000040Component4 component : organizer.getComponents()) {
            if ( null != component.getObservation() && null != component.getObservation().getCode()
                    && null != component.getObservation().getCode().getCode() ) {
                POCDMT000040Observation observation = component.getObservation();
                String code = observation.getCode().getCode();
                if ( KantaCDAConstants.Laakityslista.TOIMITUKSEN_HINTA.equals(code) ) {
                    puraToimituksenHinta(observation, toimitus);
                    // poimi hinta
                }
                else if ( KantaCDAConstants.Laakityslista.TOIMITETTU_HINTAPUTKEEN_KUULUMATONTA_LAAKETTA.equals(code) ) {
                    puraLaakevaihtokielto(observation, toimitus);
                    // poimi syy miksi toimitettu hintaputkeen kuulumatonta laaketta
                }
                else if ( KantaCDAConstants.Laakityslista.OMAVASTUUOSUUKSIEN_LUKUMAARA.equals(code) ) {
                    // poimi omavastuuosuuksien lukumäärä
                    puraOmavastuuosuuksienLukumaara(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.KOKONAAN_TOIMITETTU.equals(code) ) {
                    // poimi onko kokonaan toimitettu
                    puraKokonaanToimitettuTieto(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.LAAKE_VAIHDETTU.equals(code) ) {
                    puraLaakeVaihdettuTieto(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.ANNOSJAKELU.equals(code) ) {
                    puraToimituksenAnnosjakelu(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.APTEEKIN_HUOMAUTUS.equals(code) ) {
                    puraApteekinHuomautus(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.HUUMAUSAINE_PKV_LAAKEMAARAYS.equals(code) ) {
                    puraHuumePKVLaakemaaraysTieto(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.HUUME.equals(code) ) {
                    puraHuumausaineTieto(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.APTEEKIN_AUKIKIRJOITTAMA_ANNOSTUSOHJE.equals(code) ) {
                    puraAnnostusohje(observation, toimitus);
                }
                else if ( KantaCDAConstants.Laakityslista.LISASELVITYS_KELALLE.equals(code) ) {
                    puraLisaselvitysKelalle(observation, toimitus);
                }
            }
        }

    }

    /**
     * Purkaa POCDMT00040Observationista kelalle annettavan lisäselvityksen purettu tieto sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraLisaselvitysKelalle(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            if ( observation.getValues().get(0).getNullFlavors().isEmpty() || !observation.getValues().get(0)
                    .getNullFlavors().get(0).equals(KantaCDAConstants.NullFlavor.NI.getCode()) ) {
                toimitus.setLisaselvitysKelalle(puraContent(observation.getValues().get(0)));
            }
        }
    }

    /**
     * Purkaa POCDMT00040Observationista annostusohjeen annostusohje sijoitetaan annettuun LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraAnnostusohje(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            if ( observation.getValues().get(0).getNullFlavors().isEmpty() || !observation.getValues().get(0)
                    .getNullFlavors().get(0).equals(KantaCDAConstants.NullFlavor.NI.getCode()) ) {
                toimitus.setAnnostusohje(puraContent(observation.getValues().get(0)));
            }
        }
    }

    /**
     * Purkaa POCDMT00040Observationista tiedon onko kyseessä huumausaine purettu tieto sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraHuumausaineTieto(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            toimitus.setHuume(((BL) observation.getValues().get(0)).isValue());
        }
    }

    /**
     * Purkaa POCDMT00040Observationista PKV- ja Huumausaine tiedon purettu tieto sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraHuumePKVLaakemaaraysTieto(POCDMT000040Observation observation,
            LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            toimitus.setPKVlaakemaarays(((CE) observation.getValues().get(0)).getCode());
        }
    }

    /**
     * Purkaa POCDMT00040Observationista apteekin tekemän huomautuksen purettu tieto sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraApteekinHuomautus(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            if ( observation.getValues().get(0).getNullFlavors().isEmpty() || !observation.getValues().get(0)
                    .getNullFlavors().get(0).equals(KantaCDAConstants.NullFlavor.NI.getCode()) ) {
                toimitus.setApteekinHuomautus(puraContent(observation.getValues().get(0)));
            }
        }
    }

    /**
     * Purkaa POCDMT00040Observationista tiedon onko lääke vaihdettu toimituksen yhteydessä purettu tieto sijoitetaan
     * annettuun LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraLaakeVaihdettuTieto(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            toimitus.setLaakeVaihdettu(((BL) observation.getValues().get(0)).isValue());
        }
    }

    /**
     * Purkaa POCDMT00040Observationista tiedon onko lääkemääräys kokonaan toimitettu purettu tieto sijoitetaan
     * annettuun LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraKokonaanToimitettuTieto(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            toimitus.setKokonaanToimitettu(((BL) observation.getValues().get(0)).isValue());
        }
    }

    /**
     * Purkaa POCDMT00040Observationista toimituksen omavastuuosuuksien tiedot puretut tiedot sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraOmavastuuosuuksienLukumaara(POCDMT000040Observation observation,
            LaakemaarayksenToimitusTO toimitus) {
        if ( !observation.getValues().isEmpty() ) {
            toimitus.setOmavastuuosuuksienLukumaara(((INT) observation.getValues().get(0)).getValue().intValue());
        }
    }

    /**
     * Purkaa POCDMT00040Observationista toimituksen annosjakelu tiedot puretut tiedot sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraToimituksenAnnosjakelu(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( null != observation && !observation.getValues().isEmpty() ) {
            toimitus.setAnnosjakelu(((BL) observation.getValues().get(0)).isValue());
            if ( null != observation.getText() ) {
                toimitus.setAnnosjakeluTeksti(puraContent(observation.getText()));
            }
        }

    }

    /**
     * Purkaa POCDMT00040Observationista lääkevaihtokiellon syyn ja lisäselvityksen puretut tiedot sijoitetaan annettuun
     * LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraLaakevaihtokielto(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( null != observation && !observation.getValues().isEmpty() ) {
            CE syy = (CE) observation.getValues().get(0);
            toimitus.setLaakevaihtokiellonSyy(syy.getCode());
            if ( null != observation.getText() ) {
                toimitus.setLaakevaihtokiellonLisaselvitys(puraContent(observation.getText()));
            }
        }

    }

    /**
     * Purkaa POCDMT00040Observationista lääketoimituksen hinta tiedot (yksikkö ja määrä) puretut tiedot sijoitetaan
     * annettuun LaakemaarayksenToimitusTO:n
     * 
     * @param observation
     * @param toimitus
     */
    private void puraToimituksenHinta(POCDMT000040Observation observation, LaakemaarayksenToimitusTO toimitus) {
        if ( null != observation && !observation.getValues().isEmpty() ) {
            MO hinta = (MO) observation.getValues().get(0);
            toimitus.setToimituksenHintaValue(Double.parseDouble(hinta.getValue()));
            toimitus.setToimituksenHintaUnit(hinta.getCurrency());
        }
    }

    /**
     * Purkaa toimitetun kokonaismäärän ja yksikön annetun POCDMT00040Observationin valuesta
     * 
     * @param observation
     * @param laakemaarays
     */
    private void puraToimitettuMaara(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
        if ( !(laakemaarays instanceof LaakemaarayksenToimitusTO) || null == observation ) {
            return;
        }
        LaakemaarayksenToimitusTO toimitus = (LaakemaarayksenToimitusTO) laakemaarays;
        if ( !observation.getValues().isEmpty() ) {
            PQ toimitettuMaara = (PQ) observation.getValues().get(0);
            toimitus.setToimitettuKokonaismaaraUnit(toimitettuMaara.getUnit());
            if ( null != toimitettuMaara.getValue() ) {
                toimitus.setToimitettuKokonaismaaraValue(Integer.parseInt(toimitettuMaara.getValue()));
            }
            if ( !toimitettuMaara.getTranslations().isEmpty()
                    && null != toimitettuMaara.getTranslations().get(0).getOriginalText() ) {
                toimitus.setToimitettuKokonaismaaraOriginal(
                        puraContent(toimitettuMaara.getTranslations().get(0).getOriginalText()));
            }
        }
        if ( null != observation.getText() ) {
            toimitus.setToimitettuKokonaismaaraText(puraContent(observation.getText()));
        }

    }

    /**
     * Purkaa jäljellä olevan määrän annetusta Observationista
     * 
     * @param observation
     * @param laakemaarays
     */
    private void puraJaljellaOlevaMaara(POCDMT000040Observation observation, LaakemaaraysTO laakemaarays) {
        if ( !(laakemaarays instanceof LaakemaarayksenToimitusTO) || null == observation ) {
            return;
        }
        LaakemaarayksenToimitusTO toimitus = (LaakemaarayksenToimitusTO) laakemaarays;
        if ( !observation.getValues().isEmpty() ) {
            PQ jaljellaOlevaMaara = (PQ) observation.getValues().get(0);
            toimitus.setJaljellaOlevaMaaraUnit(jaljellaOlevaMaara.getUnit());
            if ( null != jaljellaOlevaMaara.getValue() ) {
                toimitus.setJaljellaOlevaMaaraValue(Integer.parseInt(jaljellaOlevaMaara.getValue()));
            }
            if ( !jaljellaOlevaMaara.getTranslations().isEmpty()
                    && null != jaljellaOlevaMaara.getTranslations().get(0).getOriginalText() ) {
                toimitus.setJaljellaOlevaMaaraOriginal(
                        puraContent(jaljellaOlevaMaara.getTranslations().get(0).getOriginalText()));
            }
        }
        if ( null != observation.getText() ) {
            toimitus.setJaljellaOlevaMaaraText(puraContent(observation.getText()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.kela.kanta.cda.ReseptiPurkaja#puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(org.hl7.v3.
     * POCDMT000040EntryRelationship, fi.kela.kanta.to.LaakemaaraysTO)
     */
    @Override
    protected void puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(
            POCDMT000040EntryRelationship entryRelationsip, LaakemaaraysTO laakemaarays) {
        // super.puraAsiakirjakohtaisetValmisteenJapakkauksenEntryRelationshipit(entryRelationsip, laakemaarays);
        if ( null != entryRelationsip && null != entryRelationsip.getObservation()
                && null != entryRelationsip.getObservation().getCode()
                && null != entryRelationsip.getObservation().getCode().getCode() ) {
            String observationCode = entryRelationsip.getObservation().getCode().getCode();
            if ( KantaCDAConstants.Laakityslista.TOIMITETTU_MAARA.equals(observationCode) ) {
                puraToimitettuMaara(entryRelationsip.getObservation(), laakemaarays);
            }
            else if ( KantaCDAConstants.Laakityslista.JALJELLA_OLEVA_MAARA.equals(observationCode) ) {
                puraJaljellaOlevaMaara(entryRelationsip.getObservation(), laakemaarays);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.kela.kanta.cda.ReseptiPurkaja#puraAsiakirjakohtaisetKokoJaMaaraTiedot(org.hl7.v3.POCDMT000040Supply,
     * fi.kela.kanta.to.LaakemaaraysTO)
     */
    @Override
    protected void puraAsiakirjakohtaisetKokoJaMaaraTiedot(POCDMT000040Supply supply, LaakemaaraysTO laakemaarays) {
        if ( null == supply || !(laakemaarays instanceof LaakemaarayksenToimitusTO) ) {
            return;
        }
        LaakemaarayksenToimitusTO toimitus = (LaakemaarayksenToimitusTO) laakemaarays;
        if ( null != supply.getRepeatNumber() && null != supply.getRepeatNumber().getValue() ) {
            toimitus.setPakkauksienLukumaara(supply.getRepeatNumber().getValue().intValue());
        }
        if ( null != supply.getQuantity() && null != supply.getQuantity().getValue() ) {
            alustaYksilointitiedot(toimitus);
            toimitus.getValmiste().getYksilointitiedot().setPakkausyksikko(supply.getQuantity().getUnit());
            toimitus.getValmiste().getYksilointitiedot()
                    .setPakkauskoko(Double.parseDouble(supply.getQuantity().getValue()));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fi.kela.kanta.cda.ReseptiPurkaja#puraAsiakirjakohtainenSupplyEntryrelationsipObservation(org.hl7.v3.
     * POCDMT000040Observation, java.lang.String, fi.kela.kanta.to.LaakemaaraysTO)
     */
    @Override
    protected void puraAsiakirjakohtainenSupplyEntryrelationsipObservation(POCDMT000040Observation observation,
            String observationCode, LaakemaaraysTO laakemaarays) {
        // super.puraAsiakirjakohtainenSupplyEntryrelationsipObservation(observation,observationCode, laakemaarays);
        if ( KantaCDAConstants.Laakityslista.OSAPAKKAUS.equals(observationCode)
                && laakemaarays instanceof LaakemaarayksenToimitusTO ) {
            LaakemaarayksenToimitusTO toimitus = (LaakemaarayksenToimitusTO) laakemaarays;
            toimitus.setOsapakkaus(((BL) observation.getValues().get(0)).isValue());
        }
    }
}
