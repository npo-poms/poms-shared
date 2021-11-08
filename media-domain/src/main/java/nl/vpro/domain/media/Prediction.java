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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static javax.persistence.EnumType.STRING;
import static nl.vpro.domain.Changeables.instant;

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
/*

@Table(
    uniqueConstraints = {@UniqueConstraint(columnNames = {"mediaobject_id", "platform"})}
)
*/


public class Prediction implements Comparable<Prediction>, Updatable<Prediction>, Serializable, MutableEmbargo<Prediction>, Child<MediaObject> {

    private static final long serialVersionUID = 0L;


    @XmlEnum
    @XmlType(name = "predictionStateEnum")
    public enum State implements Displayable {
        /**
         * This state should only be used for non persistent Prediction objects (as they are used in the GUI)
         */
        VIRTUAL(""),
         /**
         * This state should only be used for predictions that have no planned availability, and therefore are not published to frontend api's either!
         */
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
    @Getter
    @Setter
    protected Instant issueDate = instant();

    @Enumerated(STRING)
    @Column(nullable = false)
    @XmlAttribute
    @NotNull
    @Getter
    @Setter
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
    @Enumerated(STRING)
    @Column(length = 16)
    @XmlValue
    @JsonProperty("platform")
    @Getter
    protected Platform platform;

    @Column
    @Enumerated(STRING)
    @XmlTransient
    @Getter
    private Authority authority = Authority.USER;



    /**
     * TODO The state is 'ANNOUNCED', so shouldn't we name this field 'announcedAvailability'?
     * @since 5.6
     */
    @Column
    @XmlTransient
    @Getter
    private boolean plannedAvailability = true;


    @ManyToOne
    @XmlTransient
    protected MediaObject mediaObject;

    @Column
    @Enumerated(STRING)
    @XmlTransient
    @Getter
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
        Boolean plannedAvailability,
        Authority authority,
        State state,
        Encryption encryption,
        MediaObject parent) {
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        this.plannedAvailability = plannedAvailability == null ? true : plannedAvailability;
        this.authority = authority == null ? Authority.USER : authority;
        this.state = state == null ? State.ANNOUNCED : state;
        this.encryption = encryption;
        this.mediaObject = parent;
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

    public static Prediction unavailable(MediaObject parent, Platform platform, Authority authority) {
        return Prediction.builder()
            .plannedAvailability(false)
            .platform(platform)
            .authority(authority)
            .parent(parent)
            .build();
    }
    public static Prediction virtual(MediaObject parent, Platform platform, Authority authority) {
        return Prediction.builder()
            .plannedAvailability(false)
            .platform(platform)
            .authority(authority)
            .state(State.VIRTUAL)
            .parent(parent)
            .build();
    }

    public static Prediction.Builder announced() {
        return builder().state(State.ANNOUNCED);
    }
    public static Prediction.Builder revoked() {
        return builder().state(State.REVOKED);
    }
    public static Prediction.Builder realized() {
        return builder().state(State.REALIZED);
    }


    @Override
    public void setParent(MediaObject mo) {
        this.mediaObject = mo;

    }

    @Override
    public MediaObject getParent() {
        return this.mediaObject;

    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }


    @NonNull
    @Override
    public Prediction setPublishStartInstant(Instant start) {
        this.publishStart = start;
        invalidateXml();
        return this;
    }


    @Override
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public Prediction setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        invalidateXml();
        return this;
    }


    public void setPlatform(Platform platform) {
        this.platform = platform;
        invalidateXml();
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
        invalidateXml();
    }

    public void setPlannedAvailability(boolean plannedAvailability) {
        this.plannedAvailability = plannedAvailability;
        if (this.plannedAvailability) {
            if (this.state == State.NOT_ANNOUNCED) {
                this.state = State.ANNOUNCED;
            }
        } else {
            if (this.state == State.ANNOUNCED) {
                this.state = State.NOT_ANNOUNCED;
            }
        }
        invalidateXml();
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
        invalidateXml();
    }

    protected void invalidateXml() {
        if (this.mediaObject != null) {
            this.mediaObject.predictionsForXml = null;

        }
    }

    @Override
    public int compareTo(@NonNull Prediction o) {
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
        encryption = from.encryption;
        plannedAvailability = from.plannedAvailability;
        platform = from.platform;
        authority = from.authority;
        publishStart = from.publishStart;
        publishStop = from.publishStop;
        issueDate = from.issueDate;
    }

    /**
     * // See MSE-3992
     */
    public boolean isDRM() {
        return encryption == Encryption.DRM;
    }


    @Override
    public String toString() {
        return
            "Prediction{id=" + id + ",platform=" + platform  + ", issueDate=" + issueDate + ", state=" + state + ", encryption=" + encryption + "}";
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
        }
        this.plannedAvailability = true;
    }


}
