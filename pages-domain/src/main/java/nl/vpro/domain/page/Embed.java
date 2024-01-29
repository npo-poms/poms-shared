/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import lombok.*;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Segment;

/**
 * An embed represents an embedded {@link MediaObject}. This is indexed with  reference only (see {@link nl.vpro.domain.page.update.EmbedUpdate}, but will be published with the complete actual {@link MediaObject}. Embeds are not visible if this media object is not published.
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "embedType", propOrder = {"title", "description", "media"})
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode
public class Embed {

    @XmlElement
    @Getter
    @Setter
    private String title;

    @XmlElement
    @Getter
    @Setter
    private String description;

    @XmlElements({
        @XmlElement(name = "group",   type = Group.class,   namespace = Xmlns.MEDIA_NAMESPACE),
        @XmlElement(name = "program", type = Program.class, namespace = Xmlns.MEDIA_NAMESPACE),
        @XmlElement(name = "segment", type = Segment.class, namespace = Xmlns.MEDIA_NAMESPACE)
    })
    @JsonIgnore
    private MediaObject media;

    public Embed() {
    }

    public Embed(MediaObject media) {
        this.media = media;
    }

    @lombok.Builder
    public Embed(MediaObject media, String title, String description) {
        this.media = media;
        this.title = title;
        this.description = description;
    }

    @JsonProperty
    public MediaObject getMedia() {
        return media;
    }

    public void setMedia(MediaObject media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return title + ":" + getMedia();
    }
}
