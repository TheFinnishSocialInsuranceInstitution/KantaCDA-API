package fi.kela.kanta.testClasses;

public class ExtendingTestClass extends PojoTestClass {

    private int info0;

    public ExtendingTestClass(int info0, String info1, String info2) {
        super(info1, info2);

        this.info0 = info0;
    }

    public int getInfo0() {
        return info0;
    }

    public void setInfo0(int info0) {
        this.info0 = info0;
    }

}
