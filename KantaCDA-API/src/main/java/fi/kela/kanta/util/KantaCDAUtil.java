package fi.kela.kanta.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fi.kela.kanta.to.LaakemaaraysTO;

/**
 * KantaCDAUtils Apumetodien kaatoluokka
 */
public class KantaCDAUtil {
    private static final Object iterLock = new Object();
    private static final String VALMISTEEN_LAJI_APTEEKISSA_VALMISTETTAVA = "7";

    private KantaCDAUtil() {
    }

    public static final String CONTROL_PATTERN = "\\r|\\n";
    
    public static final String HETU_DATE_FORMAT = "ddMMyy";
    
    public static final String Y_TUNNUS_EXPR = "([0-9]{7})-[0-9]";

    private static final Logger LOGGER = LogManager.getLogger(KantaCDAUtil.class);

    private static Map<String, String> iterUnit;

    /**
     * muodostaa syntymäajan hetun perusteella
     * 
     * @param hetu
     * @return syntymäaika
     */
    public static String hetuToBirthTime(String hetu) {
        if ( onkoNullTaiTyhja(hetu) || hetu.length() != 11 ) {
            return null;
        }
        String century = "??";
        if ( hetu.charAt(6) == '+' ) {
            century = "18";
        }
        else if ( hetu.charAt(6) == '-' ) {
            century = "19";
        }
        else if ( hetu.charAt(6) == 'A' || hetu.charAt(6) == 'a' ) {
            century = "20";
        }
        else {
            century = "21";
        }
        return century + hetu.substring(4, 6) + hetu.substring(2, 4) + hetu.substring(0, 2);
    }
    
    /**
     * muodostaa syntymäajan hetun perusteella annetulla muotoilulla
     * 
     * @param hetu
     * @param format
     * @return syntymäaika
     */
    public static String hetuToBirthTime(String hetu, String format) {
    	if (onkoNullTaiTyhja(format)) {
    		return hetuToBirthTime(hetu);
    	}
    	if ( onkoNullTaiTyhja(hetu) || hetu.length() != 11 ) {
            return null;
        }
        Date birthDate = hetuToDate(hetu);
        if (birthDate == null) {
        	return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
    	return sdf.format(birthDate);
    }
    
    public static Date hetuToDate(String hetu) {
    	SimpleDateFormat hetuformat = new SimpleDateFormat(HETU_DATE_FORMAT);
    	try {
			return hetuformat.parse(hetu.substring(0, 6));
		} catch (ParseException e) {
			return null;
		}
    }

    /**
     * @param hetu
     * @return
     */
    public static int hetuToGender(String hetu) {
        if ( !onkoNullTaiTyhja(hetu) && hetu.length() == 11 ) {
            return (Integer.parseInt(hetu.substring(9, 10)) & 0x01) == 1 ? 1 : 2;
        }
        return 0;
    }

    public static Properties loadProperties(String propertyFile) throws IOException {
        InputStream input = null;
        Properties properties = new Properties();
        try {
            input = KantaCDAUtil.class.getClassLoader().getResourceAsStream(propertyFile);
            properties.load(new InputStreamReader(input, "UTF-8"));
        }
        catch (FileNotFoundException fnfe) {
            LOGGER.error(propertyFile + " FILE NOT FOUND! :" + fnfe.getMessage());
            throw fnfe;
        }
        finally {
            if ( input != null ) {
                input.close();
            }
        }
        return properties;
    }

    public static String poistaKontrolliMerkit(String teksti) {
        if ( teksti != null ) {
            return teksti.replaceAll(KantaCDAUtil.CONTROL_PATTERN, "");
        }
        return null;
    }

    /**
     * Muuntaa desimaaliluvun merkkijonoksi ja poistaa desimaalit, jotka eivät ole merkitseviä. Esim. 4.0 --> 4.
     * 
     * @param luku
     * @return
     */
    public static String doubleToString(double luku) {
        BigDecimal bd = BigDecimal.valueOf(luku);
        String bdS = bd.toPlainString();
        return bdS.indexOf(".") < 0 ? bdS : bdS.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    public static boolean onkoNullTaiTyhja(String merkkijono) {
        return null == merkkijono || merkkijono.isEmpty();
    }

    public static String muunnaIterUnit(String unit) {
        if ( iterUnit == null ) {
            iterUnit = new HashMap<String, String>();
            iterUnit.put("d", "pv");
        }
        String vali = iterUnit.get(unit);
        return vali != null ? vali : unit;
    }
    
    public static boolean onkoYTunnus(String tunnus) {
    	if (null == tunnus || tunnus.length() != 9) {
			return false;
		}
		return tunnus.matches(Y_TUNNUS_EXPR);
    }
    
    /**
     * Päättelee valmisteen lajin perusteella onko kyseessä apteekissa valmistettava lääke.
     * Jos valmisteen laji = 7, palautuu true, muuten false.
     * 
     * @param laakemaarays
     * @return 
     */
    public static boolean onkoApteekissaValmistettava(LaakemaaraysTO laakemaarays) {
		if (laakemaarays != null && laakemaarays.getValmiste() != null
				&& laakemaarays.getValmiste().getYksilointitiedot() != null) {
			if (VALMISTEEN_LAJI_APTEEKISSA_VALMISTETTAVA
					.equals(laakemaarays.getValmiste().getYksilointitiedot().getValmisteenLaji())) {
				return true;
			}
		}

		return false;
	}

}
