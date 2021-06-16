package fi.kela.kanta.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import fi.kela.kanta.testClasses.AnnotatedTestClass;
import fi.kela.kanta.testClasses.ComplexTestClass;
import fi.kela.kanta.testClasses.ExtendingTestClass;
import fi.kela.kanta.testClasses.PojoTestClass;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class GenericToStringTest {

    @Test
    public void testGenericToStringMethod() {

        PojoTestClass yid = new PojoTestClass("foo", "bar");
        String toString = yid.toString();
        String jacoco = ", $jacocoData=N/A (array)]";
        if ( toString.endsWith(jacoco) ) {
            // Jacoco osuus tulee jostain kun ajetaan maven testejä
            String expectedStart = "PojoTestClass [info1=foo, info2=bar";
            TestCase.assertTrue("expected to start with:" + expectedStart + ", but was: " + toString,
                    toString.startsWith(expectedStart));
        }
        else {
            TestCase.assertEquals("PojoTestClass [info1=foo, info2=bar, SDF=N/A (object)]", yid.toString());
        }

        ComplexTestClass tc = new ComplexTestClass(10, "asdasd", yid);

        toString = tc.toString();
        // Jacoco osuus tulee jostain kun ajetaan maven testejä, siksi vain "startsWith" (tulisi viimeisenä myös, date,
        // joka testataan erikseen)
        String expectedStart = "ComplexTestClass [id=10, desc=asdasd, otherInfo=N/A (object), intArray=N/A (array), list=N/A (object)";
        TestCase.assertTrue("expected to start with:" + expectedStart + ", but was: " + toString,
                toString.startsWith(expectedStart));

    }

    @Test
    public void testGenericToStringMethodWithDateObject() {

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.MONTH, 10);
        cal.set(Calendar.DAY_OF_MONTH, 8);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 11);
        cal.set(Calendar.SECOND, 12);
        cal.set(Calendar.MILLISECOND, 00);
        ComplexTestClass tc = new ComplexTestClass();
        tc.setDate(cal.getTime());

        String toString = tc.toString();
        String expectedToContain = "date=08.11.2016 10:11.12";
        TestCase.assertTrue("expected to contain text: " + expectedToContain + ", but the String was: " + toString,
                toString.contains(expectedToContain));
    }

    @Test
    public void testGenericToStringMethodWithNullObjects() {

        ComplexTestClass tc = new ComplexTestClass();
        tc.setDate(null);
        tc.setList(null);
        tc.setOtherInfo(null);
        tc.setIntArray(null);

        String toString = tc.toString();
        String expectedToContain = "date=null (date)";
        TestCase.assertTrue("expected to contain text: " + expectedToContain + ", but the String was: " + toString,
                toString.contains(expectedToContain));
        expectedToContain = "list=null (object)";
        TestCase.assertTrue("expected to contain text: " + expectedToContain + ", but the String was: " + toString,
                toString.contains(expectedToContain));
        expectedToContain = "intArray=null (array)";
        TestCase.assertTrue("expected to contain text: " + expectedToContain + ", but the String was: " + toString,
                toString.contains(expectedToContain));
        expectedToContain = "otherInfo=null (object)";
        TestCase.assertTrue("expected to contain text: " + expectedToContain + ", but the String was: " + toString,
                toString.contains(expectedToContain));
    }

    @Test
    public void testExtendingClassGenericToStringMethod() {

        ExtendingTestClass yid = new ExtendingTestClass(1, "foo", "bar");
        String toString = yid.toString();
        String jacoco = ", $jacocoData=N/A (array)]";
        if ( toString.endsWith(jacoco) ) {
            // Jacoco osuus tulee jostain kun ajetaan maven testejä
            String expectedStart = "ExtendingTestClass [info0=1, info1=foo, info2=bar, {extends";
            TestCase.assertTrue("expected to start with:" + expectedStart + ", but was: " + toString,
                    toString.startsWith(expectedStart));
        }
        else {
            TestCase.assertEquals("ExtendingTestClass [info0=1, info1=foo, info2=bar, SDF=N/A (object)]",
                    yid.toString());
        }

        ComplexTestClass tc = new ComplexTestClass(10, "asdasd", yid);

        toString = tc.toString();
        // Jacoco osuus tulee jostain kun ajetaan maven testejä, siksi vain "startsWith" (tulisi viimeisenä myös, date,
        // joka testataan erikseen)
        String expectedStart = "ComplexTestClass [id=10, desc=asdasd, otherInfo=N/A (object), intArray=N/A (array), list=N/A (object)";
        TestCase.assertTrue("expected to start with:" + expectedStart + ", but was: " + toString,
                toString.startsWith(expectedStart));

    }

    @Test
    public void testOmittingAnnotatedFieldFromToString() {

        AnnotatedTestClass atc = new AnnotatedTestClass();
        String toString = atc.toString();
        Assert.assertEquals("AnnotatedTestClass [visibleText=visible, hiddenText=xxxxx, SDF=N/A (object)]", toString);
    }

    @Test
    public void testEqualsPojoTestClass() {
        PojoTestClass yid = new PojoTestClass("foo", "bar");
        PojoTestClass yid2 = new PojoTestClass("oof", "rab");
        PojoTestClass yid3 = new PojoTestClass("foo", "bar");
        int hash1 = yid.hashCode();
        int hash2 = yid2.hashCode();
        int hash3 = yid3.hashCode();
        Assert.assertTrue(hash1 == hash3);
        Assert.assertTrue(hash3 != hash2);
        Assert.assertTrue(yid.equals(yid));
        Assert.assertTrue(yid2.equals(yid2));
        Assert.assertTrue(yid3.equals(yid3));
        Assert.assertFalse(yid.equals(yid2));
        Assert.assertFalse(yid2.equals(yid));
        Assert.assertFalse(yid.equals(yid3));
        Map<PojoTestClass, String> map1 = new HashMap<PojoTestClass, String>();
        map1.put(yid, "yid");
        map1.put(yid2, "yid2");
        map1.put(yid3, "yid3");
        map1.put(yid, "yid uudelleen");
        Assert.assertEquals(map1.size(), 3);
        Set<PojoTestClass> set1 = new HashSet<PojoTestClass>();
        set1.add(yid);
        set1.add(yid2);
        set1.add(yid3);
        set1.add(yid);
        Assert.assertEquals(set1.size(), 3);
    }

    @Test
    public void testEqualsComplexTestClass() {
        ExtendingTestClass ed = new ExtendingTestClass(1, "foo", "bar");
        ComplexTestClass yid = new ComplexTestClass(10, "asdasd", ed);
        ComplexTestClass yid2 = new ComplexTestClass(9, "bbsfd", ed);
        ComplexTestClass yid3 = new ComplexTestClass(10, "asdasd", ed);
        int hash1 = yid.hashCode();
        int hash2 = yid2.hashCode();
        int hash3 = yid3.hashCode();
        Assert.assertTrue(hash1 == hash3);
        Assert.assertTrue(hash3 != hash2);
        Assert.assertTrue(yid.equals(yid));
        Assert.assertTrue(yid2.equals(yid2));
        Assert.assertTrue(yid3.equals(yid3));
        Assert.assertFalse(yid.equals(yid2));
        Assert.assertFalse(yid2.equals(yid));
        Assert.assertFalse(yid.equals(yid3));
        Map<ComplexTestClass, String> map1 = new HashMap<ComplexTestClass, String>();
        map1.put(yid, "yid");
        map1.put(yid2, "yid2");
        map1.put(yid3, "yid3");
        map1.put(yid, "yid uudelleen");
        Assert.assertEquals(map1.size(), 3);
        Set<ComplexTestClass> set1 = new HashSet<ComplexTestClass>();
        set1.add(yid);
        set1.add(yid2);
        set1.add(yid3);
        set1.add(yid);
        Assert.assertEquals(set1.size(), 3);
    }
}
