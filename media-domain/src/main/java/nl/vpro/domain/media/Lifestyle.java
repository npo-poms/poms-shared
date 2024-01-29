package nl.vpro.domain.media;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.*;

import nl.vpro.i18n.Displayable;


/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Embeddable
@XmlAccessorType(XmlAccessType.NONE)
public class Lifestyle implements Serializable, Displayable {

    @Serial
    private static final long serialVersionUID = 8981980609550119820L;

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

    @Override
    public String getDisplayName() {
        return value;
    }

}
