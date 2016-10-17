package nl.vpro.domain.media;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Embeddable
@XmlAccessorType(XmlAccessType.NONE)
public class Lifestyle {

    @XmlValue
    @Column(name = "primaryLifestyle")
    private String value;


    protected Lifestyle() {
    }

    public Lifestyle(String value) {
        this.value = value;
    }

    public Lifestyle(Lifestyle source) {
        this(source.value);
    }

    public static Lifestyle copy(Lifestyle source) {
        if(source == null) {
            return null;
        }
        return new Lifestyle(source);
    }

    public String getDisplayValue() {
        return value;
    }

}
