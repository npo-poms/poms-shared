package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.EmbargoBuilder;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.jackson2.DurationToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.util.TimeUtils;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * A {@link MediaObject} can have more than one location which should differ in URL and
 * owner.
 * <p/>
 * The location owner describes an origin of the location. Several media suppliers provide
 * there own locations. To prevent conflicts while updating for incoming data, locations
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
    private static final long serialVersionUID = -140942203904508506L;

    private static final String BASE_URN = "urn:vpro:media:location:";

    public static String sanitizedProgramUrl(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.trim().split("/", 4);
        if (parts.length == 4) {
            try {
                parts[3] = URLEncoder.encode(parts[3], "UTF-8");
            } catch (UnsupportedEncodingException ignored) {

            }
        }
        return StringUtils.join(parts, "/");
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

    @XmlElement
    protected String subtitles;

    @Column(name = "`start_offset`")
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    protected Duration offset;

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

    @Column(length = 100, updatable = false, nullable = true)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    protected Platform platform;

    @XmlTransient
    private boolean authorityUpdate = false;

    public Location() {
    }

    public Location(OwnerType owner) {
        this.owner = owner;
    }

    public Location(String programUrl, OwnerType owner) {
        this.programUrl = programUrl == null ? null : programUrl.trim();
        this.owner = owner;
        setDefaultAVAttributes();
    }

    public Location(String programUrl, OwnerType owner, Platform platform) {
        this(programUrl, owner);
        this.platform = platform;
    }

    public Location(String programUrl, AVAttributes avAttributes) {
        this.programUrl = programUrl;
        this.avAttributes = avAttributes;
    }

    public Location(String programUrl, OwnerType owner, AVAttributes avAttributes) {
        this.programUrl = programUrl;
        this.owner = owner;
        this.avAttributes = avAttributes;
    }


    public Location(String programUrl, OwnerType owner, AVAttributes avAttributes, Duration duration) {
        this(programUrl, owner, avAttributes);
        this.duration = duration;
    }

    public static class Builder implements EmbargoBuilder<Builder> {

    }
    /**
     * Unset some default values, to ensure that roundtripping will result same object
     * @since 5.11
     */
    @JsonCreator
    static Location jsonCreator() {
        return builder().workflow(null).build();
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
        Workflow workflow
    ) {
        this.programUrl = programUrl;
        this.owner = owner == null ? OwnerType.BROADCASTER : owner;
        if (avAttributes == null) {
            avAttributes = new AVAttributes();
        }
        this.duration = duration;
        this.avAttributes = AVAttributes
            .builder()
            .bitrate(bitrate == null ? avAttributes.getBitrate() : bitrate)
            .avFileFormat(avFileFormat == null ? avAttributes.getAvFileFormat() : avFileFormat)
            .audioAttributes(audioAttributes == null ? avAttributes.getAudioAttributes() : audioAttributes)
            .videoAttributes(videoAttributes == null ? avAttributes.getVideoAttributes() : videoAttributes)
            .build();
        this.platform = platform;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        if (workflow != null) {
            this.workflow = workflow;
        }
    }

    @Deprecated
    public Location(String programUrl, OwnerType owner, AVAttributes avAttributes, Date duration) {
        this(programUrl, owner, avAttributes);
        setDuration(TimeUtils.durationOf(duration).orElse(null));
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

            to.setProgramUrl(from.getProgramUrl());
            to.setDuration(from.getDuration());
            to.setOffset(from.getOffset());
            to.setSubtitles(from.getSubtitles());
            to.setPublishStartInstant(from.getPublishStartInstant());
            to.setPublishStopInstant(from.getPublishStopInstant());

            to.setAvAttributes(AVAttributes.update(from.getAvAttributes(), to.getAvAttributes()));

        } else {
            to = null;
        }

        return to;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;

        if (platform != null && this.mediaObject != null) {
            Prediction record = getAuthorityRecord(false);
            if (record != null) {
                // in sync so we can query this class its fields on publishables
                if (record.getAuthority() == Authority.USER) {
                    Embargos.copyIfMoreRestricted(record, this);
                } else {
                    Embargos.copy(record, this);
                }
            }
            if (this.mediaObject.getLocations().contains(this)) {
                if (isPublishable()) {
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
        if (this.platform != null) {
            // triggers resetting of publishStop/publishStart
            this.setPlatform(this.platform);
        }
    }

    @Override
    protected String getUrnPrefix() {
        return "urn:vpro:media:location:";
    }

    public String getSubtitles() {
        return subtitles;
    }

    public Location setSubtitles(String subtitles) {
        this.subtitles = subtitles;
        return this;
    }
    public Duration getOffset() {
        return offset;
    }


    public Location setOffset(Duration offset) {
        this.offset = offset;
        return this;
    }


    public Duration getDuration() {
        return duration;
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

    public Integer getBitrate() {
        if(avAttributes == null) {
            return null;
        }
        return avAttributes.getBitrate();
    }

    public Location setBitrate(Integer bitrate) {
        if(avAttributes == null) {
            avAttributes = new AVAttributes();
        }
        avAttributes.setBitrate(bitrate);
        return this;
    }

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

    public boolean hasPlatform() {
        return platform != null;
    }

    public boolean hasDrm() {
        return getProgramUrl() != null && getProgramUrl().startsWith("npo+drm");
    }
    public boolean onStreaming() {
        return getProgramUrl() != null && getProgramUrl().startsWith("npo");
    }

    Prediction getAuthorityRecord(boolean create) {
        if (hasPlatform()) {
            if (mediaObject == null) {
                throw new IllegalStateException("Location does not have a parent mediaobject");
            }
            Prediction existing = mediaObject.getPrediction(platform);
            if (create) {
                Prediction rec = mediaObject.findOrCreatePrediction(platform);
                if (existing == null) {
                    log.info("Implicitely created prediction record for {}", platform);
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

    Prediction getAuthorityRecord() {
        return getAuthorityRecord(true);
    }

    public boolean hasVideoSizing() {
        return avAttributes != null
            && avAttributes.getVideoAttributes() != null
            && avAttributes.getVideoAttributes().getHorizontalSize() != null
            && avAttributes.getVideoAttributes().getVerticalSize() != null;
    }

    /**
     * See {@link MediaObject#isLocationAuthorityUpdate}
     */
    public boolean isAuthorityUpdate() {
        return authorityUpdate;
    }

    public void setAuthorityUpdate(Boolean ceresUpdate) {
        this.authorityUpdate = ceresUpdate;
    }

    @Override
    public Instant getPublishStartInstant() {
        if(hasPlatform() && mediaObject != null) {
            try {
                Prediction record = getAuthorityRecord(false);
                if (record != null) {
                    return record.getPublishStartInstant();
                }
            } catch (IllegalAuthorityRecord iea) {
                log.debug(iea.getMessage());
            }
        }

        return super.getPublishStartInstant();
    }

    @NonNull
    @Override
    public Location setPublishStartInstant(Instant publishStart) {
        if (! Objects.equals(this.publishStart, publishStart)) {

            super.setPublishStartInstant(publishStart);

            // Recalculate media permissions, when no media present, this is done by the add to collection
            if (mediaObject != null) {
                if (hasPlatform()) {
                    getAuthorityRecord().setPublishStartInstant(publishStart);
                }
                mediaObject.realizePrediction(this);
            }

            if (hasSystemAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }


    /**
     * The publish stop of a location is rather complicated:
     * 1. It is the offline date of the corresponding streaming platform status if that is available.
     * 2. It not, then it is the offline date of the corresponding authority record.
     * 3. It that too is not available then it will fall back to it's own field {@link PublishableObject#getPublishStopInstant()}
     */
    @Override
    public Instant getPublishStopInstant() {
        if(hasPlatform() && mediaObject != null) {
            Instant streamingOffline = onStreaming() && mediaObject.getStreamingPlatformStatus() != null ? mediaObject.getStreamingPlatformStatus().getOffline(hasDrm()) : null;
            try {
                Prediction record = getAuthorityRecord(false);
                if (record != null) {
                    Instant fromAuthorityRecord = record.getPublishStopInstant();
                    if (fromAuthorityRecord == null || (streamingOffline != null && fromAuthorityRecord.isAfter(streamingOffline))) {
                        return streamingOffline;
                    }
                    return fromAuthorityRecord;
                }
            } catch (IllegalAuthorityRecord iea) {
                log.debug(iea.getMessage());
            }
        }

        return super.getPublishStopInstant();
    }

    @NonNull
    @Override
    public Location setPublishStopInstant(Instant publishStop) {
        if (! Objects.equals(this.publishStop, publishStop)) {

            super.setPublishStopInstant(publishStop);
            if (mediaObject != null) {
                if (hasPlatform()) {
                    getAuthorityRecord().setPublishStopInstant(publishStop);
                }
                mediaObject.realizePrediction(this);
            }

            if (hasSystemAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }

    public Authority getAuthority() {
        if (platform == null) {
            return Authority.USER;
        }
        return getAuthorityRecord().getAuthority();
    }


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
        if(avAttributes != null && (avAttributes.getAvFileFormat() == null || avAttributes.getAvFileFormat().equals(AVFileFormat.UNKNOWN))) {
            avAttributes.setAvFileFormat(AVFileFormat.forProgramUrl(programUrl));
        }
    }

    private void setDefaultAVAttributes() {
        if(avAttributes == null) {
            avAttributes = new AVAttributes(AVFileFormat.forProgramUrl(programUrl));
        }
    }

    @Override
    public void setWorkflow(Workflow workflow) {
        super.setWorkflow(workflow);
        if (Workflow.REVOKES.contains(workflow) && platform != null && this.mediaObject != null) {
            Locations.updatePredictionStates(this.mediaObject, platform);
        }
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
            try {
                Prediction locationAuthorityRecord = getAuthorityRecord(false);
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
    public String toString() {
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
        int result = super.hashCode();
        result = 31 * result + (this.programUrl != null ? this.programUrl.hashCode() : 0);
        return result;
    }

    public Long getNeboId() {
        return neboId;
    }

    public void setNeboId(Long neboId) {
        this.neboId = neboId;
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
        Prediction record = getAuthorityRecord(false);
        return record != null && record.getAuthority() == Authority.SYSTEM;
    }

    public final static Comparator<Location> PRESENTATION_ORDER = new PresentationComparator();

    public static class PresentationComparator implements Comparator<Location>, Serializable {
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

    public static class IllegalAuthorityRecord extends IllegalStateException {
        private final String id;
        public IllegalAuthorityRecord(String id, String s) {
            super(s);
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

}
