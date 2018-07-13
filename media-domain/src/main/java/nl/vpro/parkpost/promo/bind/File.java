/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.parkpost.promo.bind;

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
    private String url;


    @XmlAttribute(name = "Filename")
    private String fileName;


    @XmlAttribute(name = "format")
    private AVFileFormat format;

    @XmlAttribute(name = "height")
    private Integer height;

    @XmlAttribute(name = "width")
    private Integer width;

    @XmlAttribute(name = "bitrate")
    private Integer bitrate;

    public File() {
    }

    @lombok.Builder
    public File(String fileName, AVFileFormat format, int width, int height) {
        this.fileName = fileName;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AVFileFormat getFormat() {
        return format != null ? format : AVFileFormat.forProgramUrl(fileName != null ? fileName : url);
    }

    public void setFormat(AVFileFormat format) {
        this.format = format;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
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
