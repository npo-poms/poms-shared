/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.validation.constraints.Min;
import javax.xml.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;
import nl.vpro.domain.media.AspectRatio;
import nl.vpro.domain.media.ColorType;
import nl.vpro.domain.media.VideoAttributes;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "videoAttributesUpdateType", propOrder = {
        "aspectRatio",
        "color",
        "coding"
        })
@Getter
@Setter
public class VideoAttributesUpdate {

    @XmlAttribute(name = "width")
    @Min(0)
    private Integer width;

    @XmlAttribute(name = "height")
    @Min(0)
    private Integer height;

    @XmlElement
    private AspectRatio aspectRatio;

    @XmlElement
    private ColorType color;

    @XmlElement
    private String coding;

    public static VideoAttributesUpdate copy(VideoAttributesUpdate copy) {
        if (copy == null) {
            return null;
        }
        VideoAttributesUpdate result = new VideoAttributesUpdate();
        result.width = copy.width;
        result.height = copy.height;
        result.aspectRatio = copy.aspectRatio;
        result.color = copy.color;
        result.coding = copy.coding;
        return result;
    }

    private VideoAttributesUpdate() {
    }

    public VideoAttributesUpdate(Integer width, Integer height) {
        this(width, height, null, null);
    }

    public VideoAttributesUpdate(Integer width, Integer height, String coding, ColorType color) {
        this.coding = coding;
        this.color = color;
        this.height = height;
        this.width = width;
        this.aspectRatio = AspectRatio.fromDimension(width, height);
    }

    public VideoAttributesUpdate(VideoAttributes videoAttributes) {
        width = videoAttributes.getHorizontalSize();
        height = videoAttributes.getVerticalSize();
        aspectRatio = videoAttributes.getAspectRatio();
        color = videoAttributes.getColor();
        coding = videoAttributes.getVideoCoding();
    }

    VideoAttributes toVideoAttributes() {
        VideoAttributes attributes = new VideoAttributes();
        attributes.setVerticalSize(height);
        attributes.setHorizontalSize(width);
        attributes.setAspectRatio(aspectRatio);
        attributes.setColor(color);
        attributes.setVideoCoding(coding);
        return attributes;
    }
}
