/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.page.Embed;
import nl.vpro.validation.NoHtml;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "embedUpdateType")
@Builder
public class EmbedUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 2359340553661004262L;

    public static EmbedUpdate of(Embed embed) {
        if (embed == null) {
            return null;
        }
        return EmbedUpdate.builder()
            .title(embed.getTitle())
            .description(embed.getDescription())
            .midRef(embed.getMedia().getMid())
            .build();

    }

    @NotNull
    @XmlAttribute
    @Getter
    @Setter
    private String midRef;

    @NoHtml
    @Getter
    @Setter
    private String title;

    @NoHtml(aggressive = false)
    @Getter
    @Setter
    private String description;

    public EmbedUpdate() {
    }

    public EmbedUpdate(String midRef) {
        this.midRef = midRef;
    }

    public EmbedUpdate(String midRef, String title) {
        this.midRef = midRef;
        this.title = title;
    }

    public EmbedUpdate(String midRef, String title, String description) {
        this.midRef = midRef;
        this.title = title;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        EmbedUpdate that = (EmbedUpdate)o;

        return midRef != null ? midRef.equals(that.midRef) : that.midRef == null;
    }

    @Override
    public int hashCode() {
        return midRef != null ? midRef.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "EmbedUpdate{" +
            "midRef='" + midRef + '\'' +
            ", title='" + title + '\'' +
            '}';
    }

}
