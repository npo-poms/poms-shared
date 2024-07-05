package nl.vpro.domain.media;

import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;


@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "repeatType", propOrder = {
        "value"
        })


public class Repeat implements Serializable {

    @Serial
    private static final long serialVersionUID = -5535721887986056773L;

    @Setter
    @Column(name = "reruntext")
    @XmlValue
    protected String value;

    @Column(nullable = true)
    @XmlAttribute(required = true)
    protected boolean isRerun;

    public static Repeat rerun(String value) {
        return new Repeat(true, value);
    }

    public static Repeat original(String value) {
        return new Repeat(false, value);
    }

    public static Repeat rerun() {
        return new Repeat(true, "");
    }

    public static Repeat original() {
        return new Repeat(false, "");
    }

    public static Repeat nullIfDefault(Repeat repeat) {
        if (repeat == null) {
            return repeat;
        }
        if (!repeat.isRerun && StringUtils.isBlank(repeat.value)) {
            return null;

        }
        return repeat;
    }
    public static boolean isRerun(Repeat repeat) {
        return repeat != null && repeat.isRerun();
    }
    public static boolean isOriginal(Repeat repeat) {
        return repeat == null || ! repeat.isRerun();
    }

    public Repeat(boolean isRerun, String value) {
        this.isRerun = isRerun;
        this.value = value;
    }

    public Repeat() {
    }

    public String getValue() {
        return value;
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
        return value != null ? value.equals(repeat.value) : repeat.value == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (isRerun ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return isRerun ? "RERUN" : "ORIGNAL" + (value == null ? "" : " (" + value + ")");
    }
}
