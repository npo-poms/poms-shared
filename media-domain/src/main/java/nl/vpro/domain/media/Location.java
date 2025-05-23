package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.*;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.DurationToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.util.HttpConnectionUtils;
import nl.vpro.util.Ranges;
import nl.vpro.xml.bind.DurationXmlAdapter;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static nl.vpro.domain.Changeables.instant;

/**
 * A location is a wrapper around a {@link #getProgramUrl() url} together with some metadata about it, and basically should be somehow actually playable. It may e.g. represent a downloadable MP3 file. But it can also represent an url with a scheme that can only be understood by a specific NPO player.
 * <p>
 * A {@link MediaObject} can have more than one location which should differ in URL and
 * owner.
 * <p/>
 * The location owner describes an origin of the location. Several media suppliers provide
 * their own locations. To prevent conflicts while updating for incoming data, locations
 * for those suppliers are kept in parallel.
 * <p/>
 * Note that this class confirms to a natural ordering not consistent with equals.
 *
 * @author Roelof Jan Koekoek
 * @see OwnerType
 * @since 0.4
 */
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "locationType",
    propOrder = {"programUrl",
        "avAttributes",
        "subtitles",
        "offset",
        "duration"})

@JsonPropertyOrder({
    "programUrl",
    "avAttributes",
    "owner",
    "creationDate",
    "workflow"
})
@Slf4j
public class Location extends PublishableObject<Location>
    implements MutableOwnable, Comparable<Location>, Child<MediaObject> {
    @Serial
    private static final long serialVersionUID = -140942203904508506L;

    public static final String BASE_URN = "urn:vpro:media:location:";

    private static final Pattern URL_PATTERN = Pattern.compile("^([a-zA-Z0-9+]+)://([^/?]+)(/.*?)?(\\?.*)?(#.*)?");

    @SneakyThrows
    public static String sanitizedProgramUrl(String value) {
        if (value == null) {
            return null;
        }

        Matcher matcher = URL_PATTERN.matcher(value);
        if (matcher.matches()) {
            String scheme = matcher.group(1).toLowerCase();
            String host = matcher.group(2);
            String path = matcher.group(3);
            if (path != null) {
                path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            }
            String query =  matcher.group(4);
            if (query != null) {
                query = URLDecoder.decode(query.substring(1), StandardCharsets.UTF_8);
            }

            String fragment =  matcher.group(5);
            if (fragment != null) {
                fragment= URLDecoder.decode(fragment.substring(1), StandardCharsets.UTF_8);
            }
            return new URI(scheme, host, path, query, fragment ).normalize().toASCIIString();
        } else {
            throw new IllegalArgumentException("Don't know how to sanitize " +  value);
        }
       // return value;
    }

    @Column(nullable = false)
    @XmlElement
    @nl.vpro.validation.Location
    protected String programUrl;

    @XmlElement
    @OneToOne(orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    protected AVAttributes avAttributes;

    @ManyToOne
    @XmlTransient
    protected MediaObject mediaObject;

    @Getter
    @XmlElement
    protected String subtitles;

    @Getter
    @Column(name = "`start_offset`")
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    protected Duration offset;

    @Getter
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = XMLDurationToJsonTimestamp .Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    protected Duration duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute(required = true)
    @NonNull
    protected OwnerType owner = OwnerType.BROADCASTER;


    @Column(nullable = true)
    @XmlTransient
    protected Long neboId;

    @Getter
    @Column(length = 100, updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    protected Platform platform  = Platform.INTERNETVOD;

    /**
     * Note that this {@link XmlTransient} and hence <em>not available in frontend api</em>
     * @see MediaObject#isLocationAuthorityUpdate
     */
    @Getter
    @XmlTransient
    private boolean authorityUpdate = false;

    /**
     *  Note that this {@link XmlTransient} and hence <em>not available in frontend api</em>
     */
    @Getter
    @Setter
    @XmlTransient
    private Integer statusCode;

    /**
     *  Note that this {@link XmlTransient} and hence <em>not available in frontend api</em>
     */
    @Getter
    @Setter
    @XmlTransient
    private Instant lastStatusChange;

    public Location() {
    }

    public Location(@NonNull OwnerType owner) {
        this(null, owner);
    }

    public Location(String programUrl, OwnerType owner) {
        this(programUrl, owner, Platform.INTERNETVOD);
    }

    public Location(String programUrl, OwnerType owner, Platform platform, AVAttributes avAttributes) {
        this(programUrl, owner, avAttributes, null, platform);
    }
    public Location(String programUrl, OwnerType owner, Platform platform) {
        this(programUrl, owner, null, null, platform);
    }

    @Deprecated
    public Location(String programUrl, AVAttributes avAttributes) {
        this(programUrl, null, avAttributes);
    }

    public Location(String programUrl, OwnerType owner, AVAttributes avAttributes) {
        this(programUrl, owner, avAttributes, null, null);
    }


    private Location(String programUrl, @NonNull OwnerType owner, AVAttributes avAttributes, Duration duration, Platform platform) {
        this.programUrl = programUrl == null ? null : programUrl.trim();
        this.owner = owner;
        this.avAttributes = getDefaultAVAttributes(avAttributes, this.programUrl);
        this.workflow = Workflow.PUBLISHED;
        this.duration = duration;
        this.platform = platform == null ? Platform.INTERNETVOD : platform;
    }

    public static class Builder implements EmbargoBuilder<Builder> {


        @Override
        public Builder self() {
            return this;
        }
    }
    /**
     * Unset some default values, to ensure that round tripping will result same object
     * @since 5.11
     */
    @JsonCreator
    static Location jsonCreator() {
        return builder()
            .workflow(null)
            .build();
    }


    @lombok.Builder(builderClassName = "Builder")
    protected Location(
        String programUrl,
        OwnerType owner,
        AVAttributes avAttributes,
        Duration duration,
        Integer bitrate,
        AVFileFormat avFileFormat,
        AudioAttributes audioAttributes,
        VideoAttributes videoAttributes,
        Platform platform,
        Instant publishStart,
        Instant publishStop,
        Workflow workflow,
        Instant creationDate,
        Long byteSize,
        Long id
    ) {
        this(programUrl, owner == null ? OwnerType.BROADCASTER : owner, avAttributes, duration, platform);

        if (bitrate != null || avFileFormat != null || audioAttributes != null || videoAttributes != null || byteSize != null) {
            this.avAttributes = AVAttributes
                .builder()
                .bitrate(bitrate == null ? this.avAttributes.getBitrate() : bitrate)
                .avFileFormat(avFileFormat == null ? this.avAttributes.getAvFileFormat() : avFileFormat)
                .audioAttributes(audioAttributes == null ? this.avAttributes.getAudioAttributes() : audioAttributes)
                .videoAttributes(videoAttributes == null ? this.avAttributes.getVideoAttributes() : videoAttributes)
                .byteSize(byteSize == null ? this.avAttributes.getByteSize() : byteSize)
                .build();
        }
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        // doesn't need its own
        this.workflow = requireNonNullElse(workflow, Workflow.PUBLISHED);
        this.creationInstant = creationDate == null ? Changeables.instant() : creationDate;
        this.id = id;
    }

    public Location(Location source) {
        this(source, source.mediaObject);
    }

    public Location(Location source, MediaObject parent) {
        super(source);

        this.programUrl = source.programUrl;
        this.avAttributes = AVAttributes.copy(source.avAttributes);
        this.subtitles = source.subtitles;
        this.offset = source.offset;
        this.duration = source.duration;
        this.owner = source.owner;
        this.neboId = source.neboId;
        this.platform = source.platform;
        this.authorityUpdate = source.authorityUpdate;

        this.mediaObject = parent;
    }


    public static Location copy(Location source){
        if (source == null) {
            return null;
        }
        return copy(source, source.mediaObject);
    }

    public static Location copy(Location source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Location(source, parent);
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(BASE_URN.length());
        return Long.valueOf(id);
    }

    public static String urnForId(long id) {
        return BASE_URN + id;
    }

    public static Location update(Location from, Location to, OwnerType owner) {
        if(from != null) {
            if(to == null) {
                to = new Location(owner);
            }

            if(to.getOwner() != null && !Objects.equals(owner, to.getOwner())) {
                log.info("Updating owner of {} {} -> {}", to, to.getOwner(), owner);
            }
            boolean newProgramUrl = !Objects.equals(from.getProgramUrl(), to.getProgramUrl());
            if (newProgramUrl) {
                to.setProgramUrl(from.getProgramUrl());
            }
            to.setDuration(from.getDuration());
            to.setOffset(from.getOffset());
            to.setSubtitles(from.getSubtitles());
            to.setPublishStartInstant(from.getOwnPublishStartInstant());
            to.setPublishStopInstant(from.getOwnPublishStopInstant());

            to.setAvAttributes(AVAttributes.update(from.getAvAttributes(), to.getAvAttributes()));

            if (newProgramUrl) {
                to.headRequest();
            }
            if (from.getWorkflow() != null) {
                to.setWorkflow(from.getWorkflow());
            }

        } else {
            to = null;
        }

        return to;
    }


    /**
     * Defaulting version of {@link #headRequest(Duration)} (1 day)
     * @since 7.10
     */
    public boolean headRequest() {
        return headRequest(Duration.ofDays(1));
    }

    /**
     * Executes a head request on the current location (if that seems possible), and updates some fields if needed
     * @since 7.10
     * @return Whether changes were made
     * @param age The age of the last status change, if it is younger, no request will be made
     */
    public boolean headRequest(Duration age) {
        if (programUrl != null && isPublishable()) {
            String scheme = getScheme();
            if (scheme != null && (scheme.startsWith("http") || scheme.equals("https"))) {
                if (lastStatusChange == null || lastStatusChange.isBefore(instant().minus(age))) {
                    return HttpConnectionUtils.headRequest(programUrl, (response, exception) -> {
                        if (response != null) {
                            boolean changes = false;
                            if (statusCode == null || statusCode != response.statusCode()) {
                                setStatusCode(response.statusCode());
                                setLastStatusChange(instant());
                                changes = true;
                            }
                            if (response.statusCode() == 200) {
                                OptionalLong byteSize = response.headers().firstValueAsLong("Content-Length");
                                if (byteSize.isPresent() && !Objects.equals(byteSize.getAsLong(), getByteSize())) {
                                    this.setByteSize(byteSize.getAsLong());
                                    changes = true;
                                }
                            }
                            return changes;
                        } else {
                            return false;
                        }
                    });
                }
            }
        }
        return false;
    }


    public void setPlatform(@NonNull Platform platform) {
        this.platform = platform;

        if (this.mediaObject != null) {
            if (this.mediaObject.getLocations().contains(this)) {
                if (isPublishable(instant())) {
                    this.mediaObject.realizePrediction(this);
                }
            }
        }
    }


    public String getProgramUrl() {
        if (this.programUrl != null) {
            this.programUrl = this.programUrl.trim();
        }
        return programUrl;
    }

    public Location setProgramUrl(String url) {
        this.programUrl = url == null ? null : url.trim();
        return this;
    }

    public String getScheme() {
        if (programUrl != null) {
            try {
                URI asUri = URI.create(sanitizedProgramUrl(programUrl));
                return asUri.getScheme();
            } catch (IllegalArgumentException iae) {
                log.warn(iae.getMessage());
            }
        }
        return null;
    }

    public AVAttributes getAvAttributes() {
        tryToSetAvFileFormatBasedOnProgramUrl();
        return avAttributes;
    }

    public Location setAvAttributes(AVAttributes avAttributes) {
        if (avAttributes != null && avAttributes.getId() != null) {
            if (Objects.equals(this.avAttributes, avAttributes)) {
                log.debug("Nothing to do");
                return this;
            }
            log.info("Making copy of {}", avAttributes);
            avAttributes = new AVAttributes(avAttributes);
        }

        this.avAttributes = avAttributes;
        tryToSetAvFileFormatBasedOnProgramUrl();
        return this;
    }

    @Override
    public MediaObject getParent() {
        return mediaObject;
    }

    @Override
    public void setParent(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    @Override
    protected String getUrnPrefix() {
        return "urn:vpro:media:location:";
    }

    public Location setSubtitles(String subtitles) {
        this.subtitles = subtitles;
        return this;
    }


    public Location setOffset(Duration offset) {
        this.offset = offset;
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @NonNull
    @Override
    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@NonNull OwnerType owner) {
        this.owner = owner;
    }

    /**
     * Returns {@link #getAvAttributes()}<code>getBitRate</code> or <code>null</code> if no avattributes (yet) known.
     */
    @Nullable
    public Integer getBitrate() {
        if(avAttributes == null) {
            return null;
        }
        return avAttributes.getBitrate();
    }

    public Location setBitrate(@Nullable Integer bitrate) {
        if(avAttributes == null) {
            avAttributes = new AVAttributes();
        }
        avAttributes.setBitrate(bitrate);
        return this;
    }

    @Nullable
    public Long getByteSize() {
        if (avAttributes == null) {
            return null;
        }
        return avAttributes.getByteSize();
    }

    public Location setByteSize(Long byteSize) {
        if (avAttributes == null) {
            avAttributes = new AVAttributes();
        }
        avAttributes.setByteSize(byteSize);
        return this;
    }

    @Nullable
    public AVFileFormat getAvFileFormat() {
        if(avAttributes == null) {
            return null;
        }
        return avAttributes.getAvFileFormat();
    }

    public Location setAvFileFormat(AVFileFormat format) {
        if(avAttributes == null) {
            avAttributes = new AVAttributes();
        }
        avAttributes.setAvFileFormat(format);
        return this;
    }

    /**
     * @deprecated We filled to platform for every location, so this is now always true (though december 2023 these changes are not yet all published!)
     */
    @Deprecated
    public boolean hasPlatform() {
        return platform != null;
    }

    public boolean hasDrm() {
        return getProgramUrl() != null && getProgramUrl().startsWith("npo+drm");
    }
    public boolean onStreaming() {
        return getProgramUrl() != null && getProgramUrl().startsWith("npo");
    }

    @Nullable
    Prediction getPrediction(boolean create) {
        if (hasPlatform()) {
            if (mediaObject == null) {
                throw new IllegalStateException("Location does not have a parent mediaobject");
            }
            final Prediction existing = mediaObject.getPredictionWithoutFixing(platform);
            if (create) {
                final Prediction rec = mediaObject.findOrCreatePrediction(platform);
                if (existing == null) {
                    log.info("Implicitly created prediction record for {}", platform);
                    Embargos.copy(Embargos.of(publishStart, publishStop), rec);
                }
                return rec;
            } else {
                return existing;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the {@link Prediction} that is associated with the parent {@link MediaObject} for the same {@link #getPlatform()}.
     * If it is not available, it will be created.
     */
    Prediction getPrediction() {
        return getPrediction(true);
    }

    public boolean hasVideoSizing() {
        return avAttributes != null
            && avAttributes.getVideoAttributes() != null
            && avAttributes.getVideoAttributes().getHorizontalSize() != null
            && avAttributes.getVideoAttributes().getVerticalSize() != null;
    }

    public void setAuthorityUpdate(Boolean ceresUpdate) {
        this.authorityUpdate = ceresUpdate;
    }


    /**
     * For a Location it is true that it cannot have a wider embargo than its {@link #getPrediction() associated platform}
     * <p>
     * See also {@link #getOwnPublishStartInstant()} for the (settable) value that is not constraint by {@link #getPrediction()}
     */
    @Override
    public Instant getPublishStartInstant() {
        Instant own = getOwnPublishStartInstant();
        if(hasPlatform() && mediaObject != null) {
            try {
                Prediction record = getPrediction(false);
                if (record != null) {
                    Instant recordPublishStart = record.getPublishStartInstant();
                    if (recordPublishStart == null) {
                        return own;
                    }  else {
                        if (own == null || recordPublishStart.isAfter(own)) {
                            return recordPublishStart;
                        } else {
                            return own;
                        }
                    }
                }
            } catch (IllegalAuthorityRecord iea) {
                log.debug(iea.getMessage());
            }
        }

        return own;
    }

    /**
     * @since 7.10
     */
    public Instant getOwnPublishStartInstant() {
        return super.getPublishStartInstant();
    }

    @NonNull
    @Override
    public Location setPublishStartInstant(Instant publishStart) {
        if (! Objects.equals(this.publishStart, publishStart)) {

            super.setPublishStartInstant(publishStart);
            // Recalculate media permissions, when no media present, this is done by the add to collection
            if (mediaObject != null) {
                mediaObject.realizePrediction(this);
            }

            if (hasSystemAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }



    /**
     * The publishstop of a location is can be restricted by the {@link #getPrediction() associated platform}.
     * <p>
     * @see #getOwnPublishStopInstant() getOwnPublishableInstant  for the (settable) value that is not constrainted by {@link #getPrediction()}
     */
    @Override
    @Nullable
    public Instant getPublishStopInstant() {
        Instant own = getOwnPublishStopInstant();
        if(hasPlatform() && mediaObject != null) {
            try {
                Prediction record = getPrediction(false);
                if (record != null) {
                    Instant fromAuthorityRecord = record.getPublishStopInstant();
                    if (fromAuthorityRecord == null) {
                        return own;
                    } else {
                        if (own == null || fromAuthorityRecord.isBefore(own)) {
                            return fromAuthorityRecord;
                        } else {
                            return own;
                        }
                    }
                }
            } catch (IllegalAuthorityRecord iea) {
                log.debug(iea.getMessage());
            }
        }

        return own;
    }

    /**
     * @since 7.10
     */
    public Instant getOwnPublishStopInstant() {
        return super.getPublishStopInstant();
    }


    @NonNull
    @Override
    public Location setPublishStopInstant(Instant publishStop) {
        if (! Objects.equals(this.publishStop, publishStop)) {

            super.setPublishStopInstant(publishStop);
            if (mediaObject != null) {
                mediaObject.realizePrediction(this);
            }

            if (hasSystemAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }

    /**
     * 'Own' embargo wrapped in a {@link Range}.
     * <p>
     * Note that this ensures that stop >= start
     * @since 7.10
     * @see #asRange()
     * @see #getOwnEmbargo()
     */
    public Range<Instant> getOwnPublicationRange() {
        return Ranges.closedOpen(getOwnPublishStartInstant(), getOwnPublishStopInstant());
    }

    /**
     * @since 7.10
     * @see #getOwnPublicationRange()
     */
    public MutableEmbargo<WrappedEmbargo> getOwnEmbargo() {
        return Embargos.of(
            this::getOwnPublishStartInstant,
            this::setPublishStartInstant,
            this::getOwnPublishStopInstant,
            this::setPublishStopInstant
        );
    }

    public Authority getAuthority() {
        if (platform == null) {
            return Authority.USER;
        }
        return getPrediction().getAuthority();
    }

    /**
     * Locations are basically order on their programUrl
     */
    @Override
    public int compareTo(@NonNull Location that) {
        int result = 0;
        if (programUrl != null) {
            result = programUrl.compareTo(that.programUrl == null ? "" : that.programUrl);
        } else if (that.programUrl != null) {
            result = -1 * that.programUrl.compareTo("");
        }

        if (result != 0) {
            return result;
        }
        if (id != null && that.getId() != null) {
            return (int) (id - that.getId());
        }
        if (programUrl != null || that.programUrl != null) {
            return result;
        }
        return hashCode() - that.hashCode();
    }

    private void tryToSetAvFileFormatBasedOnProgramUrl() {
        if(avAttributes != null &&
            (avAttributes.getAvFileFormat() == null || avAttributes.getAvFileFormat().equals(AVFileFormat.UNKNOWN))) {
            avAttributes.setAvFileFormat(AVFileFormat.forProgramUrl(programUrl));
        }
    }

    private static AVAttributes getDefaultAVAttributes(AVAttributes avAttributes, String programUrl) {
        return requireNonNullElseGet(
            avAttributes,
            () -> new AVAttributes(AVFileFormat.forProgramUrl(programUrl))
        );
    }

    @Override
    protected void setWorkflow(Workflow workflow) {
        super.setWorkflow(workflow);
        if (Workflow.REVOKES.contains(workflow) && platform != null && this.mediaObject != null) {
            AuthorityLocations.updatePredictionStates(this.mediaObject, platform, instant());
        }
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (parent instanceof MediaObject mo) {
            this.mediaObject = mo;
            try {
                Prediction locationAuthorityRecord = getPrediction(false);
                if (locationAuthorityRecord != null) {
                    locationAuthorityRecord.setPublishStartInstant(publishStart);
                    locationAuthorityRecord.setPublishStopInstant(publishStop);
                }
            } catch (Throwable t) {
                log.error(t.getMessage());
            }
        }
    }

    @Override
    public @NonNull String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("format", avAttributes != null ? avAttributes.getAvFileFormat() : null)
            .append("programUrl", programUrl)
            .append("owner", owner);
        if (publishStart != null) {
            builder.append("start", publishStart);
        }
        if (publishStop != null) {
            builder.append("stop", publishStop);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) {
            return true;
        }

        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Location that = (Location)o;

        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        return compareTo(that) == 0;
    }



    @Override
    public int hashCode() {
        int result = (this.programUrl != null ? this.programUrl.hashCode() : 0);
        return result == 0 ? super.hashCode() : result;
    }

    /**
     * Returns true if the system has the authority about this record. So normally it can not be edited in POMS GUI.
     *
     */
    private boolean hasSystemAuthority() {
        if (mediaObject == null) {
            // unknown
            return false;
        }
        Prediction record = getPrediction(false);
        return record != null && record.getAuthority() == Authority.SYSTEM;
    }

    public final static Comparator<Location> PRESENTATION_ORDER = new PresentationComparator();

    public static class PresentationComparator implements Comparator<Location>, Serializable {
        @Serial
        private static final long serialVersionUID = 0L;

        @Override
        public int compare(Location loc1, Location loc2) {

            if(loc1.getAvAttributes() != null && loc2.getAvAttributes() != null) {

                if(!loc1.getAvAttributes().getAvFileFormat().equals(loc2.getAvAttributes().getAvFileFormat())) {
                    return loc1.getAvAttributes().getAvFileFormat().ordinal() - loc2.getAvAttributes().getAvFileFormat().ordinal();
                }

                if(loc1.getAvAttributes().getBitrate() == null || loc2.getAvAttributes().getBitrate() == null) {
                    if(!(loc1.getAvAttributes().getBitrate() == null && loc2.getAvAttributes().getBitrate() == null)) {
                        if(loc1.getAvAttributes().getBitrate() == null) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                } else {
                    if(!loc1.getAvAttributes().getBitrate().equals(loc2.getAvAttributes().getBitrate())) {
                        return loc1.getAvAttributes().getBitrate() - loc2.getAvAttributes().getBitrate();
                    }
                }

            } else if(loc1.getAvAttributes() == null && loc2.getAvAttributes() != null) {
                return -1;
            }
            if(loc1.getAvAttributes() != null && loc2.getAvAttributes() == null) {
                return 1;
            }

            if(loc1.getProgramUrl() == null || loc2.getProgramUrl() == null) {
                if(!(loc1.getProgramUrl() == null && loc2.getProgramUrl() == null)) {
                    if(loc1.getProgramUrl() == null) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return 0;
                }
            }

            if(loc1.getProgramUrl().equals(loc2.getProgramUrl())) {
                return loc1.getId() != null && loc2.getId() != null ? loc1.getId().compareTo(loc2.getId()) :
                    Objects.equals(loc1.getId(), loc2.getId()) ? 0 :
                        loc1.getId() == null ? -1 : 1;
            }

            int result = loc1.getProgramUrl().trim().compareTo(loc2.getProgramUrl().trim());
            if(result == 0) {
                return loc1.owner.ordinal() - loc2.owner.ordinal();
            }
            return result;
        }
    }

    @Getter
    public static class IllegalAuthorityRecord extends IllegalStateException {
        @Serial
        private static final long serialVersionUID = -162376436758135168L;

        private final String id;
        public IllegalAuthorityRecord(String id, String s) {
            super(s);
            this.id = id;
        }
    }

}
