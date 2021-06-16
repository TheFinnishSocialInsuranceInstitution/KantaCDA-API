package fi.kela.kanta.cda.validation;

import fi.kela.kanta.to.LaakemaaraysTO;
import fi.kela.kanta.util.KantaCDAUtil;

public class ReseptinUudenValidoija extends LaakemaaraysValidoija {

    public ReseptinUudenValidoija(LaakemaaraysTO laakemaarays) {
        super(laakemaarays);
    }

    /**
     * Validoi LaakemaaraysTOn. Tarkistaa että annetun laakemaarayksen pohjalta pystytään kasaamaan cda.
     *
     */
    @Override
    public void validoi() {
        if ( getAlkuperainenLaakemaarays() == null ) {
            throw new IllegalArgumentException("laakemaarays cannot be null.");
        }
        validoiAmmattihenkilo(getAlkuperainenLaakemaarays().getAmmattihenkilo());
        validoiOrganisaatio(getAlkuperainenLaakemaarays().getAmmattihenkilo().getOrganisaatio(), false, true);
        validoiOrganisaatio(getAlkuperainenLaakemaarays().getLaatimispaikka(), false, false);
        validoiOrganisaatio(getAlkuperainenLaakemaarays().getLaatimispaikka().getToimintaYksikko(), true, false);
        validoiHenkilotiedot(getAlkuperainenLaakemaarays().getPotilas());
        if ( KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getReseptintyyppi()) ) {
            throw new IllegalArgumentException("Reseptin tyyppi cannot be null or empty.");
        }
        if ( null == getAlkuperainenLaakemaarays().getValmiste() ) {
            throw new IllegalArgumentException("Valmiste cannot be null.");
        }
        if ( null == getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot() ) {
            throw new IllegalArgumentException("Valmiste yksilöintitiedot cannot be null.");
        }
        if ( KantaCDAUtil.onkoNullTaiTyhja(
                getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getValmisteenLaji()) ) {
            throw new IllegalArgumentException(
                    "Valmisteen yksilöintitietojen valmisteen laji cannot be null or empty.");
        }

        if ( !KantaCDAUtil
                .onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getATCkoodi())
                && KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getLaaketietokannanVersio()) ) {
            throw new IllegalArgumentException("Lääketietokannan versio cannot be null or empty.");
        }

        if ( "1".equals(getAlkuperainenLaakemaarays().getReseptintyyppi()) ) {
            if ( KantaCDAUtil.onkoNullTaiTyhja(
                    getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getPakkauskokoteksti()) ) {
                throw new IllegalArgumentException("Pakkauskoko teksti cannot be null or empty.");
            }
        }
        else if ( "2".equals(getAlkuperainenLaakemaarays().getReseptintyyppi()) ) {
            if ( KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getLaakkeenKokonaismaaraUnit()) ) {
                throw new IllegalArgumentException("Laakkeen kokonaismaaran unit cannot be null or empty.");
            }
        }
        else if ( "3".equals(getAlkuperainenLaakemaarays().getReseptintyyppi()) ) {
            if ( KantaCDAUtil
                    .onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getAjalleMaaratynReseptinAikamaaraUnit()) ) {
                throw new IllegalArgumentException("Ajalle määrätyn aikamäärän unit cannot be null or empty.");
            }
            if ( null == getAlkuperainenLaakemaarays().getAjalleMaaratynReseptinAlkuaika() ) {
                throw new IllegalArgumentException("Ajalle määrätyn alkuaika cannot be null.");
            }
        }
        if ( "1".equals(getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getValmisteenLaji()) ) {
            if ( KantaCDAUtil.onkoNullTaiTyhja(
                    getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getYksilointitunnus()) ) {
                throw new IllegalArgumentException("Valmisteen yksilointitunnus cannot be null or empty.");
            }
            if ( KantaCDAUtil.onkoNullTaiTyhja(
                    getAlkuperainenLaakemaarays().getValmiste().getYksilointitiedot().getKauppanimi()) ) {
                throw new IllegalArgumentException("Valmisteen kauppanimi cannot be null or empty.");
            }
        }
        if ( getAlkuperainenLaakemaarays().getHoitolajit().isEmpty() ) {
            throw new IllegalArgumentException("Laakemaarayksen hoitolaji cannot be null or empty.");
        }

        if ( getAlkuperainenLaakemaarays().isUudistamiskielto() ) {
            if ( KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getUusimiskiellonSyy()) ) {
                throw new IllegalArgumentException("Laakemaarayksen uudistamiskiellon syy cannot be null or empty.");
            }
            if ( KantaCDAUtil.onkoNullTaiTyhja(getAlkuperainenLaakemaarays().getUusimiskiellonPerustelu())
                    && "5".equals(getAlkuperainenLaakemaarays().getUusimiskiellonSyy()) ) {
                throw new IllegalArgumentException(
                        "Laakemaarayksen uudistamiskiellon perustelu cannot be null or empty.");
            }
        }
        validoiRakenteinenAnnostus(getAlkuperainenLaakemaarays());
    }

}
