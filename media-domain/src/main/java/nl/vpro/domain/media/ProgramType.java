/**
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "programTypeEnum")
public enum ProgramType implements SubMediaType {


    BROADCAST(MediaType.BROADCAST),

    MOVIE(MediaType.MOVIE),

    TRAILER(MediaType.TRAILER),

    CLIP(MediaType.CLIP),

    STRAND(MediaType.STRAND),

    TRACK(MediaType.TRACK),

    VISUALRADIO(MediaType.VISUALRADIO),

    /**
     * @since 2.1
     */
    RECORDING(MediaType.RECORDING),

    PROMO(MediaType.PROMO);

    public static final String URN_PREFIX = "urn:vpro:media:program:";

    private MediaType mediaType;

    ProgramType(MediaType mediaType) {
        this.mediaType = mediaType;
    }


    public String value() {
        return name();
    }

    public ProgramType fromValue(String v) {
        return valueOf(v);
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public String getUrnPrefix() {
        return URN_PREFIX;
    }


    public static Set<ProgramType> EPISODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ProgramType.BROADCAST, ProgramType.STRAND)));
}
