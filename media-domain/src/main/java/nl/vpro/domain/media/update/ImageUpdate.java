/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.License;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.ReleaseDate;
import nl.vpro.validation.WarningValidatorGroup;
import nl.vpro.xml.bind.DateToDuration;

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
public class ImageUpdate {

    private static final Logger LOG = LoggerFactory.getLogger(ImageUpdate.class);


    @XmlAttribute(required = true)
    @NotNull
    private ImageType type;

    @XmlAttribute
    private String urn;



    @XmlAttribute
    private Date publishStart;

    @XmlAttribute
    private Date publishStop;

    @XmlAttribute(required = true)
    Boolean highlighted = false;

    @XmlElement(required = true)
    @NotNull(message = "provide title for imageUpdate")
    @Size.List({@Size(max = 255), @Size(min = 1)})
    private String title;

    @XmlElement(required = true)
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

    @URL
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String source;

    @URL
    @XmlElement
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    private String sourceName;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @NotNull(groups = {WarningValidatorGroup.class})
    private License license;

    @ReleaseDate()
    @XmlElement
    private String date;

    @Temporal(TemporalType.TIME)
    @XmlJavaTypeAdapter(DateToDuration.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Date offset;

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

    public ImageUpdate(Image image) {
        type = image.getType();
        publishStart = image.getPublishStart();
        publishStop = image.getPublishStop();
        highlighted = image.isHighlighted();
        title = image.getTitle();
        description = image.getDescription();
        String imageUri = image.getImageUri();
        this.image = imageUri.startsWith("urn:") ?
            imageUri : new ImageLocation(image.getImageUri());
        width = image.getWidth();
        height = image.getHeight();
        credits = image.getCredits();
        source = image.getSource();
        date = image.getDate();
        offset = image.getOffset();
        license = image.getLicense();
        sourceName = image.getSource();
        urn = image.getUrn();
    }

    public Image toImage(String imageUri) {
        Image result = new Image(OwnerType.BROADCASTER, imageUri);
        result.setType(type);
        result.setTitle(title);
        result.setDescription(description);
        result.setPublishStart(publishStart);
        result.setPublishStop(publishStop);
        result.setHighlighted(highlighted);
        result.setWidth(width);
        result.setHeight(height);
        result.setCredits(credits);
        result.setSource(source);
        result.setSourceName(sourceName);
        result.setLicense(license);
        result.setDate(date);
        result.setOffset(offset);
        result.setUrn(urn);
        return result;
    }

    public Image toImage(ImageMetadata metadata) {
        Image result = toImage(metadata.getUrn());
        if (metadata.getImageType() != null && type == null) {
            result.setType(metadata.getImageType());
        }
        if (StringUtils.isNotEmpty(metadata.getTitle()) && title == null) {
            result.setTitle(metadata.getTitle());
        }
        if (StringUtils.isNotEmpty(metadata.getDescription()) && description == null) {
            result.setDescription(metadata.getDescription());
        }
        if (metadata.getWidth() != null) {
            if (width != null && !metadata.getWidth().equals(width)) {
                LOG.warn("Width was set {} but it is actually {}, so ignoring", width, metadata.getWidth());
            }
            result.setWidth(metadata.getWidth());
        }
        if (metadata.getHeight() != null) {
            if (height != null && !metadata.getHeight().equals(height)) {
                LOG.warn("Height was set {} but it is actually {}, so ignoring", height, metadata.getHeight());

            }
            result.setHeight(metadata.getHeight());
        }
        return result;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }


    public String getUrn() {
        return urn;
    }

    public void setUrn(String s) {
        this.urn = s;
    }

    public Long getId() {
        return Image.idFromUrn(urn);
    }


    public void setId(Long id) {
        urn = id == null ? null : Image.BASE_URN + id;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }


    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getOffset() {
        return offset;
    }

    public void setOffset(Date offset) {
        this.offset = offset;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(ImageData image) {
        this.image = image;
    }

    public void setImage(ImageLocation image) {
        this.image = image;
    }

    public void setImage(Image image) {
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
            LOG.warn("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }
}
