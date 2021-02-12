/*
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


    BROADCAST(MediaType.BROADCAST, true),

    MOVIE(MediaType.MOVIE, true),

    TRAILER(MediaType.TRAILER),

    CLIP(MediaType.CLIP),

    STRAND(MediaType.STRAND, true),

    TRACK(MediaType.TRACK),

    VISUALRADIO(MediaType.VISUALRADIO),

    /**
     * @since 2.1
     */
    RECORDING(MediaType.RECORDING),

    PROMO(MediaType.PROMO);

    public static final String URN_PREFIX = "urn:vpro:media:program:";

    private final MediaType mediaType;
    private final boolean hasEpisodeOf;

    ProgramType(MediaType mediaType, boolean hasEpisodeOf) {
        this.mediaType = mediaType;
        this.hasEpisodeOf = hasEpisodeOf;
        if (mediaType == null){
            throw new IllegalStateException();
        }
    }

    ProgramType(MediaType mediaType) {
        this(mediaType, false);
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

    @Override
    public boolean hasEpisodeOf() {
        return hasEpisodeOf;
    }

    @Override
    public boolean canHaveScheduleEvents() {
        return EPISODES.contains(this);
    }

    public static final Set<ProgramType> EPISODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        ProgramType.BROADCAST,
        ProgramType.STRAND,
        ProgramType.MOVIE
    )));
}
