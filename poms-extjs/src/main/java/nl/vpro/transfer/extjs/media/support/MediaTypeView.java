/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import nl.vpro.domain.media.MediaType;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;


@XmlAccessorType(XmlAccessType.FIELD)
public class MediaTypeView {
    private String mediaClass;

    private String type;

    private String text;

    private boolean supportsNew;

    private boolean hasSegments;

    private boolean hasEpisodeOf;

    private String[] preferredEpisodeOf;

    private String[] allowedEpisodeOf;

    private boolean hasEpisodes;

    private String[] preferredEpisode;

    private String[] allowedEpisode;

    private boolean hasMemberOf;

    private boolean hasMembers;

    private boolean hasOrdering;

    private MediaTypeView() {
    }

    private MediaTypeView(String mediaClass, String type, String text) {
        this.mediaClass = mediaClass;
        this.type = type;
        this.text = text;
    }

    public static MediaTypeView create(MediaPermissionEvaluator permissionEvaluator, MediaType fullType) {
        if (fullType == null) {
            return null;
        }
        MediaTypeView simpleType = new MediaTypeView(fullType.getMediaClass(), fullType.name(), fullType.toString());
        simpleType.hasSegments = fullType.hasSegments();
        simpleType.hasEpisodes = fullType.hasEpisodes();
        simpleType.preferredEpisodeOf = toStringTypes(fullType.preferredEpisodeOfTypes());
        simpleType.allowedEpisodeOf = toStringTypes(fullType.allowedEpisodeOfTypes());
        simpleType.hasEpisodeOf = fullType.hasEpisodeOf();
        simpleType.preferredEpisode = toStringTypes(fullType.preferredEpisodeTypes());
        simpleType.allowedEpisode = toStringTypes(fullType.allowedEpisodeTypes());
        simpleType.supportsNew = permissionEvaluator.mayCreate(fullType);
        simpleType.hasMembers = fullType.hasMembers();
        simpleType.hasMemberOf = fullType.hasMemberOf();
        simpleType.hasOrdering = fullType.hasOrdering();

        return simpleType;
    }



    public String getMediaClass() {
        return mediaClass;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public boolean getSupportsNew() {
        return supportsNew;
    }

    public boolean getHasSegments() {
        return hasSegments;
    }

    public boolean getHasEpisodeOf() {
        return hasEpisodeOf;
    }

    public String[] getPreferredEpisodeOf() {
        return preferredEpisodeOf;
    }

    public String[] getAllowedEpisodeOf() {
        return allowedEpisodeOf;
    }

    public boolean getHasEpisodes() {
        return hasEpisodes;
    }

    public String[] getAllowedEpisode() {
        return allowedEpisode;
    }

    public String[] getPreferredEpisode() {
        return preferredEpisode;
    }

    public boolean getHasMemberOf() {
        return hasMemberOf;
    }

    public boolean getHasMembers() {
        return hasMembers;
    }

    public boolean getHasOrdering() {
        return hasOrdering;
    }

    private static String[] toStringTypes(MediaType[] types) {
        List<String> list = new ArrayList<String>();
        for(MediaType type : types) {
            list.add(type.name());
        }

        return list.toArray(new String[0]);
    }
}
