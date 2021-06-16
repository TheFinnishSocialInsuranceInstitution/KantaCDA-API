package fi.kela.kanta.testClasses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.kela.kanta.util.GenericToString;

public class ComplexTestClass extends GenericToString {

    private int id;
    private String desc;
    private PojoTestClass otherInfo;
    private int[] intArray = { 1, 2, 3 };
    private List<String> list = new ArrayList<String>();
    private Date date = null;

    public ComplexTestClass() {

    }

    public ComplexTestClass(int id, String desc, PojoTestClass other) {

        setId(id);
        setDesc(desc);
        setOtherInfo(other);
        list.add("yks");
        list.add("kaks");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PojoTestClass getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(PojoTestClass other) {
        otherInfo = other;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
