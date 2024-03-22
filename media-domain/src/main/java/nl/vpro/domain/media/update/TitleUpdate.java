/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.TypedText;
import nl.vpro.domain.media.support.*;
import nl.vpro.validation.NoHtml;

/**
 * A title of a {@link MediaUpdate}. Like a non {@link MutableOwnable} {@link Title}
 * @see nl.vpro.domain.media.update
 * @see Title
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "titleUpdateType",
    propOrder = {})
public class TitleUpdate implements TypedText {

    @XmlValue
    @NoHtml(aggressive = false)
    private String value;

    @NotNull
    private TextualType type;

    private MediaUpdate<?> media;

    private TitleUpdate() {
    }

    @SuppressWarnings("ConfusingMainMethod")
    public static TitleUpdate main(String title) {
        return new TitleUpdate(title, TextualType.MAIN);
    }

    public TitleUpdate(String title, @NonNull TextualType type) {
        this.value  = title;
        this.type = type;
    }

    public static TitleUpdate of(@NonNull TypedText to) {
        return new TitleUpdate(to.get(), to.getType());
    }


    @Override
    @XmlAttribute(required = true)
    public TextualType getType() {
        return type;
    }

    @Override
    public void setType(TextualType type) {
        this.type = type;
    }

    @XmlTransient
    public MediaUpdate<?>getMedia() {
        return media;
    }

    public void setMedia(MediaUpdate<?> media) {
        this.media = media;
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaUpdate parentu) {
            this.media = parentu;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        TitleUpdate that = (TitleUpdate)o;

        if(media != null ? !media.equals(that.media) : that.media != null) {
            return false;
        }
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (media != null ? media.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "TitleUpdate{" +
            "title='" + value + '\'' +
            ", type=" + type +
            ", media=" + (media == null ? null : media.getMid()) +
            '}';
    }

    @Override
    public void set(String s) {
        this.value = s;
    }

    @Override
    public String get() {
        return value;
    }
}
