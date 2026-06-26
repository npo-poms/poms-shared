/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import jakarta.activation.DataHandler;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.image.MutableMetadata;
import nl.vpro.domain.image.backend.BackendImage;
import nl.vpro.domain.image.backend.BackendImageMetadata;
import nl.vpro.domain.media.Deletable;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * An 'update' version of {@link Image}.
 * @see nl.vpro.domain.media.update
 * @see Image
 */

@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageUpdateType", propOrder = {
    "title",
    "description",
    "source",
    "sourceName",
    "license",
    "width",
    "height",
    "credits",
    "date",
    "offset",
    "xmlImage",
    "crids"
})
@Slf4j
@Getter
public class ImageUpdate implements MutableEmbargo<ImageUpdate>, MutableMetadata<ImageUpdate>, Deletable {

    private static final Jackson2Mapper unwrapper = Jackson2Mapper.getLenientInstance();

    @XmlAttribute(required = true)
    @NotNull
    @Setter
    private ImageType type;

    /**
     * The URN of the image object in the media object. This is basically the id prefixed with {@link Image#getUrnPrefix()}
     */
    @XmlAttribute(name = "urn")
    @Pattern(regexp = "^urn:vpro:media:image:[0-9]+$")
    private String urn;


    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStartInstant;

    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStopInstant;

    @XmlAttribute(required = true)
    @Getter
    @Setter
    Boolean highlighted = false;

    @XmlElement(required = true)
    @NotNull(message = "provide title for imageUpdate")
    @Size.List({@Size(max = 255), @Size(min = 1)})
    @Setter
    private String title;

    @XmlElement(required = false)
    @Setter
    private String description;

    @XmlElement
    @Positive
    @Setter
    private Integer height;

    @XmlElement
    @Positive
    @Setter
    private Integer width;

    @NoHtml(aggressive = false)
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    @Setter

    private String credits;


    /**
     * An URL describing where the image originates from.
     */
    @URI(mustHaveScheme = true, minHostParts = 2, groups = {PomsValidatorGroup.class})
    @XmlElement
    @NotNull(groups = {WeakWarningValidatorGroup.class})
    @Setter
    private String source;

    @XmlElement
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    @Setter
    private String sourceName;

    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    @Valid
    @Setter
    private License license;

    @ReleaseDate()
    @XmlElement
    @Setter
    private String date;

    @Temporal(TemporalType.TIME)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected java.time.Duration offset;

    /**
     * <p>
     * Description of the image. If this describes an existing {@link Image} then the type of this
     * is a {@link String} for {@link Image#getUrn()}.
     * </p>
     * <p>
     * It can also be an {@link ImageData} or an {@link ImageLocation} in which case this object describes a <em>new</em> image.
     * </p>
     */
    @XmlTransient
    @JsonIgnore
    @Valid
    private ImageWrapper wrapper;


    @XmlElement(name = "crid")
    private List<@NotNull @CRID String> crids;

    @XmlTransient
    private Instant lastModified;

    @XmlTransient
    private Instant creationDate;


    @XmlAttribute
    private Boolean delete;

    public ImageUpdate() {
    }

    public ImageUpdate(@NonNull ImageType type, @NonNull String title, String description, ImageData image) {
        this.description = description;
        this.title = title;
        this.type = type;
        setImage(image);
    }

    public ImageUpdate(@NonNull ImageType type, @NonNull String title, String description, ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        setImage(image);
    }

    public ImageUpdate(@NonNull ImageType type, @NonNull String title, String description, @URI(schemes = "urn", patterns = {"urn:vpro:image:\\d+"}) String urn) {
        this.description = description;
        this.title = title;
        this.type = type;
        setImage(urn);
    }

    /**
     */
    @Override
    public String getImageUri() {
        if (wrapper != null) {
            return wrapper.getUrn();
        }
        return null;
    }

    @Override
    public void setLastModifiedInstant(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Instant getLastModifiedInstant() {
        return lastModified;
    }

    @Override
    public Instant getCreationInstant() {
        return creationDate;
    }

    public static class Builder implements EmbargoBuilder<Builder> {

        public Builder imageUrl(String imageLocation) {
            return imageLocation(new ImageLocation(imageLocation));
        }

        public Builder imageUrl(@Nullable String mimeType, String imageLocation) {
            return imageLocation(ImageLocation.builder().mimeType(mimeType).url(imageLocation).build());
        }

        public Builder imageDataHandler(DataHandler dataHandler) {
            return imageData(new ImageData(dataHandler));
        }

        @Override
        public Builder self() {
            return this;
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private ImageUpdate(
        @NonNull ImageType type,
        @NonNull String title,
        String description,
        ImageLocation imageLocation,
        ImageData imageData,
        String imageUrn,
        @NonNull License license,
        @NonNull String source,
        @NonNull String sourceName,
        @NonNull String credits,
        Instant publishStart,
        Instant publishStop,
        List<String> crids,
        Boolean delete
        ) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.wrapper = ImageWrapper.of(imageLocation, imageData, imageUrn);
        if (!imageDataValid()) {
            throw new IllegalStateException("Can specify only on of imageLocation, imageData or imageUrn");
        }
        this.license = license;
        this.sourceName = sourceName;
        this.source = source;
        this.credits = credits;
        this.publishStartInstant = publishStart;
        this.publishStopInstant = publishStop;
        this.crids = crids == null ? null: new ArrayList<>(crids);
        this.delete = delete == null || ! delete ? null : delete;
    }


    public ImageUpdate(Image image) {
        copyFrom(image);
        highlighted = image.isHighlighted();
        String uri = image.getImageUri();
        if (uri != null) {
            if (uri
                .replace('.', ':') // See MSE-865
                .startsWith(BackendImage.BASE_URN)) {
                setImage(uri);
            } else if (uri.startsWith("urn:")) {
                log.warn("Uri starts with a non image urn: {}. Not taking it as an url, because that won't work either", uri);
                setImage(uri);
            } else {
                setImage(new ImageLocation(uri));
            }
        }
        date = image.getDate();
        offset = image.getOffset();
        urn = image.getUrn();
        crids = image.getCrids();
        lastModified = image.getLastModifiedInstant();
        creationDate = image.getCreationInstant();
    }

    public Image toImage() {
        return toImage(OwnerType.BROADCASTER);
    }

    public Image toImage(OwnerType owner) {
        Image result = new Image(owner);
        result.setCreationInstant(null); // not supported by update format. will be set by persistence layer
        result.copyFrom(this);
        result.setHighlighted(highlighted);
        result.setDate(date);
        result.setOffset(offset);
        result.setUrn(urn);
        if (wrapper != null && wrapper.urn != null) {
            result.setImageUri(wrapper.urn);
        } else if (wrapper != null && wrapper.location != null) {
            //result.setImageUri(((ImageLocation) image).getUrl());
        }
        result.setCrids(crids);
        Embargos.copy(this, result);
        if (forDeletion()) {
            PublishableObjectAccess.setWorkflow(result, Workflow.FOR_DELETION);
        }
        return result;
    }

    /**
     *
     * @param metadata Incoming metadata from the image server
     */
    public Image toImage(BackendImageMetadata<?> metadata) {
        Image result = toImage();
        result.setImageUri(metadata.getImageUri());
        result.copyFromIfSourceSet(metadata);
        result.copyFromIfSourceSet(this);
        return result;
    }

    public Long getId() {
        return Image.idFromUrn(getUrn());
    }

    public void setId(Long id) {
        urn = id == null ? null : Image.BASE_URN + id;
    }

    @NonNull
    @Override
    public ImageUpdate setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
    }

    @NonNull
    @Override
    public ImageUpdate setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;
    }
    /**
     * Sets the image as an {@link ImageData} object. I.e. the actual blob
     */
    public void setImage(ImageData image) {
        this.wrapper = image == null ? null : ImageWrapper.withData(image);
    }

    /**
     * Sets the image as an {@link ImageLocation} object. I.e. a reference to some remote url.
     */
    public void setImage(ImageLocation image) {
        this.wrapper = image == null ? null : ImageWrapper.withLocation(image);
    }

    /**
     * Sets the image as an urn, i.e. a reference to the image database
     */
    public void setImage(String urn) {
        this.wrapper = urn == null ? null : ImageWrapper.withUrn(urn);
    }

    public Object getImage() {
        if (wrapper == null) {
            return null;
        }
        return wrapper.asXmlValue();
    }

    @JsonProperty("image")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object getJsonImage() {
        if (wrapper == null) {
            return null;
        }
        if (wrapper.getUrn() != null && wrapper.getData() == null && wrapper.getLocation() == null) {
            return wrapper.getUrn();
        }
        if (wrapper.getLocation() != null) {
            return Collections.singletonMap("imageLocation", wrapper.getLocation());
        }
        if (wrapper.getData() != null) {
            return Collections.singletonMap("imageData", wrapper.getData());
        }
        if (wrapper.getUrn() != null) {
            return Collections.singletonMap("urn", wrapper.getUrn());
        }
        return null;
    }

    @JsonProperty("image")
    void setJsonImage(JsonNode image) {
        if (image == null || image.isNull()) {
            this.wrapper = null;
            return;
        }
        if (image.isTextual()) {
            setImage(image.textValue());
            return;
        }
        if (image.has("imageLocation")) {
            setImage(unwrapper.convertValue(image.get("imageLocation"), ImageLocation.class));
            return;
        }
        if (image.has("imageData")) {
            setImage(unwrapper.convertValue(image.get("imageData"), ImageData.class));
            return;
        }
        if (image.has("urn")) {
            setImage(unwrapper.convertValue(image.get("urn"), String.class));
            return;
        }
        throw new IllegalArgumentException("Expected imageLocation, imageData, urn or string value");
    }


    @XmlElements(value = {
        @XmlElement(name = "imageData", type = ImageData.class),
        @XmlElement(name = "imageLocation", type = ImageLocation.class),
        @XmlElement(name = "urn", type = String.class)
    })
    @JsonIgnore
    protected Object getXmlImage() {
        return wrapper == null ? null : wrapper.asXmlValue();
    }

    @JsonIgnore
    protected void setXmlImage(Object xmlImage) {
        wrapper = ImageWrapper.fromXmlValue(xmlImage);
    }

    @Override
    public boolean forDeletion() {
        return delete != null && delete;
    }

    @Override
    public String toString() {
        return "ImageUpdate{" +
            "image=" + wrapper +
            ", type=" + type +
            ", title=" + title +
            '}';
    }

    public Set<ConstraintViolation<ImageUpdate>> violations(Class<?>... groups) {
        return Validation.validate(this, groups);
    }

    @AssertTrue
    protected boolean imageDataValid() {
        return wrapper != null && wrapper.isValid();
    }

}
