/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.TypedText;
import nl.vpro.domain.media.support.TextualType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "descriptionUpdateType",
    propOrder = {"description"})
public class DescriptionUpdate implements Comparable<DescriptionUpdate>, TypedText {

    private String description;

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
    @XmlAttribute
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


    void afterUnmarshal(Unmarshaller unmarshaller, Object media) {
        this.media = (MediaUpdate)media;
    }

    @Override
    public int compareTo(DescriptionUpdate description) {
        if (description == null) return -1;
        return type.ordinal() - description.getType().ordinal();
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
