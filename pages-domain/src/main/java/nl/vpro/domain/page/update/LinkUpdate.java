/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.page.*;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkUpdateType")
public class LinkUpdate implements Serializable{

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

    public static LinkUpdate of(String pageRef, String text) {
        return new LinkUpdate(pageRef, text);
    }

    public static LinkUpdate of(String pageRef) {
        return new LinkUpdate(pageRef, null);
    }

    public static LinkUpdate topStory(String pageRef, String text) {
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


    public LinkUpdate(String pageRef, String text) {
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

        if(pageRef != null ? !pageRef.equals(that.pageRef) : that.pageRef != null ) {
            return false;
        }
        return type != null ? type.equals(that.type) : that.type == null;
    }

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
