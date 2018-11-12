/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.secondscreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.validation.URI;

/**
 * @author Roelof Jan Koekoek
 * @since 3.8
 */
@Entity(name = "screen")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "screenType", propOrder = {
    "title",
    "description",
    "url",
    "screenOf",
    "images"
})
@JsonPropertyOrder({
    "title",
    "description",
    "url",
    "screenOf",
    "images"
})
public class Screen extends PublishableObject<Screen> {

    protected static final String SID_PREFIX = "SCREEN_";

    @Column(nullable = false)
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @XmlElement
    private String title;

    @Column(nullable = false, length = 1024)
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 1024, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @XmlElement
    private String description;

    @Column(nullable = false, length = 1024)
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 1024, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @URI
    @XmlElement
    @XmlSchemaType(name = "anyURI")
    private String url;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "screen_id")
    @OrderColumn(name = "list_index", nullable = true)
    @XmlElement
    private List<MediaRef> screenOf;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "secondscreen_id")
    @OrderColumn(name = "list_index", nullable = true)
    @Valid
    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image", namespace = Xmlns.SHARED_NAMESPACE)
    @JsonProperty("images")
    protected List<Image> images;

    @XmlAttribute
    public String getSid() {
        return SID_PREFIX + getId();
    }

    public void setSid(String sid) {
        if(sid != null) {
            setId(Long.valueOf(sid.substring(7)));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addMediaRef(MediaObject media) {
        addMediaRef(new MediaRef(media));
    }

    public void addMediaRef(MediaRef mediaRef) {
        getScreenOf().add(mediaRef);
    }

    public List<MediaRef> getScreenOf() {
        if(screenOf == null) {
            screenOf = new ArrayList<>();
        }

        return screenOf;
    }

    public void setScreenOf(List<MediaRef> screenOf) {
        this.screenOf = screenOf;
    }

    public MediaRef findScreenOf(Long id) {
        if(screenOf != null && id != null) {
            for(MediaRef image : screenOf) {
                if(image != null) {
                    if(id.equals(image.getId())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public void addScreenOf(MediaObject media) {
        if(screenOf == null) {
            screenOf = new ArrayList<>(1);
        } else {
            for(MediaRef mediaRef : screenOf) {
                if(mediaRef.getMidRef().equals(media.getMid())) {
                    return;
                }
            }
        }

        screenOf.add(new MediaRef(media));
    }

    public boolean removeScreenOf(MediaObject media) {
        if(screenOf == null) {
            return false;
        }

        boolean answer = false;
        for(Iterator<MediaRef> iterator = screenOf.iterator(); iterator.hasNext(); ) {
            MediaRef next = iterator.next();
            if(next.getMedia().equals(media)) {
                iterator.remove();
                answer |= true;
            }
        }

        return answer;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        if(images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
    }

    public Image findImage(Long id) {
        if(images != null && id != null) {
            for(Image image : getImages()) {
                if(image != null) {
                    if(id.equals(image.getId())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public boolean removeImage(Image image) {
        if(images != null) {
            return images.remove(image);
        }
        return false;
    }

    public boolean removeImage(Long imageId) {
        boolean success = false;
        if(imageId != null && images != null) {

            for(Image image : getImages()) {
                if(imageId.equals(image.getId())) {
                    success = removeImage(image);
                    break;
                }
            }
        }
        return success;
    }

    @Override
    protected String getUrnPrefix() {
        return "urn:vpro:secondscreen:screen:";
    }
}
