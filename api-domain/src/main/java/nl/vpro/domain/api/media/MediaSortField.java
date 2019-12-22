/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "mediaSortTypeEnum")
public enum MediaSortField {
    title,
    sortDate,
    publishDate,
    episode,
    episodeAdded,
    memberAdded,
    member,
    creationDate,
    lastModified
}
