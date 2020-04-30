package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Embeddable
@XmlAccessorType(XmlAccessType.NONE)
public class SecondaryLifestyle implements Serializable, Displayable {

    @XmlValue
    @Column(name = "secondaryLifestyle")
    private String value;

    protected SecondaryLifestyle() {
    }

    public SecondaryLifestyle(String value) {
        this.value = value;
    }

    public SecondaryLifestyle(SecondaryLifestyle source) {
        this(source.value);
    }

    public static SecondaryLifestyle copy(SecondaryLifestyle source) {
        return source == null ? null : new SecondaryLifestyle(source);
    }

    @Override
    public String getDisplayName() {
        return value;
    }


}
