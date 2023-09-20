/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.AVFileFormat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "locationType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Comparable<Location> {

    @XmlElement
    private AVFileFormat avFileFormat;

    @XmlElement
    private String programUrl;

    public static Location from(nl.vpro.domain.media.Location location) {
        return new Location(location.getAvFileFormat(), location.getProgramUrl());
    }

    public Location() {
    }

    public Location(AVFileFormat avFileFormat, String programUrl) {
        this.avFileFormat = avFileFormat;
        this.programUrl = programUrl;
    }

    public AVFileFormat getAvFileFormat() {
        return avFileFormat;
    }

    public void setAvFileFormat(AVFileFormat avFileFormat) {
        this.avFileFormat = avFileFormat;
    }

    public String getProgramUrl() {
        return programUrl;
    }

    public void setProgramUrl(String programUrl) {
        this.programUrl = programUrl;
    }

    @Override
    public int compareTo(@NonNull Location loc) {

        if (avFileFormat != null && loc.avFileFormat != null && avFileFormat != loc.avFileFormat) {
            return avFileFormat.ordinal() - loc.avFileFormat.ordinal();
        }
        if (getProgramUrl() == null || loc.getProgramUrl() == null) {
            if (!(getProgramUrl() == null && loc.getProgramUrl() == null)) {
                if (getProgramUrl() == null) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }
        return this.getProgramUrl().trim().compareTo(loc.getProgramUrl().trim());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;
        String programUrl1 = location.getProgramUrl();
        if (programUrl!=null) {
            if (programUrl1==null) {
                return false;
            }
            return programUrl.trim().equals(programUrl1.trim());
        } else {
            return programUrl1==null;
        }
    }

    @Override
    public int hashCode() {
        return programUrl != null ? programUrl.trim().hashCode() : 0;
    }
}
