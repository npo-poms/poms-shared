package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.*;

import nl.vpro.i18n.Displayable;


/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@Embeddable
@XmlAccessorType(XmlAccessType.NONE)
public class SecondaryLifestyle implements Serializable, Displayable {

    private static final long serialVersionUID = -2203208005840440189L;

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
