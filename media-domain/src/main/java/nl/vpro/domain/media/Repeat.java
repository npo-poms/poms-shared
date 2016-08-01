package nl.vpro.domain.media;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.persistence.Embeddable;
import javax.persistence.Column;


@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "repeatType", propOrder = {
        "value"
        })
public class Repeat implements Serializable {

    @Column(name = "reruntext")
    @XmlValue
    protected String value;

    @Column(nullable = true)
    @XmlAttribute(required = true)
    protected boolean isRerun;

    public Repeat(boolean isRerun, String value) {
        this.isRerun = isRerun;
        this.value = value;
    }

    public Repeat() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRerun() {
        return isRerun;
    }

    public Repeat(Repeat source) {
        this.value = source.value;
        this.isRerun = source.isRerun;
    }

    public static Repeat copy(Repeat source) {
        if(source == null) {
            return null;
        }

        return new Repeat(source);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repeat repeat = (Repeat) o;

        if (isRerun != repeat.isRerun) return false;
        if (value != null ? !value.equals(repeat.value) : repeat.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (isRerun ? 1 : 0);
        return result;
    }
}
