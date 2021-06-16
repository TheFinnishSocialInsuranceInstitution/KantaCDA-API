package fi.kela.kanta.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Luokan avulla käydään etsimässä classloading:sta properties-tiedosto joka tallennetaan muistiin. Luokan avulla
 * voidaan kysyä myös yksittäistä arvoa.
 */
public class PropertyReader {

    private PropertyReader() {
    }

    private static Logger LOGGER = LogManager.getLogger(PropertyReader.class);

    private static final String ENCODING_UTF8 = "UTF-8";
    protected static final String use_defaults = "use_defaults";

    private static ConcurrentHashMap<String, Properties> cache;

    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";

    /**
     * Palauttaa kyseinen properties objektin välimuistista. Etsii classloading:sta kyseisen tiedoston ja tallentaa se
     * välimuistiin ennen paluuta.
     *
     * @param propertiesFileName
     *            Property-tiedostonnimi, jota käytetään arvojen lataamiseen.
     * @return
     * @throws IOException
     */
    public static Properties getProperties(String propertiesFileName) throws IOException {
        return getProperties(propertiesFileName, null);
    }

    /**
     * Palauttaa kyseinen properties objektin välimuistista. Etsii classloading:sta kyseisen tiedoston ja tallentaa se
     * välimuistiin ennen paluuta.
     *
     * @param propertiesFileName
     *            Property-tiedostonnimi, jota käytetään arvojen lataamiseen.
     * @param defaultPropertiesFileName
     *            Property-tiedostonnimi, jos halutaan käyttää jotain muuta kuin samaa tiedoston nimeä kuin mikä on
     *            primääri property-tiedostonnimi. Käytä <code>null</code> kun ei tarvitse etsiä erillistä default
     *            tiedostoa.
     * @return
     * @throws IOException
     */
    protected static Properties getProperties(String propertiesFileName, String defaultPropertiesFileName)
            throws IOException {
        Properties properties = null;

        if ( cache == null ) {
            cache = new ConcurrentHashMap<>();
        }

        if ( cache.containsKey(propertiesFileName) ) {
            if ( LOGGER.isTraceEnabled() ) {
                LOGGER.trace("Getting Properties from cache for " + propertiesFileName);
            }

            // return from cache
            properties = cache.get(propertiesFileName);
        }
        else {
            if ( LOGGER.isTraceEnabled() ) {
                LOGGER.trace("Reading Properties from System for " + propertiesFileName);
            }
            properties = null;

            List<InputStream> streams = getResourceStreams(propertiesFileName, defaultPropertiesFileName,
                    PropertyReader.class.getClassLoader());
            if ( streams != null ) {
                Object[] args = { streams.size(), propertiesFileName, defaultPropertiesFileName };
                LOGGER.info(String.format("Found %1$s property file(s) with name '%2$s' with default file name '%3$s'",
                        args));
            }
            for (InputStream is : streams) {
                InputStreamReader inputStreamReader = new InputStreamReader(is, ENCODING_UTF8);
                try {
                    if ( properties == null ) {
                        properties = new Properties();
                        properties.load(inputStreamReader);
                    }
                    else if ( properties.containsKey(use_defaults)
                            && Boolean.parseBoolean(properties.getProperty(use_defaults)) ) {
                        Properties defaultProperties = new Properties();
                        defaultProperties.load(inputStreamReader);
                        properties.putAll(defaultProperties);
                    }
                    else {
                        break;
                    }
                }
                catch (FileNotFoundException e) {
                    LOGGER.error("Cannot read '" + propertiesFileName + "' file.", e);
                    throw e;
                }
                catch (IOException e) {
                    LOGGER.error("Cannot access '" + propertiesFileName + "' file.", e);
                    throw e;
                }
                finally {
                    is.close();
                    inputStreamReader.close();
                }
                cache.put(propertiesFileName, properties);
            }
        }
        if ( properties == null ) {
            Object[] args = { propertiesFileName, defaultPropertiesFileName };
            String errorText = null;
            if ( defaultPropertiesFileName == null ) {
                errorText = String.format("No property file(s) were found with name '%s'", args);
            }
            else {
                errorText = String.format(
                        "No property file(s) were found with name '%1$s' or with default file name '%2$s'", args);
            }
            LOGGER.error(errorText);
            throw new IOException(errorText);
        }
        return properties;

    }

    /**
     * Palauttaa avaimella etsityn arvon halutusta properties-tiedostosta.
     *
     * @param propertiesFileName
     * @param propertyKey
     * @return
     * @throws IOException
     * @see {@link PropertyReader}
     */
    public static String getProperty(String propertiesFileName, String propertyKey) throws IOException {

        Properties props = getProperties(propertiesFileName);
        String property = props.getProperty(propertyKey);

        if ( property == null && LOGGER.isWarnEnabled() ) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot find '").append(propertyKey).append("' from '");
            sb.append(propertiesFileName).append("'.");
            LOGGER.warn(sb.toString());
        }

        if ( LOGGER.isTraceEnabled() ) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found [").append(propertiesFileName).append(":   key='");
            sb.append(propertyKey).append("', value='");
            sb.append(property).append("']");
            LOGGER.trace(sb.toString());
        }

        return property;
    }

    /**
     * Palauttaa voimassaolevan arvon halutulle avaimelle. Metodi lisää iteroitavan indeksin, mikä täydennetään avaimeen
     * <code>String.format</code> metodin avulla. Avaimen tulee päättyä '%o' merkkeihin (
     * <code><prefix osa avaimelle>%o</code>). Konfiguraatiossa arvoparien tulee indeksin jälkeen päättyä muotoihin
     * <code>.date</code> ja <code>.value</code>. Lisäksi indeksien kasvaessa tulee päivämäärien myös nousta
     * vastaavasti.</br>
     * <p>
     * Palauttaa null, kun ensimmäinen päivämäärä ei ole voimassa.
     * </p>
     * Esimerkiksi:
     * <p>
     * <code>
     * resepti.voimassaoloaika.kk.0.date=1.1.2009</br>
     * resepti.voimassaoloaika.kk.0.value=12</br></br>
     * resepti.voimassaoloaika.kk.1.date=1.1.2016</br>
     * resepti.voimassaoloaika.kk.1.value=30</br></br>
     * resepti.voimassaoloaika.kk.2.date=9.6.2016</br>
     * resepti.voimassaoloaika.kk.2.value=60</br></br>
     * </code>
     * </p>
     *
     * @param propertiesFileName
     * @param propertyKey
     * @return
     * @throws IOException
     */
    public static String getValidProperty(String propertiesFileName, String propertyKey) throws IllegalStateException {
        String value = null;
        String key = null;
        String valueKey = null;

        Calendar current = Calendar.getInstance();
        Calendar previous = null;

        try {
            for (Integer i = 0; i < 100; i++) {
                key = String.format("%s.%d.date", propertyKey, i);

                String date = getProperty(propertiesFileName, key);
                if ( date == null ) {
                    // Lopetaan lukeminen mikäli avaimelle ei löydy arvoa.
                    return value;
                }

                SimpleDateFormat sdf = new SimpleDateFormat(PropertyReader.SIMPLE_DATE_FORMAT);
                Calendar validDate = Calendar.getInstance();
                validDate.setTime(sdf.parse(date));

                /*
                 * Päivämäärien tulee olla konfiguraatiossa nousevassa järjestyksessä.
                 */
                if ( previous != null && validDate.before(previous) ) {
                    throw new IllegalStateException(
                            "Muuttujien päivämääräarvojen tulee olla nousevassa järjestyksessä.");
                }

                /*
                 * Noudetaan arvo, kun kuluva päivämäärä on voimassa.
                 */
                if ( current.after(validDate) ) {
                    valueKey = String.format("%s.%d.value", propertyKey, i);
                    value = getProperty(propertiesFileName, valueKey);
                    previous = validDate;
                }
                else {
                    // Lopetaan tarkistukset kun muuttujan arvo on tulevaisuudessa.
                    return value;
                }
            }
            return value;
        }
        catch (ParseException e) {
            throw new IllegalStateException("Päivämäärän parsiminen epäonnistui konfiguraation arvolle: " + key, e);
        }
        catch (IOException e) {
            throw new IllegalStateException("konfiguraation lukeminen epäonnistui arvolle: " + key, e);
        }
    }

    /**
     * Tyhjentää välimuistista
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * tyhjentää välimuistista kyseisen Properties-objektin
     *
     * @param propertiesFileName
     * @return
     */
    public static Properties clearProperties(String propertiesFileName) {
        return cache.remove(propertiesFileName);
    }

    /**
     * Poistaa muistista yksittäisen arvoparin
     *
     * @param propertiesFileName
     * @param property
     */
    public static String removeProperty(String propertiesFileName, String property) {
        return (String) cache.get(propertiesFileName).remove(property);
    }

    /**
     * Hakee kaikki input streamit annetulla class lopaderilla, jotka osoittavat nimettyyn tiedostoon.
     *
     * @param name
     *            Tiedoston nimi, jota etsiä.
     * @param classLoader
     *            ClassLoader jolla haku tehdään.
     * @param defaultPropertiesFileName
     *            Property-tiedostonnimi, jos halutaan käyttää jotain muuta kuin samaa tiedoston nimeä kuin mikä on
     *            primääri property-tiedostonnimi. Käytä <code>null</code> kun ei tarvitse etsiä erillistä default
     *            tiedostoa.
     * @return Listaus streameista, joilla tiedostot saadaan ladattua.
     * @throws IOException
     */
    public static List<InputStream> getResourceStreams(final String name, String defaultPropertiesFileName,
            final ClassLoader classLoader) throws IOException {
        final List<InputStream> list = new ArrayList<>();
        final Enumeration<URL> primarySystemResources = (classLoader == null ? ClassLoader.getSystemClassLoader()
                : classLoader).getResources(name);
        while (primarySystemResources.hasMoreElements()) {
            list.add(primarySystemResources.nextElement().openStream());
        }
        if ( defaultPropertiesFileName != null ) {
            final Enumeration<URL> secondarySystemResources = (classLoader == null ? ClassLoader.getSystemClassLoader()
                    : classLoader).getResources(defaultPropertiesFileName);
            while (secondarySystemResources.hasMoreElements()) {
                list.add(secondarySystemResources.nextElement().openStream());
            }
        }
        return list;
    }
}
