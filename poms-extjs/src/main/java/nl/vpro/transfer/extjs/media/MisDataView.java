/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "mainTitle",
        "episodeTitle",
        "originalTitle",
        "mainDescription",
        "shortDescription"
        })
public class MisDataView {

    private Long id;

    private String mainTitle;

    private String episodeTitle;

    private String originalTitle;

    private String mainDescription;

    private String shortDescription;

    protected MisDataView() {
    }

    public static MisDataView create(MediaObject fullMedia) {
        MisDataView simpleMedia = new MisDataView();

        simpleMedia.id = fullMedia.getId();

        Title tempTitle = fullMedia.findTitle(OwnerType.MIS, TextualType.MAIN);
        if(tempTitle != null) {
            simpleMedia.mainTitle = tempTitle.getTitle();
        }
        tempTitle = fullMedia.findTitle(OwnerType.MIS, TextualType.EPISODE);
        if(tempTitle != null) {
            simpleMedia.episodeTitle = tempTitle.getTitle();
        }
        tempTitle = fullMedia.findTitle(OwnerType.MIS, TextualType.ORIGINAL);
        if(tempTitle != null) {
            simpleMedia.originalTitle = tempTitle.getTitle();
        }

        Description tempDescription = fullMedia.findDescription(OwnerType.MIS, TextualType.MAIN);
        if(tempDescription != null) {
            simpleMedia.mainDescription = tempDescription.getDescription();
        }
        tempDescription = fullMedia.findDescription(OwnerType.MIS, TextualType.EPISODE);
        if(tempDescription != null) {
            simpleMedia.shortDescription= tempDescription.getDescription();
        }
        tempDescription = fullMedia.findDescription(OwnerType.MIS, TextualType.SHORT);
        if(tempDescription != null) {
            simpleMedia.shortDescription = tempDescription.getDescription();
        }

        return simpleMedia;
    }

    public Long getId() {
        return id;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }
}