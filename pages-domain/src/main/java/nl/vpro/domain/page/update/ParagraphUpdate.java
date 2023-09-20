/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.io.Serial;
import java.io.Serializable;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.page.Paragraph;
import nl.vpro.validation.NoHtml;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "paragraphUpdateType", propOrder = {"title", "body", "image"})
@XmlAccessorType(XmlAccessType.NONE)
public class ParagraphUpdate implements Serializable {

    @Serial
    private static final long serialVersionUID = 5358055471817534969L;

    @NoHtml
    private String title;

    @NoHtml
    private String body;

    public static ParagraphUpdate of(String title, String body) {
        return new ParagraphUpdate(title, body, null);
    }

    public static ParagraphUpdate of(Paragraph p) {
        return new ParagraphUpdate(p.getTitle(), p.getBody(), ImageUpdate.of(p.getImage()));
    }


    public static ParagraphUpdate body(String body) {
        return new ParagraphUpdate(null, body, null);
    }

    public static ParagraphUpdate of(String title) {
        return of(title, null);
    }

    @Valid
    private ImageUpdate image;

    public ParagraphUpdate() {
    }

    @lombok.Builder
    public ParagraphUpdate(String title, String body, ImageUpdate image) {
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public Paragraph toParagraph() {
        Paragraph result = new Paragraph();
        result.setTitle(title);
        result.setBody(body);
        if (image != null) {
            result.setImage(image.toImage());
        }
        return result;
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
    public ImageUpdate getImage() {
        return image;
    }

    public void setImage(ImageUpdate image) {
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

        ParagraphUpdate paragraph = (ParagraphUpdate)o;

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

    @Override
    public String toString() {
        return title + ":" + body;
    }
}
