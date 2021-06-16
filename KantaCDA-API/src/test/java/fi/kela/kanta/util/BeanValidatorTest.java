package fi.kela.kanta.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fi.kela.kanta.testClasses.BeanValidationTO;
import fi.kela.kanta.testClasses.SubBVTO;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class BeanValidatorTest {

    private static final Logger LOGGER = LogManager.getLogger(BeanValidatorTest.class);

    @Test
    public void doBasicValidationTest() {

        BeanValidationTO bean = new BeanValidationTO();
        bean.setFirstName("Matti");
        bean.setLastName("M");
        bean.setSex("M");
        Calendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, 2000);
        bean.setBirthDay(greg.getTime());
        greg.set(Calendar.YEAR, 2020);
        bean.setDyingDate(greg.getTime());
        bean.setPin("500");

        BeanValidator<BeanValidationTO> validator = new BeanValidator<BeanValidationTO>();
        Set<ConstraintViolation<BeanValidationTO>> errors = validator.doValidation(bean);
        LOGGER.trace("Validation errors: " + errors.size());
        this.printErrors(errors, "doBasicValidationTest");
        TestCase.assertEquals(3, errors.size());
    }

    @Test
    public void doHibernateValidationTest() {

        BeanValidator<BeanValidationTO> validator = new BeanValidator<BeanValidationTO>();
        BeanValidationTO bean = new BeanValidationTO();
        bean.setFirstName("Matti");
        bean.setLastName("Meikäläinen");
        bean.setSex("M");
        bean.setCallName("Mame");
        bean.setPin("1234");
        Calendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, 2010);
        bean.setBirthDay(greg.getTime());
        greg.set(Calendar.YEAR, 2000);
        bean.setDyingDate(greg.getTime());

        Set<ConstraintViolation<BeanValidationTO>> errors = validator.doValidation(bean);
        this.printErrors(errors, "doHibernateValidationTest");
        TestCase.assertEquals(1, errors.size());
    }

    @Test
    public void doSubObjectValidationTest() {

        BeanValidator<BeanValidationTO> validator = new BeanValidator<BeanValidationTO>();
        BeanValidationTO bean = new BeanValidationTO();
        bean.setFirstName("Matti");
        bean.setLastName("Meikäläinen");
        bean.setSex("M");
        bean.setCallName("Mame");
        bean.setPin("1234");
        Calendar greg = new GregorianCalendar();
        greg.set(Calendar.YEAR, 2000);
        bean.setBirthDay(greg.getTime());
        greg.set(Calendar.YEAR, 2100);
        bean.setDyingDate(greg.getTime());

        SubBVTO sub = new SubBVTO();
        sub.setId(1);
        sub.setText("bad");
        bean.setSub(sub);

        try {
            Set<ConstraintViolation<BeanValidationTO>> errors = validator.doValidation(bean);
            TestCase.assertEquals(1, errors.size());
            this.printErrors(errors, "doSubObjectValidationTest");
        }
        catch (Exception e) {
            LOGGER.error(e);
            TestCase.fail("Jotain meni pieleen.");
        }
    }

    @Test
    public void doCrossFieldCheck() {

        BeanValidator<BeanValidationTO> validator = new BeanValidator<BeanValidationTO>();
        BeanValidationTO bean = new BeanValidationTO();
        bean.setFirstName("Matti");
        bean.setLastName("Meikäläinen");
        bean.setSex("M");
        bean.setCallName("Mame");
        bean.setPin("1234");

        Set<ConstraintViolation<BeanValidationTO>> errors = validator.doValidation(bean);
        this.printErrors(errors, "testCrossFieldCheck");
        TestCase.assertEquals(1, errors.size());
    }

    private synchronized void printErrors(Set<ConstraintViolation<BeanValidationTO>> errors, String testName) {

        LOGGER.trace("======== " + testName + " (" + errors.size() + ") ========");
        for (ConstraintViolation<BeanValidationTO> error : errors) {
            LOGGER.trace("message = " + error.getMessage());
            LOGGER.trace("propertyPath = " + error.getPropertyPath());
            LOGGER.trace("value = " + error.getInvalidValue());
            LOGGER.trace("========");
        }
        LOGGER.trace("");
        this.notifyAll();
    }

}
