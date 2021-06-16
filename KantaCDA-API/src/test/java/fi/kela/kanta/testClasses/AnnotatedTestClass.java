package fi.kela.kanta.testClasses;

import fi.kela.kanta.util.GenericToString;

public class AnnotatedTestClass extends GenericToString {

    public String visibleText = "visible";
    @OmitFromToString
    public String hiddenText = "hidden";
}
