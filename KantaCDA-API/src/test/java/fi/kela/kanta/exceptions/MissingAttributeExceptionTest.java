package fi.kela.kanta.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class MissingAttributeExceptionTest {

    @Test
    public void testCreatingDefaultExceptionMessage() {
        MissingAttributeException mae = new MissingAttributeException("attribuutti");
        TestCase.assertEquals("Sisäinen attribuutti 'attribuutti' puuttuu.", mae.getMessage());
    }
}
