package fi.kela.kanta.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Geneerinen luokka eri bean:ien validoimiseksi, niissä annettuja BeanValidation annotaatio-ehtoja vasten.
 *
 * @param <T>
 *            Beanin tyyppi, jota validoidaan.
 */
public class BeanValidator<T> {

    private static String begin = "[";
    private static String end = "]";

    /**
     * Validoi annettu bean:in siinä annettuja BeanValidation annotaatio-ehtoja vasten.
     *
     * @param bean
     *            Validoitava bean.
     * @return Listaus löytyneistä validointivirheistä.
     */
    public Set<ConstraintViolation<T>> doValidation(T bean) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> errors = validator.validate(bean);
        return errors;
    }

    /**
     * @return Siisti printti virheistä, logitusta varten.
     */
    public String prettyPrint(Set<ConstraintViolation<T>> virheet) {

        StringBuilder sb = new StringBuilder();
        sb.append(BeanValidator.begin);
        for (ConstraintViolation<T> error : virheet) {
            sb.append("{" + error.getMessage());
            sb.append(", propertyPath = " + error.getPropertyPath());
            sb.append(", value = " + error.getInvalidValue());
            sb.append("}, ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append(BeanValidator.end);
        return sb.toString();
    }
}
