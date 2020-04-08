package nl.vpro.domain.api.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

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
@Slf4j
public class RedirectList implements Iterable<RedirectEntry> {

    @JsonProperty("map")
    private Map<String, String> redirects = new HashMap<>();

    private Map<String, String> resolvedRedirects;

    // used for XML binding only.
    private List<RedirectEntry> entries;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    Instant lastUpdate;

    public RedirectList() {
        lastUpdate = Instant.EPOCH;
    }
    public RedirectList(Instant lastUpdate, Map<String, String> redirects) {
        this.lastUpdate = lastUpdate;
        this.redirects = redirects;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(redirects);
    }

    public Map<String, String> getResolvedMap() {
        if (resolvedRedirects == null) {
            resolvedRedirects = resolvedMap();
        }
        return resolvedRedirects;
    }

    public final Optional<String> redirect(String mid) {
        return Optional.ofNullable(getResolvedMap().get(mid));
    }

    public String put(String key, String value) {
        String put = redirects.put(key, value);
        if (put != null) {
            entries = null;
            resolvedRedirects = null;
        }
        return put;
    }

    private  Map<String, String> resolvedMap() {
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, String> entry : redirects.entrySet()) {
            String value = entry.getValue();
            String to = value;
            Set<String> infinityProtect = new LinkedHashSet<>();
            infinityProtect.add(value);
            while (to != null) {
                to = redirects.get(to);
                if (to != null) {
                    value = to;
                    if (!infinityProtect.add(value)) {
                        log.info("Detected circular redirecting, breaking here: {}", infinityProtect);
                        value = entry.getKey();
                        break;
                    }


                }
            }
            result.put(entry.getKey(), value);
        }
        return Collections.unmodifiableMap(result);
    }

    public List<RedirectEntry> getList() {
        return Collections.unmodifiableList(toList());
    }


    private List<RedirectEntry> toList() {
        return getMap()
            .entrySet()
            .stream()
            .map(e -> {
                String ultimate = redirect(e.getKey()).orElse(null);
                return new RedirectEntry(e.getKey(), e.getValue(), ultimate);
            })
            .collect(Collectors.toList());
    }

    @XmlElement(name = "entry", namespace = Xmlns.API_NAMESPACE)
    @JsonIgnore
    protected List<RedirectEntry> getXmlList() {
        if (entries == null) {
            entries = toList();
        }
        return entries;

    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public int size() {
        return redirects.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedirectList that = (RedirectList) o;

        if (lastUpdate != null ? !lastUpdate.equals(that.lastUpdate) : that.lastUpdate != null) {
            return false;
        }
        if (redirects != null ? !redirects.equals(that.redirects) : that.redirects != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = redirects != null ? redirects.hashCode() : 0;
        result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public Iterator<RedirectEntry> iterator() {
        return getList().iterator();
    }

    protected void reduceXmlHelper() {
        if (entries != null) {
            redirects = new HashMap<>();
            for (RedirectEntry e : entries) {
                redirects.put(e.getFrom(), e.getTo());
            }
            entries = null;
        }
    }
    void afterUnmarshal(Unmarshaller u, Object parent) {
        reduceXmlHelper();
    }
}
