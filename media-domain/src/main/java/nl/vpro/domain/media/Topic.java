package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "topicType", propOrder = {
        "name",
        "scopeNotes",
        "gtaaUri",
        "gtaaStatus"
})
public class Topic extends DomainObject implements MediaObjectOwnableListItem<Topic, Topics> {

    @ManyToOne(targetEntity = Topics.class, fetch = FetchType.LAZY)
    @XmlTransient
    private Topics parent;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, targetEntity = GTAARecord.class)
    @JoinColumn(name = "gtaa_id")
    @XmlTransient
    private GTAARecord gtaaRecord = new GTAARecord();

    public Topic() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private Topic(Long id,
                  @NonNull String name,
                  @Singular List<String> scopeNotes,
                  @NonNull URI uri,
                  GTAAStatus gtaaStatus) {

        this.id = id;
        this.gtaaRecord = GTAARecord.builder()
                .name(name)
                .scopeNotes(scopeNotes)
                .uri(uri)
                .status(gtaaStatus)
                .build();
    }

    @XmlElement
    public String getName() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getName)
                .orElse(null);
    }

    public void setName(String name) {
        gtaaRecord.setName(name);
    }

    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    public List<String> getScopeNotes() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getScopeNotes)
                .orElse(new ArrayList<>());
    }

    public void setScopeNotes(List<String> scopeNotes) {
        gtaaRecord.setScopeNotes(scopeNotes);
    }

    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getStatus)
                .orElse(null);
    }

    public void setGtaaStatus(GTAAStatus gtaaStatus) {
        gtaaRecord.setStatus(gtaaStatus);
    }

    @XmlAttribute
    public URI getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getUri)
                .orElse(null);
    }

    public void setGtaaUri(URI uri) {
        gtaaRecord.setUri(uri);
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (object.getClass() != getClass()) {
            return false;
        }

        if (super.equals(object)) {
            return true;
        }

        Topic topic = (Topic) object;
        return new EqualsBuilder()
                .append(getName(), topic.getName())
                .append(getScopeNotes(), topic.getScopeNotes())
                .append(getGtaaUri(), topic.getGtaaUri())
                .isEquals();
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getScopeNotes())
                .append(getGtaaUri())
                .toHashCode();
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("scopeNotes", getScopeNotes())
                .append("gtaa_uri", getGtaaUri())
                .toString();
    }

    @Override
    public int compareTo(Topic topic) {

        if (getName() != null && topic.getName() != null && !getName().equals(topic.getName())) {
            return getName().compareTo(topic.getName());
        }

        if (getGtaaUri() != null && topic.getGtaaUri() != null && !getGtaaUri().equals(topic.getGtaaUri())) {
            return getGtaaUri().compareTo(topic.getGtaaUri());
        }

        return 0;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Topic clone() {

        Topic clone = new Topic();
        clone.setParent(parent);
        clone.setGtaaRecord(gtaaRecord);
        return clone;
    }

    public static class Builder {

        public Topic.Builder gtaaUri(String uri) {
            return uri(URI.create(uri));
        }
    }
}
