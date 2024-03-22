/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.TypedText;
import nl.vpro.domain.media.support.*;
import nl.vpro.validation.NoHtml;

/**
 * A description of a {@link MediaUpdate}. Like a non {@link MutableOwnable} {@link Description}
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "descriptionUpdateType",
    propOrder = {})
public class DescriptionUpdate implements TypedText {

    @XmlValue
    @NoHtml(aggressive = false)
    private String value;

    @NotNull
    private TextualType type;

    private MediaUpdate<?> media;

    private DescriptionUpdate() {
        // needed by jaxb
    }


    public DescriptionUpdate(@Nullable String description, @NonNull TextualType type) {
        this.value = description;
        this.type = type;
    }

    public static DescriptionUpdate of(@NonNull TypedText to) {
        return new DescriptionUpdate(to.get(), to.getType());
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
    public MediaUpdate<?> getMedia() {
        return media;
    }

    public void setMedia(MediaUpdate<?> media) {
        this.media = media;
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaUpdate) {
            this.media = (MediaUpdate) parent;
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

        DescriptionUpdate that = (DescriptionUpdate)o;

        if(media != null ? !media.equals(that.media) : that.media != null) {
            return false;
        }
        if(type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (media != null ? media.hashCode() : 0);
        return result;
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
