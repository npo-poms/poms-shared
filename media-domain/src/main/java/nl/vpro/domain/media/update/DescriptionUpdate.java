/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.TypedText;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.TextualType;

/**
 * A description of a {@link MediaUpdate}. Like a non {@link Ownable} {@link Description}
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "descriptionUpdateType",
    propOrder = {"description"})
public class DescriptionUpdate implements TypedText {

    private String description;

    @NotNull
    private TextualType type;

    private MediaUpdate media;

    public DescriptionUpdate() {
    }

    public DescriptionUpdate(String description, TextualType type) {
        this.description = description;
        this.type = type;
    }

    DescriptionUpdate(String description, TextualType type, MediaUpdate media) {
        this(description, type);
        this.media = media;
    }


    public static DescriptionUpdate of(TypedText to) {
        return new DescriptionUpdate(to.get(), to.getType());
    }

    @XmlValue
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public MediaUpdate getMedia() {
        return media;
    }

    public void setMedia(MediaUpdate media) {
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
        setDescription(s);
    }

    @Override
    public String get() {
        return getDescription();
    }
}
