package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.Repeat;


@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "repeatType", propOrder = {
        "value"
        })


public class RepeatUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = -5535721887986056773L;

    @XmlValue
    @Getter
    @Setter
    protected String value;

    @XmlAttribute(required = true)
    @Getter
    protected boolean isRerun;


    public static RepeatUpdate of(Repeat repeat){
        return repeat == null ? null : new RepeatUpdate(repeat);
    }

    public static Repeat asRepeat(RepeatUpdate repeat){
        return repeat == null ? null : new Repeat(repeat.isRerun(), repeat.getValue());
    }



    public RepeatUpdate(boolean isRerun, String value) {
        this.isRerun = isRerun;
        this.value = value;
    }

    public RepeatUpdate() {
    }

    public RepeatUpdate(Repeat source) {
        this.value = source.getValue();
        this.isRerun = source.isRerun();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepeatUpdate repeat = (RepeatUpdate) o;

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
