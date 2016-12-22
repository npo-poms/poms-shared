package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "redirects")
public class RedirectList implements Iterable<RedirectEntry> {

    Map<String, String> redirects;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant lastUpdate;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant lastChange;

    public RedirectList() {
        lastUpdate = Instant.EPOCH;
    }
    public RedirectList(Instant lastUpdate, Instant lastChange, Map<String, String> redirects) {
        this.lastUpdate = lastUpdate;
        this.lastChange = lastChange;
        this.redirects = redirects;
    }

    @JsonProperty
    public Map<String, String> getMap() {
        if (redirects == null) {
            redirects = new HashMap<>();
        }
        return redirects;
    }

    @XmlElement(name = "entry", namespace = Xmlns.API_NAMESPACE)
    @JsonIgnore
    public List<RedirectEntry> getList() {
        return getMap()
            .entrySet()
            .stream()
            .map(RedirectEntry::new)
            .collect(Collectors.toList());
    }

    public void setMap(Map<String, String> redirects) {
        this.redirects = redirects;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public Instant getLastChange() {
        return lastChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedirectList that = (RedirectList) o;

        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) return false;
        if (redirects != null ? !redirects.equals(that.redirects) : that.redirects != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = redirects != null ? redirects.hashCode() : 0;
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }

    @Override
    public Iterator<RedirectEntry> iterator() {
        return getList().iterator();

    }
}
