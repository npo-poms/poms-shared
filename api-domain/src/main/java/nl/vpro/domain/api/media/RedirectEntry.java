package nl.vpro.domain.api.media;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(namespace = Xmlns.API_NAMESPACE)
public class RedirectEntry {
    @XmlAttribute
    private String from;
    @XmlAttribute
    private String to;

    public RedirectEntry() {
    }

    public RedirectEntry(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public RedirectEntry(Map.Entry<String, String>  entry) {
        this.from = entry.getKey();
        this.to = entry.getValue();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }

}
