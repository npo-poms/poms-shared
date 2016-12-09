package nl.vpro.domain.media;

import lombok.Builder;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.jackson2.DurationToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.persistence.DurationToTimeConverter;
import nl.vpro.util.DateUtils;
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
 * @see nl.vpro.domain.media.support.OwnerType
 * @since 0.4
 */
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationType",
    propOrder = {"programUrl",
        "avAttributes",
        "subtitles",
        "offset",
        "duration"})
public class Location extends PublishableObject implements Ownable, Comparable<Location> {
    //TODO Validate URL, TYPE and Owner AVTYPE

    private static final Logger LOG = LoggerFactory.getLogger(Location.class);

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
            } catch (UnsupportedEncodingException e) {

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

    @Column(name = "start_offset")
    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @Convert(converter = DurationToTimeConverter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    protected Duration offset;

    @XmlElement
    @Convert(converter = DurationToTimeConverter.class)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = XMLDurationToJsonTimestamp .Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    protected Duration duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute(required = true)
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

    @Builder
    public Location(
        String programUrl,
        OwnerType owner,
        AVAttributes avAttributes,
        Duration duration,
        Integer bitrate,
        AVFileFormat avFileFormat,
        AudioAttributes audioAttributes,
        VideoAttributes videoAttributes
    ) {
        this.programUrl = programUrl;
        this.owner = owner;
        if (avAttributes == null) {
            avAttributes = new AVAttributes();
        };
        this.duration = duration;
        this.avAttributes = AVAttributes
            .builder()
            .bitrate(bitrate == null ? avAttributes.getBitrate() : bitrate)
            .avFileFormat(avFileFormat == null ? avAttributes.getAvFileFormat() : avFileFormat)
            .audioAttributes(audioAttributes == null ? avAttributes.getAudioAttributes() : audioAttributes)
            .videoAttributes(videoAttributes == null ? avAttributes.getVideoAttributes() : videoAttributes)
            .build();

    }

    @Deprecated
    public Location(String programUrl, OwnerType owner, AVAttributes avAttributes, Date duration) {
        this(programUrl, owner, avAttributes);
        setDuration(duration);
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
                LOG.info("Updating owner of {} {} -> {}", to, to.getOwner(), owner);
            }

            to.setProgramUrl(from.getProgramUrl());
            to.setDuration(from.getDuration());
            to.setOffset(from.getOffset());
            to.setSubtitles(from.getSubtitles());
            to.setPublishStart(from.getPublishStart());
            to.setPublishStop(from.getPublishStop());

            to.setAvAttributes(AVAttributes.update(from.getAvAttributes(), to.getAvAttributes()));

        } else if(from == null) {
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
            LocationAuthorityRecord record = getAuthorityRecord();
            // in sync so we can query this class its fields on publishables
            this.publishStart = record.getRestrictionStart();
            this.publishStop = record.getRestrictionStop();
            if (this.mediaObject.getLocations().contains(this)) {
                this.mediaObject.realizePrediction(this);
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
        tryToSetAvFileFormatBasedOnProgramUrl(avAttributes);
        return avAttributes;
    }

    public Location setAvAttributes(AVAttributes avAttributes) {
        tryToSetAvFileFormatBasedOnProgramUrl(avAttributes);
        this.avAttributes = avAttributes;
        return this;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    Location setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
        if (this.platform != null) {
            // triggers resetting of publishStop/publishStart
            this.setPlatform(this.platform);
        }
        return this;
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

    @JsonIgnore
    public Location setOffset(Date offset) {
        this.offset = offset == null ? null : Duration.ofMillis(offset.getTime());
        return this;
    }


    public Duration getDuration() {
        return duration;
    }

    public Location setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    @Deprecated
    @JsonIgnore
    public Location setDuration(Date duration) {
        this.duration = duration == null ? null : Duration.ofMillis(duration.getTime());
        return this;
    }

    @Override
    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public void setOwner(OwnerType owner) {
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

    public LocationAuthorityRecord getAuthorityRecord() {
        if (hasPlatform()) {
            if (mediaObject == null) {
                throw new IllegalStateException("Location does not have a parent mediaobject");
            }

            LocationAuthorityRecord rec = mediaObject.getLocationAuthorityRecord(platform);
            if (rec == null) {
                throw new IllegalAuthorativeRecord(mediaObject.getMid(), String.format("MediaObject %s of %s has no authorative record %s", mediaObject, this, platform));
            }
            return rec;
        } else {
            return null;
        }
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
    public Date getPublishStart() {
        if(isCeresLocation() && mediaObject != null) {
            try {
                LocationAuthorityRecord record = getAuthorityRecord();
                return DateUtils.toDate(record.getRestrictionStart());
            } catch (IllegalAuthorativeRecord iea) {
                LOG.debug(iea.getMessage());
            }
        }

        return super.getPublishStart();
    }

    @Override
    public PublishableObject setPublishStart(Date publishStart) {
        if (! Objects.equals(this.publishStart, DateUtils.toInstant(publishStart))) {

            super.setPublishStart(publishStart);

            // Recalculate media permissions, when no media present, this is done by the add to collection
            if (mediaObject != null) {
                if (isCeresLocation()) {
                    getAuthorityRecord().setRestrictionStart(DateUtils.toInstant(publishStart));
                }
                mediaObject.realizePrediction(this);
            }

            if (hasCeresAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }

    @Override
    public Date getPublishStop() {
        if(isCeresLocation() && mediaObject != null) {
            try {
                return DateUtils.toDate(getAuthorityRecord().getRestrictionStop());
            } catch (IllegalAuthorativeRecord iea) {
                LOG.debug(iea.getMessage());
            }
        }

        return super.getPublishStop();
    }

    @Override
    public PublishableObject setPublishStop(Date publishStop) {
        if (! Objects.equals(this.publishStop, DateUtils.toInstant(publishStop))) {

            super.setPublishStop(publishStop);
            if (mediaObject != null) {
                if (isCeresLocation()) {
                    getAuthorityRecord().setRestrictionStop(DateUtils.toInstant(publishStop));
                }
                mediaObject.realizePrediction(this);
            }

            if (hasCeresAuthority()) {
                authorityUpdate = true;
            }
        }

        return this;
    }

    @Override
    public int compareTo(Location that) {
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

    private AVAttributes tryToSetAvFileFormatBasedOnProgramUrl(AVAttributes avAttributes) {
        if(avAttributes != null && (avAttributes.getAvFileFormat() == null || avAttributes.getAvFileFormat().equals(AVFileFormat.UNKNOWN))) {
            avAttributes.setAvFileFormat(AVFileFormat.forProgramUrl(programUrl));
        }
        return avAttributes;
    }

    private void setDefaultAVAttributes() {
        if(avAttributes == null) {
            avAttributes = new AVAttributes(AVFileFormat.forProgramUrl(programUrl));
        }
    }



    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
            if (platform != null) {
                LocationAuthorityRecord.unknownAuthority(this.mediaObject, platform);
            }
        }
        try {
            LocationAuthorityRecord locationAuthorityRecord = getAuthorityRecord();

            if (locationAuthorityRecord != null) {
                locationAuthorityRecord.setRestrictionStart(publishStart);
                locationAuthorityRecord.setRestrictionStop(publishStop);
            }
        } catch (Throwable t) {
            LOG.error(t.getMessage());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("format", avAttributes != null ? avAttributes.getAvFileFormat() : null)
            .append("programUrl", programUrl)
            .append("owner", owner)
            .toString();
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


    public boolean isCeresLocation() {
        return hasPlatform();
    }
    /**
     * Returns true if ceres has the authority about this record. So normally it can not be edited in POMS.
     *
     */
    public boolean hasCeresAuthority() {
        if (mediaObject == null) {
            // unknown
            return false;
        }
        LocationAuthorityRecord record = getAuthorityRecord();
        return record != null && record.hasAuthority();
    }

    public final static Comparator<Location> PRESENTATION_ORDER = new PresentationComparator();

    public static class PresentationComparator implements Comparator<Location>, Serializable {
        private static final long serialVersionUID = 0l;

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
                    loc1.getId() == loc2.getId() ? 0 :
                        loc1.getId() == null ? -1 : 1;
            }

            int result = loc1.getProgramUrl().trim().compareTo(loc2.getProgramUrl().trim());
            if(result == 0) {
                return loc1.owner.ordinal() - loc2.owner.ordinal();
            }
            return result;
        }
    }

    public static class IllegalAuthorativeRecord extends IllegalStateException {
        private final String id;
        public IllegalAuthorativeRecord(String id, String s) {
            super(s);
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

}
