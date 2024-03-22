/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.page.Image;
import nl.vpro.domain.support.License;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;
import nl.vpro.validation.WarningValidatorGroup;


@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageUpdateType", propOrder = {
    "title",
    "description",
    "source",
    "sourceName",
    "license",
    "credits",
    "image",
})
@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor
@Data
public class ImageUpdate implements Serializable {

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


    @XmlElements(value = {
//        @XmlElement(name = "imageData", namespace = Xmlns.UPDATE_NAMESPACE, type = ImageData.class),
//        @XmlElement(name = "urn", namespace = Xmlns.UPDATE_NAMESPACE, type = String.class),
        @XmlElement(name = "imageLocation", type = ImageLocation.class)
    })
    @NotNull
    @Valid
    private Object image;

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

    public ImageUpdate(ImageType type, String title, String description, ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    public ImageUpdate(Image image) {
        type = image.getType();
        title = image.getTitle();
        description = image.getDescription();
        this.image = new ImageLocation(image.getUrl());
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
        if(image instanceof ImageLocation) {
            result.setUrl(((ImageLocation)image).getUrl());
        } else {
            throw new UnsupportedOperationException("We only support image locations for now");
        }

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

        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(image);
    }


    public static class Builder {

        public Builder imageUrl(String imageLocation) {
            return image(new ImageLocation(imageLocation));
        }

    }
}
