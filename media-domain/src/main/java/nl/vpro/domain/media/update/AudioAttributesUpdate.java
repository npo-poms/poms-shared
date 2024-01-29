/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;
import nl.vpro.domain.media.AudioAttributes;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "audioAttributesUpdateType", propOrder = {
        "channels",
        "coding"
        })
@Getter
@Setter
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



}
