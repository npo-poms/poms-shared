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
 * See <a href="https://jira.vpro.nl/browse/MSE-1324">MSE-1324</a>,
 * See <a href="https://jira.vpro.nl/browse/MSE-2402">MSE-2402</a>
 * <p>
 * Promos could be shipped with a file element, which is not used anymore.
 * <pre>
 * {@code
 * mediaprod=>  select m.lastmodified  from mediaobject m join program p on m.id = p.id join location l on p.id = l.mediaobject_id  where type = 'PROMO' and not(l.programurl like 'npo%') order by l.id desc limit 3;
 *         lastmodified
 * ----------------------------
 *  2019-03-27 14:17:37.283+00
 *  2019-03-27 14:17:37.283+00
 *  2019-03-27 14:16:55.549+00
 * (3 rows)
 * }
 * </pre>
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 * @deprecated Not used since 2019
 */
@Setter
@XmlAccessorType(XmlAccessType.NONE)
@Deprecated
public class File {

    @XmlValue
    @Getter
    private String url;


    @XmlAttribute(name = "Filename")
    @Getter
    private String fileName;


    @XmlAttribute(name = "format")
    private AVFileFormat format;

    @XmlAttribute(name = "height")
    @Getter
    private Integer height;

    @XmlAttribute(name = "width")
    @Getter
    private Integer width;

    @XmlAttribute(name = "bitrate")
    @Getter
    private Integer bitrate;

    public File() {
    }

    @lombok.Builder
    private File(String fileName, AVFileFormat format, Integer width, Integer height, Integer bitrate, String url) {
        this.fileName = fileName;
        this.format = format;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.url = url;
    }

    public AVFileFormat getFormat() {
        return format != null ? format : AVFileFormat.forProgramUrl(fileName != null ? fileName : url);
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
