/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.support.TextualType;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "titleUpdateType",
    propOrder = {"title"})
public class TitleUpdate implements Comparable<TitleUpdate>{

    private String title;

    private TextualType type;

    private MediaUpdate media;

    private TitleUpdate() {
    }

    public static TitleUpdate main(String title) {
        return new TitleUpdate(title, TextualType.MAIN);
    }

    public TitleUpdate(String title, TextualType type) {
        this.title = title;
        this.type = type;
    }

    TitleUpdate(String title, TextualType type, MediaUpdate media) {
        this(title, type);
        this.media = media;
    }

    @XmlValue
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute
    public TextualType getType() {
        return type;
    }

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
    public int compareTo(TitleUpdate title) {

        if (title == null) {
            return -1;
        }

        return (type == null ? -1 : type.ordinal()) - (title.getType() == null ? -1 : title.getType().ordinal());
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
    public String toString() {
        return "TitleUpdate{" +
            "title='" + title + '\'' +
            ", type=" + type +
            ", media=" + (media == null ? null : media.getMid()) +
            '}';
    }
}
