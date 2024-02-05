package nl.vpro.domain.page;

import lombok.*;

import java.time.Instant;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.image.Metadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.support.License;
import nl.vpro.validation.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "imageType")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.Builder
public class Image implements Metadata {

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
    @NotNull(groups = {WeakWarningValidatorGroup.class})
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

    @Override
    public Instant getLastModifiedInstant() {
        return getCreationInstant();
    }

    @Override
    public Instant getCreationInstant() {
        return null;
    }

    @Override
    public Integer getHeight() {
        return null;
    }

    @Override
    public Integer getWidth() {
        return null;
    }
}
