package fi.kela.kanta.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class KokoNimiTO implements Serializable {

    private static final long serialVersionUID = 23412346L;

    private final ArrayList<NimiTO> nimet;

    public KokoNimiTO() {
        nimet = new ArrayList<NimiTO>();
    }

    public KokoNimiTO(String etunimi, String sukunimi) {
        nimet = new ArrayList<NimiTO>();
        lisaa("family", null, sukunimi);
        if ( etunimi != null && etunimi.contains(" ") ) {

            String kutsumaNimi = null;
            StringTokenizer st = new StringTokenizer(etunimi);

            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if ( kutsumaNimi == null ) {
                    kutsumaNimi = token;
                }
                lisaa("given", null, token);
            }
            if ( kutsumaNimi != null ) {
                lisaa("given", "CL", kutsumaNimi);
            }

        }
        else {
            lisaa("given", null, etunimi);
            lisaa("given", "CL", etunimi);
        }
    }

    public KokoNimiTO(String etunimi, String sukunimi, Collection<String> muutEtunimiet) {
        nimet = new ArrayList<NimiTO>();
        nimet.add(new NimiTO("family", null, sukunimi));
        nimet.add(new NimiTO("given", null, etunimi));
        nimet.add(new NimiTO("given", "CL", etunimi));
        for (String nimi : muutEtunimiet) {
            nimet.add(new NimiTO("given", null, nimi));
        }
    }

    /**
     * Lisää elementti nimeen.
     * 
     * @param tyyppi
     *            Elementin nimi. Esim. given, family
     * @param maare
     *            Qualifier-parametri, esim. CL
     * @param nimi
     *            Nimielementin arvo.
     */
    public void lisaa(String tyyppi, String maare, String nimi) {
        nimet.add(new NimiTO(tyyppi, maare, nimi));
    }

    public List<NimiTO> getNimet() {
        return nimet;
    }

    public String getKokoNimi() {
        StringBuilder nimi = new StringBuilder();
        StringBuilder etunimi = new StringBuilder();
        StringBuilder suffiksi = new StringBuilder();
        for (NimiTO elementti : nimet) {
            if ( "given".equals(elementti.getTyyppi()) && !"CL".equalsIgnoreCase(elementti.getMaare()) ) {
                if ( etunimi.length() > 0 ) {
                    etunimi.append(" ");
                }
                etunimi.append(elementti.getNimi());

            }
            else if ( "family".equals(elementti.getTyyppi()) ) {
                if ( nimi.length() > 0 ) {
                    nimi.append(" ");
                }
                nimi.append(elementti.getNimi());

            }
            else if ( "suffix".equals(elementti.getTyyppi()) ) {
                if ( suffiksi.length() > 0 ) {
                    suffiksi.append(" ");
                }
                suffiksi.append(elementti.getNimi());
            }
        }
        if ( etunimi.length() > 0 ) {
            nimi.append(", ").append(etunimi);
        }
        if ( suffiksi.length() > 0 ) {
            nimi.append(", ").append(suffiksi);
        }
        return nimi.toString();
    }

    public String getKutsumanimi() {
        String nimi = "";
        for (NimiTO elementti : nimet) {
            if ( "CL".equalsIgnoreCase(elementti.getMaare()) ) {
                return elementti.getNimi();
            }
            if ( "given".equals(elementti.getTyyppi()) && nimi.length() <= 0 ) {
                nimi = elementti.getNimi();
            }
        }
        return nimi;
    }

    public String getEtunimi() {
        return getNimi("given");
    }

    public String getSukunimi() {
        return getNimi("family");
    }

    public String getNimi(String nimenTyyppi) {
        StringBuilder nimi = new StringBuilder();
        for (NimiTO elementti : nimet) {
            if ( nimenTyyppi.equals(elementti.getTyyppi()) && elementti.getNimi() != null ) {
                if ( nimi.length() > 0 ) {
                    nimi.append(" ");
                }
                nimi.append(elementti.getNimi());
            }
        }
        return nimi.toString();
    }
}
