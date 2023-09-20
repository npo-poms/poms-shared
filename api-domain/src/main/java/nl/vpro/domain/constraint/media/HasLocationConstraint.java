/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.ExistsConstraint;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Platform;

/**
 * @author Rico Jansen
 * @since 2.2
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "hasLocationConstraintType")
public class HasLocationConstraint implements ExistsConstraint<MediaObject> {

    public static final String PLATFORM_NONE = "NONE";

    private Platform platform;

    private boolean noPlatform = false;

    @Override
    public String getESPath() {
        if (platform != null) {
            return "locations.platform";
        } else {
            return "locations.urn";
        }
    }

    @Override
    public boolean test(MediaObject input) {
        return
            !input.getLocations().isEmpty()
            && hasPlatform(input)
            ;
    }


    private boolean hasPlatform(MediaObject input) {
        for (Location l : input.getLocations()) {
            if (noPlatform) {
                if (Objects.equals(null, l.getPlatform())) {
                    return true;
                }
            } else {
                if (platform == null || Objects.equals(platform, l.getPlatform())) {
                    return true;
                }

            }
        }
        return false;
    }


    @XmlAttribute
    public String getPlatform() {
        return platform == null ? (noPlatform ? PLATFORM_NONE : null) : platform.name();
    }

    public void setPlatform(String platform) {
        if (platform == null) {
            this.platform = null;
            this.noPlatform = false;
        } else if (PLATFORM_NONE.equals(platform)) {
            this.platform = null;
            this.noPlatform = true;
        } else {
            this.platform = Platform.valueOf(platform);
            this.noPlatform = false;
        }
    }

    public boolean isNoPlatform() {
        return noPlatform;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "/" + getESPath() + (platform == null ? "" : "{platform='" + platform + "'}");
    }

}
