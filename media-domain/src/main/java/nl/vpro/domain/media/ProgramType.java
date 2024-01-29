/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "programTypeEnum")
public enum ProgramType implements SubMediaType {


    BROADCAST(MediaType.BROADCAST, true, false),

    MOVIE(MediaType.MOVIE, true, false),

    TRAILER(MediaType.TRAILER, false, true),

    CLIP(MediaType.CLIP, false, true),

    STRAND(MediaType.STRAND, true, false),

    TRACK(MediaType.TRACK, false, true),

    VISUALRADIO(MediaType.VISUALRADIO, true, true),

    /**
     * @since 2.1
     */
    RECORDING(MediaType.RECORDING, false, true),

    PROMO(MediaType.PROMO, false, false);

    public static final String URN_PREFIX = "urn:vpro:media:program:";

    private final MediaType mediaType;
    private final boolean hasEpisodeOf;
    private final boolean canBeCreatedByNormalUsers;

    ProgramType(MediaType mediaType, boolean hasEpisodeOf, boolean canBeCreatedByNormalUsers) {
        this.mediaType = mediaType;
        this.hasEpisodeOf = hasEpisodeOf;
        this.canBeCreatedByNormalUsers = canBeCreatedByNormalUsers;
        if (mediaType == null){
            throw new IllegalStateException();
        }
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

    @Override
    public boolean canBeCreatedByNormalUsers() {
        return canBeCreatedByNormalUsers;
    }

    public static final Set<ProgramType> EPISODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        ProgramType.BROADCAST,
        ProgramType.STRAND,
        ProgramType.MOVIE
    )));
}
