/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.Builder;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.validation.NoHtml;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "embedUpdateType")
@Builder
public class EmbedUpdate implements Serializable {

    @NotNull
    @XmlAttribute
    private String midRef;

    @NoHtml
    private String title;

    @NoHtml
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

    public String getMidRef() {
        return midRef;
    }

    public void setMidRef(String midRef) {
        this.midRef = midRef;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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
