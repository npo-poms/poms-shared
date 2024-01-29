/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.AVAttributes;
import nl.vpro.domain.media.AVFileFormat;

@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "avAtributeUpdateType", propOrder = {
    "bitrate",
    "byteSize",
    "avFileFormat",
    "videoAttributes",
    "audioAttributes"
})
public class AVAttributesUpdate {

    @XmlElement
    @Min(0L)
    @Getter
    private Integer bitrate;

    @XmlElement
    @Min(0L)
    @Getter
    private Long byteSize;

    @XmlElement
    @Getter
    private AVFileFormat avFileFormat;

    @XmlElement
    @Getter
    @Valid
    private AudioAttributesUpdate audioAttributes;

    @XmlElement
    @Getter
    @Valid
    private VideoAttributesUpdate videoAttributes;

    public static AVAttributesUpdate copy(AVAttributesUpdate from) {
        if (from == null) {
            return null;
        }
        AVAttributesUpdate result = new AVAttributesUpdate();
        result.bitrate = from.bitrate;
        result.byteSize = from.byteSize;
        result.avFileFormat = from.avFileFormat;
        result.audioAttributes = AudioAttributesUpdate.copy(from.audioAttributes);
        result.videoAttributes = VideoAttributesUpdate.copy(from.videoAttributes);
        return result;
    }

    public AVAttributesUpdate() {
    }

    public AVAttributesUpdate(AVFileFormat avFileFormat, Integer bitrate) {
        this.avFileFormat = avFileFormat;
        this.bitrate = bitrate;
    }

    public AVAttributesUpdate(AVFileFormat avFileFormat, Integer bitrate, AudioAttributesUpdate audioAttributes, VideoAttributesUpdate videoAttributes) {
        this(avFileFormat, bitrate);
        this.audioAttributes = audioAttributes;
        this.videoAttributes = videoAttributes;
    }

    public static AVAttributesUpdate of(AVAttributes attributes) {
        if (attributes == null) {
            return null;
        }
        return new AVAttributesUpdate(attributes);
    }

    public AVAttributesUpdate(AVAttributes attributes) {
        bitrate = attributes.getBitrate();
        avFileFormat = attributes.getAvFileFormat();

        if(attributes.getAudioAttributes() != null) {
            audioAttributes = new AudioAttributesUpdate(attributes.getAudioAttributes());
        }

        if(attributes.getVideoAttributes() != null) {
            videoAttributes = new VideoAttributesUpdate(attributes.getVideoAttributes());
        }
    }

    AVAttributes toAvAttributes() {
        final AVAttributes result = new AVAttributes(bitrate, avFileFormat);
        result.setByteSize(byteSize);
        if(audioAttributes != null) {
            result.setAudioAttributes(audioAttributes.toAudioAttributes());
        }
        if(videoAttributes != null) {
            result.setVideoAttributes(videoAttributes.toVideoAttributes());
        }

        return result;
    }
    public static AVAttributes toAvAttributes(AVAttributesUpdate avAttributesUpdate) {
        return avAttributesUpdate == null ? null : avAttributesUpdate.toAvAttributes();
    }

    }
