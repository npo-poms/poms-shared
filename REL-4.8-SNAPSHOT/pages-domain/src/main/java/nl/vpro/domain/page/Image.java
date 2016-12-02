package nl.vpro.domain.page;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.image.ImageType;
import nl.vpro.validation.NoHtml;

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
    @URL
    private String url;

    @XmlElement
    @NoHtml
    @JsonProperty
    private String title;

    @XmlElement
    @NoHtml
    @JsonProperty
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Image)) {
            return false;
        }

        Image image = (Image)o;

        if(!url.equals(image.url)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
