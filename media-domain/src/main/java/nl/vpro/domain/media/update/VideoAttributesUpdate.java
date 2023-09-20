/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.*;

/**
 * @see nl.vpro.domain.media.update
 * @see VideoAttributes
 */
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
