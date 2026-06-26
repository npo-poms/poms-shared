/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.page.Image;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.validation.*;


@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageUpdateType", propOrder = {
    "title",
    "description",
    "source",
    "sourceName",
    "license",
    "credits",
    "imageLocation"
})
@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor
@Data
public class ImageUpdate implements Serializable {

    private static final Jackson2Mapper LENIENT = Jackson2Mapper.getLenientInstance();

    @Serial
    private static final long serialVersionUID = -8902321923389277697L;

    public static ImageUpdate of(Image image) {
        if (image == null) {
            return null;
        }
        return ImageUpdate.builder()
            .title(image.getTitle())
            .description(image.getDescription())
            .license(image.getLicense())
            .credits(image.getCredits())
            .source(image.getSource())
            .sourceName(image.getSourceName())
            .type(image.getType())
            .imageUrl(image.getUrl())
            .build();
    }


    @XmlAttribute(required = true)
    @NotNull
    private ImageType type;

    @XmlElement
    @Size.List({ @Size(max = 512, message = "image title contains too many (> {max}) characters"),
            @Size(min = 1, message = "image title contains no characters"), })
    @NoHtml
    private String title;

    @XmlElement
    @NoHtml(aggressive = false)
    private String description;


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
    private License license;


    @NotNull
    @Valid
    @JsonIgnore
    private ImageLocation imageLocation;

    public ImageUpdate() {
    }

/*
    public ImageUpdate(ImageType type, String title, String description, ImageData image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }
*/

    public ImageUpdate(@NonNull ImageType type, String title, String description, @NonNull ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.imageLocation = image;
    }

    public ImageUpdate(Image image) {
        type = image.getType();
        title = image.getTitle();
        description = image.getDescription();
        this.imageLocation = new ImageLocation(image.getUrl());
    }

    public Image toImage() {
        Image result = new Image();
        result.setType(type);
        result.setTitle(title);
        result.setDescription(description);
        result.setSource(source);
        result.setSourceName(sourceName);
        result.setCredits(credits);
        result.setLicense(license);
        result.setUrl(imageLocation.getUrl());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ImageUpdate)) {
            return false;
        }

        ImageUpdate that = (ImageUpdate)o;

        return Objects.equals(imageLocation, that.imageLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imageLocation);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Object getImage() {
        return getImageLocation();
    }

    @JsonProperty("image")
    Map<String, ImageLocation> getImageJson() {
        return getImageLocation() == null ? null : Collections.singletonMap("imageLocation", getImageLocation());
    }

    @JsonProperty("image")
    void setImageJson(JsonNode image) {
        if (image == null || image.isNull()) {
            this.imageLocation = null;
            return;
        }
        if (image.has("imageLocation")) {
            this.imageLocation = LENIENT.convertValue(image.get("imageLocation"), ImageLocation.class);
            return;
        }
        if (image.isObject()) {
            this.imageLocation = LENIENT.convertValue(image, ImageLocation.class);
            return;
        }
        throw new IllegalArgumentException("Expected imageLocation object");
    }


    public static class Builder {

        public Builder imageUrl(String imageLocation) {
            return imageLocation(new ImageLocation(imageLocation));
        }

        public Builder image(ImageLocation imageLocation) {
            return imageLocation(imageLocation);
        }


    }
}
