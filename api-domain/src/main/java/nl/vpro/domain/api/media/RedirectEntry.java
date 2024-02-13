package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace = Xmlns.API_NAMESPACE)
@Getter
@Setter
public class RedirectEntry {
    @XmlAttribute
    private String from;
    @XmlAttribute
    private String to;

    /**
     * @since 5.13
     */
    @XmlAttribute
    private String ultimate;


    public RedirectEntry() {
    }

    public RedirectEntry(String from, String to, String ultimate) {
        this.from = from;
        this.to = to;
        this.ultimate = ultimate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedirectEntry that = (RedirectEntry) o;

        if (from != null ? !from.equals(that.from) : that.from != null) {
            return false;
        }
        return to != null ? to.equals(that.to) : that.to == null;
    }

    @XmlAttribute
    public Boolean getCircular() {
        return ultimate == null ? true : null;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return from + " -> " + to + (Objects.equals(to, ultimate) ? "" : (" ..-> " + ultimate));
    }

}
