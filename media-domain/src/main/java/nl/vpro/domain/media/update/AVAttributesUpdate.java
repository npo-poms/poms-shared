/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.AVAttributes;
import nl.vpro.domain.media.AVFileFormat;

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
    private Integer bitrate;

    @XmlElement
    @Size(min = 0)
    private Long byteSize;

    @XmlElement
    private AVFileFormat avFileFormat;

    @XmlElement
    private AudioAttributesUpdate audioAttributes;

    @XmlElement
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
        AVAttributes result = new AVAttributes(bitrate, avFileFormat);
        result.setByteSize(byteSize);
        if(audioAttributes != null) {
            result.setAudioAttributes(audioAttributes.toAudioAttributes());
        }
        if(videoAttributes != null) {
            result.setVideoAttributes(videoAttributes.toVideoAttributes());
        }

        return result;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public AVFileFormat getAvFileFormat() {
        return avFileFormat;
    }

    public void setAvFileFormat(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
    }

    public AudioAttributesUpdate getAudioAttributes() {
        return audioAttributes;
    }

    public void setAudioAttributes(AudioAttributesUpdate audioAttributes) {
        this.audioAttributes = audioAttributes;
    }

    public VideoAttributesUpdate getVideoAttributes() {
        return videoAttributes;
    }

    public void setVideoAttributes(VideoAttributesUpdate videoAttributes) {
        this.videoAttributes = videoAttributes;
    }
}
