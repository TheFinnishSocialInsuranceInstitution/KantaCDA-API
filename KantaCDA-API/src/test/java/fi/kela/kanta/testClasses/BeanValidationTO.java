package fi.kela.kanta.testClasses;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.ScriptAssert;

/**
 * Tämä luokka demoaa BeanValidation annotaatioiden käyttöä. Toiminta JSF:n yhteydessä tapahtuu automaattisesti.<br/>
 * <b>testaa:</b> http:\\localhost:8080\BeanValidation<br/>
 * tai (http:\\localhost:8080\KelainDocumentation)<br/>
 * ProgrammaticValidatorTest.java taas sisältää yksinkertaisen esimerkin, kuinka bean validationin saa suoritettua
 * ohjelmallisesti.
 */
// Erillinen Hibernate validaatio kun halutaan tarkistaa esim. kahden päivämäärän korrelointia
// Huom. ehto on jokseenkin fiktiivinen, demoaa vain toimintaa.
// Null checkit tarvitaan jotta ei heitä hämärää exceptiota jos arvot puuttuu
@ScriptAssert(lang = "javascript", script = "(_this.birthDay == null && _this.dyingDay == null) || _this.birthDay.before(_this.dyingDate)", message = "{validointi.vaara_synt_aika}")
public class BeanValidationTO {

    // NotNull pitää olla aina jos myös null vaihtoehto pitää tarkistaa. Tämä ei sisälly annetun merkkimäärän
    // tarkistamiseen.
    // message-attribuutille voidaan määrittää avaimet ja näiden arvot löytyvät 'ValidationMessages.properties'
    // tiedostosta
    @NotNull(message = "{validointi.etunimen_pituus}")
    // Tarkistaa annettujen merkkien määrän, mutta ei null vaihtoehtoa
    @Size(min = 2, max = 32, message = "{validointi.etunimen_pituus}")
    private String firstName;

    @NotNull(message = "{validointi.sukunimen_pituus}")
    @Size(min = 2, max = 64, message = "{validointi.sukunimen_pituus}")
    private String lastName;
    @NotNull(message = "{validointi.kutsumanimi}")
    // Mikäli halutaan suorittaa regexpiin perustuva validointi
    @Pattern(regexp = "[a-zA-Z0-9]{3,16}", message = "{validointi.kutsumanimi}")
    private String callName;
    @NotNull(message = "{validointi.pinkoodi}")
    @Size(min = 4, max = 8, message = "{validointi.pinkoodi}")
    // Käytettävissä numeerisille arvoilla
    // @Min(value = 1000, message = "{validointi.pinkoodi}")
    private String pin;
    // @NotNull(message = "{validointi.sukupuoli}")
    private String sex;
    // @NotNull(message = "{validointi.synt_paiva_pakollinen}")
    private Date birthDay;
    private Date dyingDate;
    // Make the validator to proceed to contained object
    @Valid
    private SubBVTO sub;

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCallName() {
        return this.callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthDay() {
        return this.birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public Date getDyingDate() {
        return this.dyingDate;
    }

    public void setDyingDate(Date dyingDate) {
        this.dyingDate = dyingDate;
    }

    public SubBVTO getSub() {
        return this.sub;
    }

    public void setSub(SubBVTO sub) {
        this.sub = sub;
    }

    // Mikäli normaalit validaatio annotaatiot eivät riitä, voidaan tällä tavoin vaihtoehtoisesti määrittää kenttien
    // sisällön ristiin tarkistusta
    /**
     * Tarkistaa että syntymäpäivä on asetettu, mikäli hahmon sukupuoli on valittu.
     *
     * @return <code>True</code> jos syntymäpäivä on ok, <code>false</code> jos syntymäpäivä puuttuu kun sukupuoli on
     *         valittu
     */
    @AssertTrue(message = "Jos sukupuoli on tiedossa, täytyy olla myös syntymäpäivä.")
    private boolean isValid() {

        String s = this.getSex();
        if ( s != null ) {
            Date bDay = this.getBirthDay();
            if ( bDay == null ) {
                return false;
            }
        }
        return true;
    }

    // Voidaan asettaa myös useampia kuin yksi erikseen suoritettava validointi
    // @AssertTrue(message = "Pakkovirhe")
    public boolean isValidKans() {

        return false;
    }
}
