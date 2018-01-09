/*
 * Copyright (C) 2006/2007 All rights reserved
 * VPRO The Netherlands
 * Creation date 15-nov-2006.
 */

package nl.vpro.domain.media.support;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.EmbargoBuilder;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.image.Metadata;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.secondscreen.Screen;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * <p>
 * A {@link MediaObject} can have more than one images which should differ in URL and
 * owner.
 * </p><p>
 * The image owner describes an origin of the image. Several media suppliers provide
 * there own images. To prevent conflicts while updating for incoming data, images
 * for those suppliers are kept in parallel.
 * </p>
 *
 * @author Roelof Jan Koekoek
 * @see nl.vpro.domain.media.support.OwnerType
 * @since 0.4
 */
@Entity
@Table(uniqueConstraints = {
    //@UniqueConstraint(columnNames = {"mediaobject_id", "list_index"})
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
@JsonPropertyOrder({
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
    "date",
    "owner",
    "type",
    "highlighted",
    "creationDate",
    "workflow"
})
@Slf4j
public class Image extends PublishableObject<Image>
    implements Metadata<Image>, Ownable, MediaObjectChild, Child<MediaObject> {
    public static final Pattern SERVER_URI_PATTERN = Pattern.compile("^urn:vpro[.:]image:(\\d+)$");

    public static final String BASE_URN = "urn:vpro:media:image:";

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

    @Column(name = "`offset`")
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    protected java.time.Duration offset;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private Integer width;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private Integer height;

    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    private String credits;

    @URI()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    private String source;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    private String sourceName;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Embedded
    @Getter
    @Setter
    private License license;

    @ReleaseDate()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    private String date;

    @ManyToOne
    @XmlTransient
    private MediaObject mediaObject;

    @ManyToOne
    @XmlTransient
    private Screen secondscreen;


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

    public static class Builder implements EmbargoBuilder<Builder> {
        private OwnerType owner = OwnerType.BROADCASTER;
        private ImageType type = ImageType.PICTURE;

    }

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    protected Image(
        OwnerType owner,
        ImageType type,
        String imageUri,
        String title,
        String description,
        Long id,
        String credits,
        License license,
        String source,
        Integer height,
        Integer width,
        java.time.Duration offset,
        String date,
        Instant publishStart,
        Instant publishStop,
        Instant creationDate,
        Instant lastModified
        ) {
        this(owner, type, imageUri);
        this.title = title;
        this.description = description;
        this.id = id;
        this.credits = credits;
        this.license = license;
        this.source = source;
        this.height = height;
        this.width = width;
        this.offset = offset;
        this.date = date;
        this.publishStart = publishStart;
        this.publishStop = publishStop;
        this.creationDate = creationDate;
        this.lastModified = lastModified;

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
        if (urn == null)  {
            return null;
        }
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

    @Override
    public ImageType getType() {
        return type;
    }

    @Override
    public void setType(ImageType type) {
        this.type = type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if(title != null && title.length() > 255) {
            title = title.substring(0, 254);
        }

        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        if(description != null && description.length() > 255) {
            description = description.substring(0, 254);
        }

        this.description = description;
    }

    @Override
    public String getImageUri() {
        return imageUri;
    }

    public Image setImageUri(String uri) {
        this.imageUri = uri == null ? null : uri.trim();
        return this;
    }

    public java.time.Duration getOffset() {
        return offset;
    }

    public Image setOffset(java.time.Duration offset) {
        this.offset = offset;
        return this;
    }


    @Override
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width The width to set.
     */
    @Override
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height The height to set.
     */
    @Override
    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public Image setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted == Boolean.TRUE;
        return this;
    }

    @Override
    public String getCredits() {
        return credits;
    }

    @Override
    public void setCredits(String credits) {
        this.credits = !StringUtils.isBlank(credits) ? credits : null;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String creditURL) {
        this.source = creditURL;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = !StringUtils.isBlank(date) ? date : null;
    }

    @Override
    public Image setPublishStopInstant(Instant publishStop) {
        super.setPublishStopInstant(publishStop);
        return this;
    }

    @Override
    public Image setPublishStartInstant(Instant publishStart) {
        super.setPublishStartInstant(publishStart);
        return this;
    }

    @Override
    public MediaObject getParent() {
        return mediaObject;
    }

    @Override
    public void setParent(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public Screen getSecondscreen() {
        return secondscreen;
    }

    public void setSecondscreen(Screen mediaObject) {
        this.secondscreen = mediaObject;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Image other = (Image) obj;

        if ((id != null && other.id != null) && !Objects.equals(id, other.id)) {
            return false;
        }

        Long moParent = mediaObject == null ? null : mediaObject.getId();
        Long ssParent = secondscreen == null ? null : secondscreen.getId();

        Long otherMoParent = other.mediaObject == null ? null : other.mediaObject.getId();
        Long otherSsParent = other.secondscreen == null ? null : other.secondscreen.getId();

        return Objects.equals(imageUri, other.imageUri)
            && Objects.equals(type, other.type)
            && (moParent == null || otherMoParent == null || Objects.equals(moParent, otherMoParent))
            && (ssParent == null || otherSsParent == null || Objects.equals(ssParent, otherSsParent))
        ;
    }

    public static Image of(ImageMetadata<?> metaData) {
        Image image = new Image();
        image.copyFrom(metaData);
        return image;
    }

    @Override
    public void copyFrom(Metadata<?> metadata) {
        Metadata.super.copyFrom(metadata);
        if (metadata.getImageUri() != null) {
            if (imageUri == null) {
                setImageUri(metadata.getImageUri());
            } else if (!Objects.equals(imageUri, metadata.getImageUri())) {
                log.warn("Can't update imageUri of {} (from {})", this, metadata);
            }
        }
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (imageUri == null ? 0 : imageUri.hashCode());
    }


}
