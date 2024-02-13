package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "videoAttributesType", propOrder = {
    "color",
    "videoCoding",
    "aspectRatio"
})
public class VideoAttributes implements Serializable {
    @Serial
    private static final long serialVersionUID = 5314003558805319340L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;


    @XmlAttribute(name = "width")
    private Integer horizontalSize;

    @XmlAttribute(name = "heigth") // TODO: typo, should be height!!
    private Integer verticalSize;

    @XmlElement
    @Enumerated(EnumType.STRING)
    private ColorType color;

    @XmlElement
    private String videoCoding;

    @XmlElement
    @Enumerated(EnumType.STRING)
    private AspectRatio aspectRatio;

    /**
     * @since 5.9
     */
    @XmlTransient
    @Getter
    @Setter
    private Float fps;

    public VideoAttributes() {
    }

    public VideoAttributes(Integer horizontalSize, Integer verticalSize) {
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        aspectRatio = AspectRatio.fromDimension(horizontalSize, verticalSize);
    }

    public VideoAttributes(String videoCoding, Integer horizontalSize, Integer verticalSize) {
        this(videoCoding, horizontalSize, verticalSize, null, null);
    }

    @lombok.Builder
    private VideoAttributes(String videoCoding, Integer horizontalSize, Integer verticalSize, Float fps, ColorType color) {
        this(horizontalSize, verticalSize);
        this.videoCoding = videoCoding;
        this.fps = fps;
        this.color = color;
    }

    public VideoAttributes(VideoAttributes source) {
        this(source.videoCoding, source.horizontalSize, source.verticalSize);
        this.fps = source.fps;
        this.color = source.color;
        this.aspectRatio = source.aspectRatio;
    }

    public static VideoAttributes copy(VideoAttributes source) {
        if(source == null) {
            return null;
        }
        return new VideoAttributes(source);
    }

    public static VideoAttributes update(VideoAttributes from, VideoAttributes to) {
        return update(from, to, true);
    }

    public static VideoAttributes update(VideoAttributes from, VideoAttributes to, boolean overwrite) {
        if(from != null) {
            if(to == null) {
                to = new VideoAttributes();
            }
            if (overwrite || to.getColor() == null) {
                to.setColor(from.getColor());
            }
            if (overwrite || to.getVideoCoding() == null) {
                to.setVideoCoding(from.getVideoCoding());
            }
            if (overwrite || to.getAspectRatio() == null) {
                to.setAspectRatio(from.getAspectRatio());
            }
            if (overwrite || to.getHorizontalSize() == null) {
                to.setHorizontalSize(from.getHorizontalSize());
            }
            if (overwrite || to.getVerticalSize() == null) {
                to.setVerticalSize(from.getVerticalSize());
            }
        } else {
            to = null;
        }

        return to;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHorizontalSize() {
        return horizontalSize;
    }

    public void setHorizontalSize(Integer horizontalSize) {
        this.horizontalSize = horizontalSize;
    }

    public Integer getVerticalSize() {
        return verticalSize;
    }

    public void setVerticalSize(Integer verticalSize) {
        this.verticalSize = verticalSize;
    }

    public String getVideoCoding() {
        return videoCoding;
    }

    public void setVideoCoding(String coding) {
        this.videoCoding = coding;
    }

    public ColorType getColor() {
        return color;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    public AspectRatio getAspectRatio() {
        if (aspectRatio != null) {
            return aspectRatio;
        } else {
            if (horizontalSize != null && verticalSize != null) {
                return AspectRatio.fromDimension(verticalSize, horizontalSize);
            } else {
                return null;
            }
        }
    }

    public void setAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("horizontalSize", horizontalSize)
            .append("verticalSize", verticalSize)
            .append("color", color)
            .append("videoCoding", videoCoding)
            .append("aspectRatio", aspectRatio)
            .toString();
    }
}
