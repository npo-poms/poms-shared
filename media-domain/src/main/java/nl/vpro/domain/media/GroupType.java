/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 3 nov 2008.
 */
package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author roekoe
 *
 */
@XmlEnum
@XmlType(name = "groupTypeEnum")
public enum GroupType implements SubMediaType {

    SERIES(MediaType.SERIES, true),
    SEASON(MediaType.SEASON, true),
    UMBRELLA(MediaType.UMBRELLA, false),
    @Deprecated   // MSE-1453
    ARCHIVE(MediaType.ARCHIVE),
    COLLECTION(MediaType.COLLECTION),
    PLAYLIST(MediaType.PLAYLIST),
    ALBUM(MediaType.ALBUM),
    STRAND(MediaType.STRAND);

    public static final String URN_PREFIX = "urn:vpro:media:group:";

    private final MediaType mediaType;

    private final boolean canContainEpisodes;

    GroupType(MediaType mediaType) {
        this(mediaType, false);
    }

    GroupType(MediaType mediaType, boolean canContainEpisodes) {
        this.mediaType = mediaType;
        this.canContainEpisodes = canContainEpisodes;
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

    public static final Set<GroupType> EPISODE_CONTAINERS;
    static {
        EPISODE_CONTAINERS = Collections.unmodifiableSet(
            Arrays.stream(GroupType.values())
                .filter(GroupType::canContainEpisodes)
                .collect(Collectors.toSet())
        );
    }

}
