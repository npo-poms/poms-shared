/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.page.Link;
import nl.vpro.domain.page.LinkType;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkUpdateType")
public class LinkUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = -5090488540344490405L;

    @NotNull
    @URI(
        lenient = true,
        mustHaveScheme = true,
        minHostParts = 2,
        schemes = {"http", "https"}
    )
    @XmlAttribute
    @Getter
    @Setter
    @NonNull
    private String pageRef;

    @NoHtml
    @Getter
    @Setter
    private String text;

    @XmlAttribute
    @Getter
    @Setter
    private LinkType type;

    public static LinkUpdate of(@NonNull String pageRef, String text) {
        return new LinkUpdate(pageRef, text);
    }

    public static LinkUpdate of(@NonNull String pageRef) {
        return new LinkUpdate(pageRef, null);
    }

    public static LinkUpdate topStory(@NonNull String pageRef, String text) {
        LinkUpdate update = new LinkUpdate(pageRef, text);
        update.setType(LinkType.TOP_STORY);
        return update;
    }

    public static LinkUpdate of(Link l) {
        LinkUpdate u = new LinkUpdate(l.getPageRef(), l.getText());
        u.setType(l.getType());
        return u;
    }

    public LinkUpdate() {
    }

    public LinkUpdate(@NonNull String pageRef) {
        this.pageRef = pageRef;
    }


    public LinkUpdate(LinkUpdate copy) {
        this.pageRef = copy.pageRef;
        this.type = copy.type;
        this.text = copy.text;
    }


    public LinkUpdate(@NonNull String pageRef, String text) {
        this.pageRef = pageRef;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        LinkUpdate that = (LinkUpdate)o;

        if(!Objects.equals(pageRef, that.pageRef)) {
            return false;
        }
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int hashCode() {
        return pageRef != null ? pageRef.hashCode() : 0;
    }

    public Link toLink() {
        return new Link(pageRef, text, type);
    }

    @Override
    public String toString() {
        return "LinkUpdate{" +
            "pageRef='" + pageRef + '\'' +
            ", text='" + text + '\'' +
            '}';
    }
}
