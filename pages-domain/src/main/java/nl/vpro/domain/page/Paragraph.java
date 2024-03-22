/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.validation.NoHtml;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "paragraphType", propOrder = {"title", "body", "image"})
@XmlAccessorType(XmlAccessType.NONE)
public class Paragraph {

    @NoHtml
    private String title;

    @NoHtml(aggressive = false)
    private String body;

    @Valid
    private Image image;

    public Paragraph() {
    }

    public Paragraph(String title, String body, Image image) {
        this.title = title;
        this.body = body;
        this.image = image;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @XmlElement
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Paragraph paragraph = (Paragraph)o;

        if(body != null ? !body.equals(paragraph.body) : paragraph.body != null) {
            return false;
        }
        if(image != null ? !image.equals(paragraph.image) : paragraph.image != null) {
            return false;
        }
        return title != null ? title.equals(paragraph.title) : paragraph.title == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}
