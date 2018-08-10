package nl.vpro.domain.media.bind;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
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
    @XmlJavaTypeAdapter(CountryCodeAdapter.Code.class)
    private  CountryCode code;

    public CountryWrapper() {
    }

    public CountryWrapper(String code) {
        this.code = CountryCode.valueOf(code);
    }

    public CountryWrapper(CountryCode code) {
        if (code == null) {
            throw new IllegalArgumentException();
        }
        this.code = code;
    }

    @XmlValue
    @JsonProperty("value")
    public String getName() {
        return Locales.getCountryName(code,
            Locales.NETHERLANDISH
        );
    }

    public void setName(String name) {
        // i hate jaxb
    }

    public CountryCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ":" + getName();
    }
}
