/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.io.Serializable;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.page.Link;
import nl.vpro.domain.page.LinkType;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "linkUpdateType")
public class LinkUpdate implements Serializable{

    @NotNull
    @URI(lenient = true, mustHaveScheme = true, minHostParts = 2)
    @XmlAttribute
    private String pageRef;

    @NoHtml
    private String text;

    @XmlAttribute
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

    public String getPageRef() {
        return pageRef;
    }

    public void setPageRef(String pageRef) {
        this.pageRef = pageRef;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
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

    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LinkUpdate{" +
            "pageRef='" + pageRef + '\'' +
            ", text='" + text + '\'' +
            '}';
    }
}
