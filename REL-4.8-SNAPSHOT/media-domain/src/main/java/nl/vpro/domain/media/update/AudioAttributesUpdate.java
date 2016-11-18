/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.AudioAttributes;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "audioAttributesUpdateType", propOrder = {
        "channels",
        "coding"
        })
public class AudioAttributesUpdate {

    @XmlElement
    protected Integer channels;

    @XmlElement
    protected String coding;

    public static AudioAttributesUpdate copy(AudioAttributesUpdate copy) {
        if (copy == null) {
            return null;
        }
        AudioAttributesUpdate result = new AudioAttributesUpdate();
        result.channels = copy.channels;
        result.coding = copy.coding;
        return result;
    }

    public AudioAttributesUpdate() {
    }

    public AudioAttributesUpdate(Integer channels, String coding) {
        this.channels = channels;
        this.coding = coding;
    }

    public AudioAttributesUpdate(AudioAttributes attributes) {
        channels = attributes.getNumberOfChannels();
        coding = attributes.getAudioCoding();
    }

    public AudioAttributes toAudioAttributes() {
        return new AudioAttributes(coding, channels);
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public String getCoding() {
        return coding;
    }

    public void setCoding(String coding) {
        this.coding = coding;
    }
}
