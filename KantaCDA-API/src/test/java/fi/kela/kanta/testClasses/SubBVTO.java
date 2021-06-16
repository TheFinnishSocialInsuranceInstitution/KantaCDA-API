package fi.kela.kanta.testClasses;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Toissijainen luokka BeanValidationTO: käytettäväksi jota voidaan käyttää Bean Validationin testaamiseen, että se
 * yltää myös parametri luokkiin.
 */
public class SubBVTO {

    @NotNull
    private int id;

    @Size(min = 5, max = 500)
    private String text;

    public SubBVTO() {

    }

    public SubBVTO(int id, String text) {
        this.setId(id);
        this.setText(text);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
