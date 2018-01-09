/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.activation.DataHandler;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.EmbargoBuilder;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.image.BasicImageMetadata;
import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.image.Metadata;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.ReleaseDate;
import nl.vpro.validation.URI;
import nl.vpro.validation.WarningValidatorGroup;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static nl.vpro.domain.media.update.MediaUpdate.VALIDATOR;


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
    "image"
})

@Slf4j
@Data
public class ImageUpdate implements Embargo<ImageUpdate>, Metadata<ImageUpdate> {

    @XmlAttribute(required = true)
    @NotNull
    private ImageType type;

    @XmlAttribute(name = "urn")
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
    Boolean highlighted = false;

    @XmlElement(required = true)
    @NotNull(message = "provide title for imageUpdate")
    @Size.List({@Size(max = 255), @Size(min = 1)})
    private String title;

    @XmlElement(required = false)
    private String description;

    @XmlElement
    @Min(1)
    private Integer height;

    @XmlElement
    @Min(1)
    private Integer width;


    @NoHtml
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String credits;

    @URI
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String source;

    @XmlElement
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    private String sourceName;

    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    @Valid
    private License license;

    @ReleaseDate()
    @XmlElement
    private String date;

    @Temporal(TemporalType.TIME)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected java.time.Duration offset;

    @XmlElements(value = {
        @XmlElement(name = "imageData", type = ImageData.class),
        @XmlElement(name = "imageLocation", type = ImageLocation.class),
        @XmlElement(name = "urn", type = String.class)
    })
    @NotNull
    @Valid
    private Object image;

    public ImageUpdate() {
    }

    public ImageUpdate(ImageType type, String title, String description, ImageData image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    public ImageUpdate(ImageType type, String title, String description, ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    /**
     * This cannot be updated. Don't return it.
     * @return
     */
    @Override
    public String getImageUri() {
        return null;

    }

    public static class Builder implements EmbargoBuilder<Builder> {

        public Builder imageUrl(String imageLocation) {
            return imageLocation(new ImageLocation(imageLocation));
        }

        public Builder imageDataHandler(DataHandler dataHandler) {
            return imageData(new ImageData(dataHandler));
        }

    }

    @lombok.Builder(builderClassName = "Builder")
    public ImageUpdate(
        ImageType type,
        String title,
        String description,
        ImageLocation imageLocation,
        ImageData imageData,
        String imageUrn,
        License license,
        String source,
        String sourceName,
        String credits,
        Instant publishStart,
        Instant publishStop
        ) {
        this.description = description;
        this.title = title;
        this.type = type;
        Stream.of(imageLocation, imageData, imageUrn).filter(Objects::nonNull).forEach(o -> {
                if (this.image != null) {
                    throw new IllegalStateException("Can specify only on of imageLocation, imageData or imageUrn");
                }
                this.image = o;
            }
        );
        this.license = license;
        this.sourceName = sourceName;
        this.source = source;
        this.credits = credits;
        this.publishStartInstant = publishStart;
        this.publishStopInstant = publishStop;
    }


    public ImageUpdate(Image image) {
        copyFrom(image);
        highlighted = image.isHighlighted();
        String imageUri = image.getImageUri();
        this.image = imageUri != null && imageUri.startsWith("urn:") ?
            imageUri : new ImageLocation(image.getImageUri());
        date = image.getDate();
        offset = image.getOffset();
        urn = image.getUrn();
    }

    public Image toImage(String imageUri) {
        Image result = new Image(OwnerType.BROADCASTER, imageUri);
        result.copyFrom(this);
        result.setHighlighted(highlighted);
        result.setDate(date);
        result.setOffset(offset);
        result.setUrn(urn);
        Embargos.copy(this, result);
        return result;
    }

    /**
     *
     * @param metadata Incoming metadata from the image server
     */
    public Image toImage(ImageMetadata<?> metadata) {
        BasicImageMetadata basic = BasicImageMetadata.of(metadata);
        basic.copyFromIfSourceSet(this);
        Image result = toImage(metadata.getImageUri());
        return result;
    }



    public Long getId() {
        return Image.idFromUrn(getUrn());
    }


    public void setId(Long id) {
        urn = id == null ? null : Image.BASE_URN + id;
    }

    @Override
    public ImageUpdate setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
    }

    @Override
    public ImageUpdate setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }


    public void setImage(ImageData image) {
        this.image = image;
    }

    public void setImage(ImageLocation image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ImageUpdate{" +
            "image=" + image +
            ", type=" + type +
            ", title=" + title +
            '}';
    }

    public Set<ConstraintViolation<ImageUpdate>> violations(Class<?>... groups) {
        if (VALIDATOR != null) {
            return VALIDATOR.validate(this, groups);
        } else {
            log.warn("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }
}
