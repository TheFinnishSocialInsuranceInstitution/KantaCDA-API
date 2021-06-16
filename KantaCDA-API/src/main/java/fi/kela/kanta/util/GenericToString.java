package fi.kela.kanta.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericToString {

    @OmitFromToString
    private SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm.ss");
    private static final String comma = ", ";
    private static final String equals = "=";
    private static final String open_parameters = " [";
    private static final String close_parameters = "]";
    private static final String null_date = "null (date)";
    private static final String na_date = "N/A (date)";
    private static final String null_non_primitive = "null (object)";
    private static final String na_non_primitive = "N/A (object)";
    private static final String null_array = "null (array)";
    private static final String na_array = "N/A (array)";
    private static final String na_access = "N/A (restricted)";
    private static final String hidden_value = "xxxxx";
    private static final Logger LOGGER = LogManager.getLogger(GenericToString.class);

    @Override
    public String toString() {
        return this.toString(this);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return super.equals(o);
    }

    /**
     * Geneerinen toString metodi, jolla saadaan yksinkertainen siisti key=value listaus annetun luokan attribuuteista.
     *
     * @param obj
     *            Objekti jonka tiedot halutaan tulostaa.
     * @return Listaus annetun objektin attribuuteista ja niiden arvoista.
     */
    public String toString(Object obj) {

        StringBuilder sb = new StringBuilder();
        sb.append(obj.getClass().getSimpleName());
        sb.append(GenericToString.open_parameters);

        boolean fieldsAdded = false;
        for (Field field : GenericToString.getAllFields(obj.getClass())) {
            // Ei näytetä staattisia kenttiä
            if ( field != null && !java.lang.reflect.Modifier.isStatic(field.getModifiers())
                    && !(field.getName().startsWith("this$") || field.getName().startsWith("_persistence")) ) {
                fieldsAdded = true;
                sb.append(field.getName()).append(GenericToString.equals);
                try {
                    if ( field.getType().isPrimitive() || field.getType().isAssignableFrom(String.class) ) {
                        if ( field.getAnnotation(OmitFromToString.class) != null ) {
                            sb.append(hidden_value);
                        }
                        else {
                            sb.append(FieldUtils.readField(obj, field.getName(), true));
                        }
                    }
                    else if ( field.getType().isArray() ) {
                        if ( !field.isAccessible() ) {
                            field.setAccessible(true);
                        }
                        // TODO: Voi jatkokehittää siten että tulostaa siististi arrayn
                        Object fieldValue = field.get(obj);
                        if ( fieldValue != null ) {
                            sb.append(GenericToString.na_array);
                        }
                        else {
                            sb.append(GenericToString.null_array);
                        }
                    }
                    else {
                        addObjectInfo(sb, field, obj);
                    }
                }
                catch (IllegalAccessException e) {
                    LOGGER.error(e);
                    sb.append(GenericToString.na_access);
                }

                sb.append(GenericToString.comma);
            }
        }

        if ( fieldsAdded ) {
            // remove last comma and space
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(GenericToString.close_parameters);
        return sb.toString();
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private void addObjectInfo(StringBuilder sb, Field field, Object originalObject)
            throws IllegalArgumentException, IllegalAccessException {

        if ( field != null ) {
            if ( !field.isAccessible() ) {
                field.setAccessible(true);
            }
            if ( field.getType().isAssignableFrom(Date.class) ) {
                try {
                    Object date = field.get(originalObject);
                    if ( date != null ) {
                        sb.append(SDF.format(date));
                    }
                    else {
                        sb.append(GenericToString.null_date);
                    }
                    // Return accessibility?
                }
                catch (Exception e) {
                    LOGGER.warn(e);
                    sb.append(GenericToString.na_date);
                }
            }
            else {
                // TODO: Voi jatkokehittää siten että tulostaa esim. lapsiobjektin tiedot
                Object fieldValue = field.get(originalObject);
                if ( fieldValue != null ) {
                    sb.append(GenericToString.na_non_primitive);
                }
                else {
                    sb.append(GenericToString.null_non_primitive);
                }
            }
        }
    }

    /**
     * Annotation to omit a field from printing automatically by the generic toString method. E.G. some information that
     * is not wished to be seen. The value of this field is simply presented with 'xxxxx'.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface OmitFromToString {

    }
}
