package nl.vpro.domain.page;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.image.support.License;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;
import nl.vpro.validation.WarningValidatorGroup;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "imageType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Image {

    @XmlAttribute
    private ImageType type;

    @XmlAttribute
    @NotNull
    @URI
    private String url;

    @XmlElement
    @NoHtml
    @JsonProperty
    private String title;

    @XmlElement
    @NoHtml
    @JsonProperty
    private String description;

    @NoHtml
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String credits;

    @URI()
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
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {WarningValidatorGroup.class})
    private License license;


    public static Image from(nl.vpro.domain.media.support.Image image) {
        return new Image(image.getImageUri());
    }

    public Image() {
    }

    public Image(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public Image(String url) {
        this.url = url;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Image)) {
            return false;
        }

        Image image = (Image)o;

        return url.equals(image.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
