package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "videoAttributesType", propOrder = {"color", "videoCoding", "aspectRatio"})
public class VideoAttributes implements Serializable {
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

    public VideoAttributes() {
    }

    public VideoAttributes(Integer horizontalSize, Integer verticalSize) {
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        aspectRatio = AspectRatio.fromDimension(horizontalSize, verticalSize);
    }


    @lombok.Builder
    public VideoAttributes(String videoCoding, Integer horizontalSize, Integer verticalSize) {
        this(horizontalSize, verticalSize);
        this.videoCoding = videoCoding;
    }

    public VideoAttributes(VideoAttributes source) {
        this(source.videoCoding, source.horizontalSize, source.verticalSize);
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
        if(from != null) {
            if(to == null) {
                to = new VideoAttributes();
            }

            to.setAspectRatio(from.getAspectRatio());
            to.setHorizontalSize(from.getHorizontalSize());
            to.setVerticalSize(from.getVerticalSize());
            to.setVideoCoding(from.getVideoCoding());

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
