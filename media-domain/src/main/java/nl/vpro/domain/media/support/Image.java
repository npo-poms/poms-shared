/*
 * Copyright (C) 2006/2007 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 * Creation date 15-nov-2006.
 */

package nl.vpro.domain.media.support;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.image.*;
import nl.vpro.domain.image.backend.BackendImageMetadata;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * <p>
 * A {@link MediaObject} can have more than one images. But among them  {@link #getImageUri()}/{@link #getType()} is unique.
 * </p>
 * <p>
 * The image owner describes an origin of the image. Several media suppliers provide
 * there own images. To prevent conflicts while updating for incoming data, images
 * for those suppliers are kept in parallel.
 * </p>
 * <p>
 *     TODO: I think we may elaborate a bit on image uniqueness. {@link #equals(Object)} implements what is said above. But why e.g. a different {@link #getOwner()} would not make a different image?
 * </p>
 *
 * @author Roelof Jan Koekoek
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
        "date",
        "crids"
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
    "workflow",
    "crids"
})
@Slf4j
public class Image extends PublishableObject<Image>
    implements MutableMetadata<Image>, MutableOwnable, Child<MediaObject> {
    public static final Pattern SERVER_URI_PATTERN = Pattern.compile("^urn:vpro[.:]image:(\\d+)$");

    public static final String BASE_URN = "urn:vpro:media:image:";

    @Serial
    private static final long serialVersionUID = 2182582685395751329L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    @XmlAttribute(required = false)
    OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    @XmlAttribute(required = true)
    @Getter
    @Setter
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
    @Getter
    private String title;

    @Column(name = "imageurl")
    @ImageURI
    @NotNull(groups = {PrePersistValidatorGroup.class})
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE, required = true)
    private String imageUri;

    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Getter
    private String description;

    @Column(name = "`offset`")
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @Getter
    protected java.time.Duration offset;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Getter
    @Setter
    private Integer width;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Getter
    @Setter
    private Integer height;

    @NoHtml
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Getter
    private String credits;

    @URI()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Getter
    private String source;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    @Getter
    private String sourceName;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Embedded
    @Getter
    private License license;

    @ReleaseDate()
    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Getter
    private String date;


    @ElementCollection
    @Column(name = "crid", nullable = false, unique = true)
    @OrderColumn(name = "list_index", nullable = false)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @StringList(maxLength = 255)
    @Getter
    @Setter
    @XmlElement(name = "crid", namespace = Xmlns.SHARED_NAMESPACE)
    @JsonProperty("crids")
    protected List<@NotNull @CRID String> crids;


    @ManyToOne
    @XmlTransient
    private MediaObject mediaObject;


    public Image() {
    }

    public Image(OwnerType owner) {
        this.owner = owner;
        this.workflow = Workflow.PUBLISHED;
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


        public Builder creationDate(Instant instant) {
            return creationInstant(instant);
        }
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
        Instant creationInstant,
        Instant lastModified,
        @Singular List<String> crids
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
        this.creationInstant = creationInstant;
        this.lastModified = lastModified;
        this.crids = crids == null ? null : new ArrayList<>(crids);
    }


    @SuppressWarnings("CopyConstructorMissesField") // not copying embargo and creation/lastmodified
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
        // crids?
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


    @NonNull
    public static Image update(@NonNull Image from, @NonNull Image to) {
        if(! from.getOwner().equals(to.getOwner())) {
            log.info("Updating image {} of different owner  ({} !+ {})", to, to.getOwner(), from.getOwner());
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
        to.setSource(from.getSource());
        return to;
    }

    @Override
    public void setTitle(String title) {
        if(title != null && title.length() > 255) {
            title = title.substring(0, 255);
        }

        this.title = title;
    }

    @Override
    public void setLicense(License license) {
        this.license = license;
    }
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public void setSource(String source) {
        this.source = source;
    }
    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    /**
     * The 'uri' of the image on the image server
     * This means a string of the form urn:vpro:image:&lt;id&gt;
     * where id is the database id of the image in the image database.
     * <p>
     * Several image may share this image uri, if they represent the exact same image
     * (but may vary in meta data)
     *
     */
    @Override
    public String getImageUri() {
        return imageUri;
    }

    public Image setImageUri(String uri) {
        this.imageUri = uri == null ? null : uri.trim();
        return this;
    }

    public Long getImageId() {
        return getIdFromImageUri(imageUri);
    }

    @Nullable
    public static Long getIdFromImageUri(@Nullable String imageUri) {

        if (imageUri == null) {
            return null;
        }
        Matcher matcher = Image.SERVER_URI_PATTERN.matcher(imageUri);
        if(!matcher.find()) {
            return null;
        }

        String id = matcher.group(1);

        if(StringUtils.isEmpty(id)) {
            return null;
        }
        return Long.parseLong(id);
    }


    public Image setOffset(java.time.Duration offset) {
        this.offset = offset;
        return this;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public Image setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted == Boolean.TRUE;
        return this;
    }

    @Override
    public void setCredits(String credits) {
        this.credits = !StringUtils.isBlank(credits) ? credits : null;
    }

    @Override
    public void setDate(String date) {
        this.date = !StringUtils.isBlank(date) ? date : null;
    }

    @NonNull
    @Override
    public Image setPublishStopInstant(Instant publishStop) {
        super.setPublishStopInstant(publishStop);
        return this;
    }

    @NonNull
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

    /**
     * {@inheritDoc}
     *
     * For images in the media database it is "urn:vpro:media:image:"
     */
    @Override
    public String getUrnPrefix() {
        return BASE_URN;
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

        Long otherMoParent = other.mediaObject == null ? null : other.mediaObject.getId();

        return Objects.equals(imageUri, other.imageUri)
            && Objects.equals(type, other.type)
            //&& Objects.equals(owner, other.owner)
            //&& Objects.equals(title, other.title)
            //&& Objects.equals(description, other.description)
            && (moParent == null || otherMoParent == null || Objects.equals(moParent, otherMoParent))
        ;
    }

    public static Image of(@NonNull BackendImageMetadata<?> metaData) {
        Image image = new Image();
        image.copyFrom(metaData);
        return image;
    }

    @Override
    public ChangeReport copyFrom(@NonNull MutableMetadata<?> metadata) {
        ChangeReport change = MutableMetadata.super.copyFrom(metadata);
        if (metadata.getImageUri() != null) {
            if (imageUri == null) {
                setImageUri(metadata.getImageUri());
                change.change();
            } else if (!Objects.equals(imageUri, metadata.getImageUri())) {
                log.warn("Can't update imageUri of {} (from {})", this, metadata);
            }
        }
        return change;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (imageUri == null ? 0 : imageUri.hashCode());
    }


}
