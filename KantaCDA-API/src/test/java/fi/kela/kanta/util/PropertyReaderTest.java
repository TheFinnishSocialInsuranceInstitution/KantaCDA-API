package fi.kela.kanta.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fi.kela.kanta.testClasses.AbstractTest;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class PropertyReaderTest extends AbstractTest {

    private static final Logger LOGGER = LogManager.getLogger(PropertyReaderTest.class);

    @Test
    public void propertiesTest() {
        Properties props = null;
        try {
            props = PropertyReader.getProperties("testi.properties");
        }
        catch (IOException e) {
            LOGGER.error(e);
            TestCase.fail("IOException");
        }

        Assert.assertEquals("value", props.getProperty("testi.arvo"));
    }

    @Test
    public void propertyTest() {
        String prop = null;

        try {
            prop = PropertyReader.getProperty("testi.properties", "testi.arvo");
        }
        catch (IOException e) {
            LOGGER.error(e);
            TestCase.fail("IOException");
        }

        Assert.assertEquals("value", prop);
    }

    @Test
    public void validPropertyTest() throws IllegalArgumentException {
        String prop = null;

        prop = PropertyReader.getValidProperty("testi.properties", "testi");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop, "30");

        prop = PropertyReader.getValidProperty("testi.properties", "keski");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop, "30");

        prop = PropertyReader.getValidProperty("testi.properties", "future");
        Assert.assertNull(prop);

    }

    @Test(expected = IOException.class)
    public void noFileFoundTest() throws IOException {

        PropertyReader.getProperties("nosuch.file");
        Assert.fail("IOException expected");
    }

    // Test
    public void validProperty_currentDate() throws IllegalArgumentException {

        String prop = null;
        prop = PropertyReader.getValidProperty("testi.properties", "today");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop, "ff");
    }

    @Test(expected = IllegalStateException.class)
    public void validProperty_exceptionTest() {
        String prop = null;

        prop = PropertyReader.getValidProperty("testi.properties", "exception");

        Assert.assertEquals(prop, "30");
    }
}
