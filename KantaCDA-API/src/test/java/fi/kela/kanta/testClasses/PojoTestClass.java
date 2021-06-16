package fi.kela.kanta.testClasses;

import fi.kela.kanta.util.GenericToString;

public class PojoTestClass extends GenericToString {

    private String info1;
    private String info2;

    public PojoTestClass(String info1, String info2) {

        setInfo1(info1);
        setInfo2(info2);
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String subInfo1) {
        info1 = subInfo1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String subInfo2) {
        info2 = subInfo2;
    }
}
