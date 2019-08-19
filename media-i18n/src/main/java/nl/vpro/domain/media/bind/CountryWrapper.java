package nl.vpro.domain.media.bind;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.meeuw.i18n.Region;
import org.meeuw.i18n.bind.jaxb.Code;
import org.meeuw.i18n.countries.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.i18n.Locales;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "countryType", propOrder = {"name"})
@JsonPropertyOrder({"code", "value"})
public class CountryWrapper {

    @XmlAttribute
    @XmlJavaTypeAdapter(Code.class)
    private Region code;

    public CountryWrapper() {
    }

    public CountryWrapper(String code) {
        this.code = Country.getByCode(code).orElseThrow(() -> new IllegalArgumentException("no such country " + code));
    }

    public CountryWrapper(Region code) {
        if (code == null) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }

    @XmlValue
    @JsonProperty("value")
    public String getName() {
        return Locales.getCountryName(code,  Locales.NETHERLANDISH);
    }

    public void setName(String name) {
        // i hate jaxb
    }

    public Region getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ":" + getName();
    }
}
