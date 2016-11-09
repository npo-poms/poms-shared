/*
 * Copyright (C) 2006/2007 All rights reserved
 * VPRO The Netherlands
 * Creation date 15-nov-2006.
 */

package nl.vpro.domain.media.support;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.ImageURI;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.ReleaseDate;
import nl.vpro.xml.bind.DateToDuration;

/**
 * A {@link MediaObject} can have more than one images which should differ in URL and
 * owner.
 * <p/>
 * The image owner describes an origin of the image. Several media suppliers provide
 * there own images. To prevent conflicts while updating for incoming data, images
 * for those suppliers are kept in parallel.
 * <p/>
 *
 * @author Roelof Jan Koekoek
 * @see nl.vpro.domain.media.support.OwnerType
 * @since 0.4
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mediaobject_id", "list_index"})
})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageType",
    namespace = Xmlns.SHARED_NAMESPACE,
    propOrder = {
        "title",
        "description",
        "imageUri",
        "offset",
        "height",
        "width",
        "credits",
        "source",
        "sourceName",
        "license",
        "date"
    })
public class Image extends PublishableObject implements Ownable {
    public static final Pattern SERVER_URI_PATTERN = Pattern.compile("^urn:vpro[.:]image:(\\d+)$");

    private static final String BASE_URN = "urn:vpro:media:image:";

    private static final long serialVersionUID = 2182582685395751329L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute(required = false)
    OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    @XmlAttribute(required = true)
    ImageType type = ImageType.PICTURE;

    @XmlAttribute(required = true)
    @Column(nullable = false)
    Boolean highlighted = false;

    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE, required = true)
    private String title;

    @Column(name = "imageurl")
    @ImageURI
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE, required = true)
    private String imageUri;

    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private String description;

    @Temporal(TemporalType.TIME)
    @Column(name = "start_offset")
    @XmlJavaTypeAdapter(DateToDuration.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    protected Date offset;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private Integer width;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private Integer height;

    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private String credits;

    @URL()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private String source;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    private String sourceName;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Enumerated(EnumType.STRING)
    private License license;


    @ReleaseDate()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private String date;

    @Column(name = "list_index", nullable = false)
    @XmlTransient
    private int listIndex = 0;

    @ManyToOne
//    @JoinColumn(insertable=false, updatable=false, nullable=false)
    @XmlTransient
    private MediaObject mediaObject;

    public Image() {
    }

    public Image(OwnerType owner) {
        this.owner = owner;
    }

    public Image(OwnerType owner, String imageUri) {
        this(owner);
        this.imageUri = imageUri;
    }

    public Image(OwnerType owner, ImageType type, String imageUri) {
        this(owner, imageUri);
        this.type = type;
    }

    public Image(Image source) {
        super(source);
        this.owner = source.owner;
        this.type = source.type;
        this.highlighted = source.highlighted;
        this.title = source.title;
        this.imageUri = source.imageUri;
        this.description = source.description;
        this.offset = source.offset;
        this.width = source.width;
        this.height = source.height;
        this.credits = source.credits;
        this.source = source.source;
        this.date = source.date;
    }

    public static Image copy(Image source) {
        if(source == null) {
            return null;
        }
        return new Image(source);
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(BASE_URN.length());
        return Long.valueOf(id);
    }


    public static Image update(Image from, Image to, OwnerType owner) {
        if(from != null) {
            if(to == null) {
                to = new Image(owner);
            }

            if(!owner.equals(to.getOwner())) {
                throw new UnsupportedOperationException("Can not update the owner field for an image");
            }

            to.setTitle(from.getTitle());
            to.setDescription(from.getDescription());
            to.setType(from.getType());
            to.setHeight(from.getHeight());
            to.setWidth(from.getWidth());
            to.setType(from.getType());
            to.setImageUri(from.getImageUri());
            to.setLicense(from.getLicense());
            to.setSourceName(from.getSourceName());
            return to;
        } else {
            return null;
        }
    }

    @Override
    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public void setOwner(OwnerType owner) {
        this.owner = owner;
    }

    public ImageType getType() {
        return type;
    }

    public Image setType(ImageType type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Image setTitle(String title) {
        if(title != null && title.length() > 255) {
            title = title.substring(0, 254);
        }

        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Image setDescription(String description) {
        if(description != null && description.length() > 255) {
            description = description.substring(0, 254);
        }

        this.description = description;
        return this;
    }

    public String getImageUri() {
        return imageUri;
    }

    public Image setImageUri(String uri) {
        this.imageUri = uri == null ? uri : uri.trim();
        return this;
    }

    public Date getOffset() {
        return offset;
    }

    public void setOffset(Date offset) {
        this.offset = offset;
    }

    @JsonIgnore
    public void setOffset(java.time.Duration offset) {
        this.offset = new Date(offset.toMillis());
    }

    public Integer getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    public Image setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    public Image setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public Image setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted == Boolean.TRUE;
        return this;
    }

    public String getCredits() {
        return credits;
    }

    public Image setCredits(String credits) {
        this.credits = !StringUtils.isBlank(credits) ? credits : null;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Image setSource(String creditURL) {
        this.source = creditURL;
        return this;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Image setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Image setDate(String date) {
        this.date = !StringUtils.isBlank(date) ? date : null;
        return this;
    }

    @Override
    public Image setPublishStop(Date publishStop) {
        super.setPublishStop(publishStop);
        return this;
    }

    @Override
    public Image setPublishStart(Date publishStart) {
        super.setPublishStart(publishStart);
        return this;
    }

    public License getLicense() {
        return license;
    }

    public Image setLicense(License license) {
        this.license = license;
        return this;
    }


    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    @Override
    public String getUrnPrefix() {
        return "urn:vpro:media:image:";
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("owner", owner)
            .append("type", type)
            .append("highlighted", highlighted)
            .append("title", title)
            .append("imageUri", imageUri)
            .append("description", description)
            .append("width", width)
            .append("height", height)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Image image = (Image)o;
        if(id != null && id.equals(image.id)) {
            return true;
        }

        if(!Objects.equals(imageUri, image.imageUri)) {
            return false;
        }

        return true;
    }

    public static Image of(ImageMetadata metaData) {
        Image image = new Image();
        image.setImageUri(metaData.getUrn());
        image.setType(metaData.getImageType());
        image.setTitle(metaData.getTitle());
        image.setDescription(metaData.getDescription());
        image.setHeight(metaData.getHeight());
        image.setWidth(metaData.getWidth());
        return image;
    }

    public Image copyFrom(ImageMetadata metaData) {
        setImageUri(metaData.getUrn());
        setType(metaData.getImageType());
        setTitle(metaData.getTitle());
        setDescription(metaData.getDescription());
        setHeight(metaData.getHeight());
        setWidth(metaData.getWidth());
        return this;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (imageUri == null ? 0 : imageUri.hashCode());
    }
}
