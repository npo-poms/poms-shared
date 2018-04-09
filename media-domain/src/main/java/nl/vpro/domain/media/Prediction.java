package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.Displayable;
import nl.vpro.domain.Embargo;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * A prediction is related to a program and indicates that locations (for a certain platform) <em>will be</em> available.
 *
 * Also it contains information about the embargo restrictions for that platform.
 *
 *
 * @author Michiel Meeuwissen
 * @since 1.6
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "predictionType",
        propOrder = {
})
@Table(
    uniqueConstraints = {@UniqueConstraint(columnNames = {"mediaobject_id", "platform"})}
)
public class Prediction implements Comparable<Prediction>, Updatable<Prediction>, Serializable, Embargo, Child<MediaObject> {

    private static final long serialVersionUID = 0L;


    @XmlEnum
    @XmlType(name = "predictionStateEnum")
    public enum State implements Displayable {
        /**
         * This state should only be used for non persistent Prediction objects (as they are used in the GUI)
         */
        VIRTUAL(""),
        NOT_ANNOUNCED(""),
        ANNOUNCED("Aangekondigd"),
        REALIZED("Gerealiseerd"),
        REVOKED("Teruggetrokken");

        @Getter
        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @XmlTransient
    @Column(nullable = false)
    @NotNull
    protected Instant issueDate = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute
    @NotNull
    protected State state = State.ANNOUNCED;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStart;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStop;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    @XmlValue
    @JsonProperty("platform")
    protected Platform platform;

    @Column
    @Enumerated(EnumType.STRING)
    @XmlTransient
    private Authority authority = Authority.USER;

    @Column
    @XmlTransient
    @Setter
    @Getter
    private boolean plannedAvailability = true;


    @ManyToOne
    @XmlTransient
    protected MediaObject mediaObject;

    @Column
    @Enumerated(EnumType.STRING)
    @XmlTransient
    @Getter
    @Setter
    protected Encryption encryption;

    public Prediction() {
    }

    /**
     * Constructor needed for jackson unmarshalling
     */
    public Prediction(String platform) {
        this.platform = Platform.valueOf(platform);
    }


    public Prediction(Platform platform) {
        this.platform = platform;
    }

    public Prediction(Platform platform, State state) {
        this.platform = platform;
        this.state = state;
    }

    public Prediction(Platform platform, Instant publishStart, Instant publishStop) {
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
    }


    @lombok.Builder
    private Prediction(
        Platform platform,
        Instant publishStart,
        Instant publishStop,
        boolean plannedAvailability,
        Authority authority,
        State state,
        Encryption encryption) {
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        this.plannedAvailability = plannedAvailability;
        this.authority = authority == null ? Authority.USER : authority;
        this.state = state == null ? State.ANNOUNCED : state;
        this.encryption = encryption;

    }

    public Prediction(Prediction source) {
        this(source, source.mediaObject);
    }

    public Prediction(Prediction source, MediaObject parent) {
        this(source.getPlatform(), source.getPublishStartInstant(), source.getPublishStopInstant());
        this.issueDate = source.issueDate;
        this.state = source.state;
        this.mediaObject = parent;
        this.plannedAvailability = source.plannedAvailability;
        this.encryption = source.encryption;
    }

    public static Prediction copy(Prediction source){
        return copy(source, source.mediaObject);
    }

    public static Prediction copy(Prediction source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Prediction(source, parent);
    }

    public static Prediction unavailable(Platform platform, Authority authority) {
        return Prediction.builder()
            .plannedAvailability(false)
            .platform(platform)
            .authority(authority)
            .build();
    }
    public static Prediction virtual(Platform platform, Authority authority) {
        return Prediction.builder()
            .plannedAvailability(false)
            .platform(platform)
            .authority(authority)
            .state(State.VIRTUAL)
            .build();
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }


    @Deprecated
    public MediaObject getMediaObject() {
        return mediaObject;
    }

    @Deprecated
    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }


    @Override
    public void setParent(MediaObject mo) {
        this.mediaObject = mo;

    }

    @Override
    public MediaObject getParent() {
        return this.mediaObject;

    }

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }


    @Override
    public Prediction setPublishStartInstant(Instant start) {
        this.publishStart = start;
        return this;
    }


    @Override
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @Override
    public Prediction setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }


    @Override
    public int compareTo(Prediction o) {
        if (platform == null) {
            return o == null ? 0 : o.platform == null ? 0 : 1;
        } else {
            return o == null || o.platform == null ? 1 : platform.compareTo(o.platform);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prediction)) {
            return false;
        }
        Prediction other = (Prediction) o;
        if (platform == null || other.platform == null) {
            return other == this;
        }
        return Objects.equals(platform, other.platform);

    }

    @Override
    public int hashCode() {
        return platform.hashCode();
    }

    @Override
    public void update(Prediction from) {
        state = from.state;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }


    @Override
    public String toString() {
        return
            "Prediction{id=" + id + ",platform=" + platform  + ", issueDate=" + issueDate + ", state=" + state + "}";
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
        }
        this.plannedAvailability = true;
    }


}
