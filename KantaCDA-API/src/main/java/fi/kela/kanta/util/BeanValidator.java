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
