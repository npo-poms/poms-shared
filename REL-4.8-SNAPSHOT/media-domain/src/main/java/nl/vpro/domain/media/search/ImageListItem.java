package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.domain.media.support.Image;

/**
 * @since 3.5
 */
public class ImageListItem extends PublishableListItem{
    private String title;

    private String description;

    private String imageUri;

    public ImageListItem() {
    }

    public ImageListItem(Image image) {
        super(image);

        setUrn(image.getUrn());

        this.title = image.getTitle();
        this.description = image.getDescription();
        this.imageUri = image.getImageUri();

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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
