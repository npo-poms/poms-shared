/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 * Creation date 3 nov 2008.
 */
package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author roekoe
 *
 */
@XmlEnum
@XmlType(name = "groupTypeEnum")
public enum GroupType implements SubMediaType {

    SERIES(MediaType.SERIES, true, true /* seems doubtfull */),
    SEASON(MediaType.SEASON, true, true /* seems questionable */),
    UMBRELLA(MediaType.UMBRELLA, false, true),
    @Deprecated   // MSE-1453
    ARCHIVE(MediaType.ARCHIVE, false, false /* since it is deprecated */),
    COLLECTION(MediaType.COLLECTION, false, true),
    PLAYLIST(MediaType.PLAYLIST, false, true),
    ALBUM(MediaType.ALBUM, false, true),
    STRAND(MediaType.STRAND, false, true);

    public static final String URN_PREFIX = "urn:vpro:media:group:";

    private final MediaType mediaType;

    private final boolean canContainEpisodes;

    private final boolean canBeCreatedByNormalUsers;



    GroupType(MediaType mediaType, boolean canContainEpisodes, boolean canBeCreatedByNormalUsers) {
        this.mediaType = mediaType;
        this.canContainEpisodes = canContainEpisodes;
        this.canBeCreatedByNormalUsers = canBeCreatedByNormalUsers;
        if (mediaType == null) {
            throw new IllegalStateException();
        }
    }

    public String value() {
        return name();
    }
    @Override
    public String getUrnPrefix() {
        return URN_PREFIX;
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public boolean canContainEpisodes() {
        return canContainEpisodes;
    }

    @Override
    public boolean canBeCreatedByNormalUsers() {
        return canBeCreatedByNormalUsers;
    }

    public static final Set<GroupType> EPISODE_CONTAINERS;
    static {
        EPISODE_CONTAINERS = Arrays.stream(GroupType.values())
            .filter(GroupType::canContainEpisodes)
            .collect(Collectors.toUnmodifiableSet());
    }

}
