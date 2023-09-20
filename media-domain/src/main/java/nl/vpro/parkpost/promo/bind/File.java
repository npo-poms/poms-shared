/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.parkpost.promo.bind;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import nl.vpro.domain.media.AVFileFormat;

/**
 * See https://jira.vpro.nl/browse/MSE-1324
 * See https://jira.vpro.nl/browse/MSE-2402
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.NONE)
public class File {

    @XmlValue
    @Getter
    @Setter
    private String url;


    @XmlAttribute(name = "Filename")
    @Getter
    @Setter
    private String fileName;


    @XmlAttribute(name = "format")
    private AVFileFormat format;

    @XmlAttribute(name = "height")
    @Getter
    @Setter
    private Integer height;

    @XmlAttribute(name = "width")
    @Getter
    @Setter
    private Integer width;

    @XmlAttribute(name = "bitrate")
    @Getter
    @Setter
    private Integer bitrate;

    public File() {
    }

    @lombok.Builder
    private File(String fileName, AVFileFormat format, Integer width, Integer height, Integer bitrate) {
        this.fileName = fileName;
        this.format = format;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
    }

    public AVFileFormat getFormat() {
        return format != null ? format : AVFileFormat.forProgramUrl(fileName != null ? fileName : url);
    }

    public void setFormat(AVFileFormat format) {
        this.format = format;
    }

    public String getExtension() {
        if (fileName != null) {
            return getExtension(fileName);
        } else if (url != null) {
            return getExtension(url);
        }
        return null;
    }
    private String getExtension(String s) {
        int i = s.lastIndexOf('.');
        if (i >= 0) {
            return s.substring(i + 1);
        } else {
            return null;
        }

    }
}
