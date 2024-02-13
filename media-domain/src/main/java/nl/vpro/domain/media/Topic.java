package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.*;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;

/**
 * A Topic is a wrapper around a GTAARecord linking it to a Topics record.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(of = { "gtaaRecord" })
@EqualsAndHashCode(of = { "gtaaRecord" }, callSuper = false)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "topicType", propOrder = {"name", "scopeNotes", "gtaaUri", "gtaaStatus"})
public class Topic extends DomainObject implements MediaObjectOwnableListItem<Topic, Topics>, GTAARecordManaged {

    @Serial
    private static final long serialVersionUID = -3211405159144788072L;

    @ManyToOne(fetch = FetchType.LAZY)
    @XmlTransient
    private Topics parent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gtaa_uri", nullable = false)
    @XmlTransient
    private GTAARecord gtaaRecord;

    public Topic() {
        gtaaRecord = new GTAARecord();
    }

    @lombok.Builder
    private Topic(Long id,
                  String name,
                  @Singular List<String> scopeNotes,
                  @NonNull String uri,
                  GTAAStatus gtaaStatus) {

        this.id = id;
        this.gtaaRecord = GTAARecord.builder()
                .name(name)
                .scopeNotes(scopeNotes)
                .uri(uri)
                .status(gtaaStatus)
                .build();
    }

    @Override
    @XmlElement
    public String getName() {
        return GTAARecordManaged.super.getName();
    }
    @Override
    public void  setName(String name) {
        GTAARecordManaged.super.setName(name);
    }

    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    @Override
    public List<String> getScopeNotes() {
        return GTAARecordManaged.super.getScopeNotes();
    }

    @Override
    public void setScopeNotes(List<String> scopeNotes) {
        GTAARecordManaged.super.setScopeNotes(scopeNotes);
    }

    @Override
    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return GTAARecordManaged.super.getGtaaStatus();
    }
    @Override
    public void setGtaaStatus(GTAAStatus status) {
        GTAARecordManaged.super.setGtaaStatus(status);
    }

    @Override
    @XmlAttribute
    public String  getGtaaUri() {
        return GTAARecordManaged.super.getGtaaUri();
    }
    @Override
    public void setGtaaUri(String uri) {
        GTAARecordManaged.super.setGtaaUri(uri);
    }
    @Override
    public int compareTo(Topic topic) {
        if (getGtaaUri() != null) {
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
}
