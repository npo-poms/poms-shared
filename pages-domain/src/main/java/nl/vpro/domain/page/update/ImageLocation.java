/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.validation.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageLocationType", propOrder = {
    "url"
})
public final class ImageLocation implements Serializable{

    @Serial
    private static final long serialVersionUID = 7949324241319668897L;

    @XmlElement
    @NotNull(message = "provide image location")
    @URI(message = "provide a valid url to image location")
    private String url;

    public ImageLocation() {

    }

    public ImageLocation(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageLocation" +
            "{url='" + url + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageLocation that = (ImageLocation)o;

        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
