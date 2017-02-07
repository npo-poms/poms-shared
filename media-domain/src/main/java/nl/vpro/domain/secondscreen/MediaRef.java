/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.secondscreen;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.8
 */
@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "mediaRefType")
@JsonPropertyOrder({
    "midRef",
    "type"
})
public class MediaRef {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private MediaObject media;

    @Transient
    private String midRef;

    @Transient
    private MediaType mediaType;

    public MediaRef() {
    }

    public MediaRef(MediaObject media) {
        this.media = media;
    }

    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public MediaObject getMedia() {
        return media;
    }

    @XmlAttribute
    public String getMidRef() {
        if(this.media != null) {
            return media.getMid();
        }
        return midRef;
    }

    public void setMidRef(String midRef) {
        if(media != null) {
            throw new IllegalStateException("Call set midRef on the enclosed media, this is a JAXB only setter");
        }

        this.midRef = midRef;
    }

    @XmlAttribute
    public MediaType getType() {
        return media != null ? MediaType.getMediaType(media) : mediaType;
    }

    public void setType(MediaType type) {
        if(media != null && MediaType.getMediaType(media) != type) {
            throw new IllegalStateException("Supplied type " + type + " does not match media type " + MediaType.getMediaType(media));
        }
        mediaType = type;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        MediaRef mediaRef = (MediaRef)o;

        if(id != null ? !id.equals(mediaRef.id) : mediaRef.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
