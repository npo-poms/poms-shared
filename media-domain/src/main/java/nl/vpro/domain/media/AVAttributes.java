package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "avattributes")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "avAttributesType", propOrder = {
    "bitrate",
    "byteSize",
    "avFileFormat",
    "videoAttributes",
    "audioAttributes"
})
public class AVAttributes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1651506882422062995L;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @XmlElement
    @Min(0L)
    @Nullable
    private Integer bitrate;

    @XmlElement
    @Min(0L)
    @Getter@Setter
    private Long byteSize;

    @XmlElement
    @Enumerated(EnumType.STRING)
    private AVFileFormat avFileFormat;

    @Getter
    @XmlElement
    @OneToOne(optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    private AudioAttributes audioAttributes;

    @Getter
    @XmlElement
    @OneToOne(optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    private VideoAttributes videoAttributes;

    public AVAttributes() {
    }

    public AVAttributes(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
    }

    public AVAttributes(@Nullable Integer bitrate, AVFileFormat avFileFormat) {
        this.bitrate = bitrate;
        this.avFileFormat = avFileFormat;
    }

    @lombok.Builder
    private AVAttributes(
        Integer bitrate,
        Long byteSize,
        AVFileFormat avFileFormat,
        AudioAttributes audioAttributes,
        VideoAttributes videoAttributes) {
        this.bitrate = bitrate;
        this.avFileFormat = avFileFormat;
        this.audioAttributes = audioAttributes;
        this.videoAttributes = videoAttributes;
        this.byteSize = byteSize;
    }

    /**
     * Copy constructor
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public AVAttributes(AVAttributes source) {
        this(
            source.bitrate,
            source.byteSize,
            source.avFileFormat,
            AudioAttributes.copy(source.audioAttributes),
            VideoAttributes.copy(source.videoAttributes));
    }

    public static AVAttributes copy(AVAttributes source){
        if(source == null) {
            return null;
        }
        return new AVAttributes(source);
    }

    public static AVAttributes update(AVAttributes from, AVAttributes to) {

        return update(from, to, true);
    }

    public static AVAttributes update(AVAttributes from, AVAttributes to, boolean overwrite) {
        if(from != null) {
            if(to == null) {
                to = new AVAttributes();
            }
            if (overwrite || to.getByteSize() == null) {
                to.setByteSize(from.getByteSize());
            }
            if (overwrite || to.getAvFileFormat() == null) {
                to.setAvFileFormat(from.getAvFileFormat());
            }
            if (overwrite || to.getBitrate() == null) {
                to.setBitrate(from.getBitrate());
            }

            to.setAudioAttributes(
                AudioAttributes.update(
                    from.getAudioAttributes(), to.getAudioAttributes(), overwrite
                )
            );
            to.setVideoAttributes(
                VideoAttributes.update(
                    from.getVideoAttributes(), to.getVideoAttributes(), overwrite)
            );

        } else {
            to = null;
        }

        return to;
    }

    /**
     * Returns the associated bitrate (or <code>null</code> if unknown)
     */
    @Nullable
    public Integer getBitrate() {
        return bitrate;
    }

    public AVAttributes setBitrate(@Nullable Integer bitrate) {
        this.bitrate = bitrate;
        return this;
    }

    public AVFileFormat getAvFileFormat() {
        return (avFileFormat != null) ? avFileFormat : AVFileFormat.UNKNOWN;
    }

    public AVAttributes setAvFileFormat(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
        return this;
    }

    public AVAttributes setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
        return this;
    }

    public AVAttributes setVideoAttributes(VideoAttributes videoAttributes) {
        this.videoAttributes = videoAttributes;
        return this;
    }

    /**
     * checks if it is video or audio.
     *
     * @return true if video
     */
    public boolean hasVideo() {
        return videoAttributes != null;

    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("bitrate", bitrate)
            .append("avFileFormat", avFileFormat)
            .append("audioAttributes", audioAttributes)
            .append("videoAttributes", videoAttributes)
            .toString();
    }
}
