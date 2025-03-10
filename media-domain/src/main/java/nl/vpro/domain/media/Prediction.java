package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.*;
import nl.vpro.i18n.Displayable;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.Ranges;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static jakarta.persistence.EnumType.STRING;
import static nl.vpro.domain.Changeables.instant;

/**
 * A prediction is related to a program and indicates that locations (for a certain platform) <em>will be</em> available, and nowadays also whether it <em>is</em> or <em>was</em> available.
 * <p>
 * Also, it contains information about the embargo restrictions for that platform.
 * <p>
 * There may be an {@link #getEncryption() encryption} associated. If set then the prediction will only be valid for the platform and that given prediction. If not set, normally both encrypted and non-encrypted locations will be present.
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

@Slf4j
public class Prediction implements Comparable<Prediction>, Updatable<Prediction>, Serializable, MutableEmbargo<Prediction>, Child<MediaObject> {

    @Serial
    private static final long serialVersionUID = 0L;


    @XmlEnum
    @XmlType(name = "predictionStateEnum")
    @Getter
    public enum State implements Displayable {

        /**
         * This state should only be used for non-persistent Prediction objects (as they are used in the GUI)
         */
        VIRTUAL(""),
         /**
         * This state should only be used for predictions that have no planned availability, and therefore are not published to frontend api's either!
         */
        NOT_ANNOUNCED(""),
        ANNOUNCED("Aangekondigd"),
        REALIZED("Gerealiseerd"),
        REVOKED("Teruggetrokken");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }


        public static State of(@NonNull Embargo embargo) {
            final Instant instant = instant();
            if (embargo.inPublicationWindow(instant) ) {
                return REALIZED;
            }
            if (embargo.wasUnderEmbargo(instant)) {
                return REVOKED;
            }
            return ANNOUNCED;
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
    protected State state = State.ANNOUNCED;


    @Transient
    @Getter
    protected transient State previousState;


    @XmlTransient
    protected Instant publishStart;


    @XmlTransient
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
     *
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
        MediaObject parent,
        Long id) {
        this.id = id;
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        this.plannedAvailability = plannedAvailability == null ? true : plannedAvailability;
        this.authority = authority == null ? Authority.USER : authority;
        this.state = state == null ? State.ANNOUNCED : state;
        this.encryption = encryption;
        this.mediaObject = parent;
    }

    private Prediction(Prediction source) {
        this(source, source.mediaObject, source.authority);
    }

    /**
     * Created a new prediction, based on the source, but with a different parent,
     */
    private Prediction(Prediction source, MediaObject parent, Authority authority) {
        this(source.getPlatform(), source.getPublishStartInstant(), source.getPublishStopInstant());
        this.issueDate = source.issueDate;
        this.state = source.state;
        this.mediaObject = parent;
        this.plannedAvailability = source.plannedAvailability;
        this.encryption = source.encryption;
        this.authority = authority;
    }

    public void copyFrom(Prediction source) {
        this.platform = source.platform;
        this.publishStart = source.publishStart;
        this.publishStop = source.publishStop;
        this.state = source.state;
        this.plannedAvailability = source.plannedAvailability;
        this.encryption = source.encryption;
        this.authority = source.authority;
    }

    /**
     *
     */
    public static Prediction copy(Prediction source, Authority authority) {
        if (source == null) {
            return null;
        }
        return new Prediction(source, source.mediaObject, authority);
    }

    /**
     * Copied a prediction to a new parent, with authority {@link Authority#USER}
     */
    public static Prediction copy(Prediction source, MediaObject parent){
        if(source == null) {
            return null;
        }
        return new Prediction(source, parent, Authority.USER);
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

    public static Prediction.Builder announced(Platform platform) {
        return announced().platform(platform).plannedAvailability(true);
    }

    public static Prediction.Builder announced() {
        return builder().plannedAvailability(true).state(State.ANNOUNCED);
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


    /**
     * Basically the start of a prediction is {@link #getOwnPublishStartInstant()}. If there are locations that are to be published earlier, they won't be published either.
     * <p />
     * If there are no locations, then the assumption is that the publish start is 'predicted' and around that time a notify from NEP fill follow.
     */
    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Override
    public Instant getPublishStartInstant() {
        Instant result =  publishStart;
        /*if (mediaObject != null // can be determined
        ) {
            Instant earliestLocation = Instant.MAX;
            int foundLocations = 0;
            for (Location l : mediaObject.getLocations()) {
                if (l.isDeleted()) {
                    continue;
                }
                if (l.getOwnPublishStopInstant() != null && l.getOwnPublishStopInstant().isBefore(instant())) {
                    // it was published earlier, but not anymore, so ignore this!
                    continue;
                }
                if (platform.matches(l.getPlatform())) {
                    foundLocations++;
                    if (l.getOwnPublishStartInstant() == null) {
                        earliestLocation = null;
                        break;
                    } else if (l.getOwnPublishStartInstant().isBefore(earliestLocation)) {
                        earliestLocation = l.getOwnPublishStopInstant();
                    }
                }
            }
            if (foundLocations > 0) {
                if (earliestLocation != null && earliestLocation.isAfter(result)) {
                    result = earliestLocation;
                }
            }

        }*/
        return result;
    }


    @NonNull
    @Override
    public Prediction setPublishStartInstant(Instant start) {
        this.publishStart = start;
        invalidateXml();
        return this;
    }


    /**
     * When this prediction will be revoked.
     *<p>
     * If all locations will be revoked before the {@link #getOwnPublishStopInstant() registered publishstop in} this prediction, then this method will return the latest value of that.
     * @see #getOwnPublishStopInstant()
     */
    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Override
    public Instant getPublishStopInstant() {
        Instant result =  publishStop;
        if (mediaObject != null) {
            Instant latestLocation = Instant.MIN;
            int foundLocations = 0;
            for (Location l : mediaObject.getLocations()) {
                if (l.isDeleted()) {
                    continue;
                }
                if (platform.matches(l.getPlatform())) {
                    foundLocations++;
                    if (l.getOwnPublishStopInstant() == null) {
                        latestLocation = null;
                        break;
                    } else if (l.getOwnPublishStopInstant().isAfter(latestLocation)) {
                        latestLocation = l.getOwnPublishStopInstant();
                    }
                }
            }
            if (foundLocations > 0) {
                if (result == null || (latestLocation != null && latestLocation.isBefore(result))) {
                    result = latestLocation;
                }
            }

        }
        return result;
    }

    /**
     * Returns the 'own' embargo start. Currently, always the same as {@link #getPublishStartInstant()}
     * @see #getPublishStartInstant()
     * @see #getOwnEmbargo()
     * @since 7.10
     */
    public Instant getOwnPublishStartInstant() {
        return publishStart;
    }

    /**
     * Returns the 'own' embargo stop. {@link #getPublishStopInstant()} may be restricted by available locations.
     * @see #getPublishStopInstant()
     * @see #getOwnEmbargo()
     * @since 7.10
     */
    public Instant getOwnPublishStopInstant() {
        return publishStop;
    }

    /**
     * 'Own' embargo wrapped in a {@link Range}.
     * <p>
     * @since 7.10
     * @see #asRange()
     * @see #getOwnEmbargo()
     */
    public Range<Instant> getOwnPublicationRange() {
        try {
            return Ranges.closedOpen(getOwnPublishStartInstant(), getOwnPublishStopInstant());
        } catch (IllegalArgumentException iae) {
            log.error("For {} {} - {} is not a valid range", this, getOwnPublishStartInstant(), getOwnPublishStopInstant());
            return Ranges.closedOpen(null, getOwnPublishStopInstant());
        }
    }

    /**
     * An embargo object based on {@link #getOwnPublishStartInstant()} and {@link #getOwnPublishStopInstant()}
     * @since 7.10
     * @see #getOwnPublicationRange()
     */
    public MutableEmbargo<?> getOwnEmbargo() {
        return Embargos.of(
            this::getOwnPublishStartInstant, this::setPublishStartInstant,
            this::getOwnPublishStopInstant, this::setPublishStopInstant
        );
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
        if (this.plannedAvailability != plannedAvailability) {
            this.plannedAvailability = plannedAvailability;

            if (this.plannedAvailability) {
                if (this.state == State.NOT_ANNOUNCED) {
                    this.state = State.ANNOUNCED;
                } else if (this.state != State.ANNOUNCED) {
                    log.info("State of prediction already {} is {}. Not setting to ANNOUNCED", this, state);
                }
            } else {
                if (this.state == State.ANNOUNCED) {
                    this.state = State.NOT_ANNOUNCED;
                } else if (this.state != State.NOT_ANNOUNCED) {
                    log.info("State of prediction already {} is {}. Not setting to NOT_ANNOUNCED", this, state);
                }
            }
            invalidateXml();
        }
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
        invalidateXml();
    }


    public void setState(State state) {
        if (previousState == null) {
            previousState = this.state;
        }
        this.state = state;
        invalidateXml();
    }

    protected void invalidateXml() {
        if (this.mediaObject != null) {
            this.mediaObject.predictionsForXml = null;

        }
    }

    @Override
    public int compareTo(@NonNull Prediction o) {
        if (o == null){
            throw new NullPointerException("Cannot compare to null");
        }
        if (platform == null) {
            return o == null ? 0 : o.platform == null ? 0 : 1;
        } else {
            return o == null || o.platform == null ? 1 : platform.compareTo(o.platform);
        }
    }

    /**
     *
     */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prediction other)) {
            return false;
        }
        return equalsIgnoreParent(other) && Objects.equals(getParent(), other.getParent());
    }

    public boolean equalsIgnoreParent(Prediction other) {

        if (platform == null || other.platform == null) {
            return other == this;
        }
        return Objects.equals(platform, other.platform);
    }

    public boolean fieldEquals(Prediction prediction) {
        return equals(prediction) &&
            asRange().equals(prediction.asRange()) &&
            getEncryption() == prediction.getEncryption() &&
            plannedAvailability == prediction.plannedAvailability;
    }

    @Override
    public int hashCode() {
        return platform == null ? 0 : platform.hashCode();
    }

    @Override
    public void update(Prediction from) {
        state = from.state;
        encryption = from.encryption;
        plannedAvailability = from.plannedAvailability;
        platform = from.platform;
        authority = from.authority;
        Embargos.copy(from, this);
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
            "Prediction{id=" + id + ",platform=" + platform  + ", issueDate=" + issueDate + ", state=" + state + ", encryption=" + encryption  + (mediaObject == null ? "" : ", parent=" + mediaObject) + "}";
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
        }
        this.plannedAvailability = true;
    }


    public static class Builder {
        public Builder range(@Nullable Range<Instant> range) {
            if (range != null) {
                if (range.hasLowerBound()) {
                    publishStart(range.lowerEndpoint());
                } else {
                    publishStart(null);
                }
                if (range.hasUpperBound()) {
                    publishStop(range.upperEndpoint());
                } else {
                    publishStop(null);
                }
            }
            return this;
        }
    }


}
