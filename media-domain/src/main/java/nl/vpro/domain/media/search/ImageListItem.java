package nl.vpro.domain.media.search;

import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.License;

/**
 * @since 3.5
 */
public class ImageListItem extends PublishableListItem {
    private String title;

    private String description;

    private String imageUri;

    private License license;

    private String sourceName;

    public ImageListItem() {
    }

    public ImageListItem(Image image) {
        super(image);
        setUrn(image.getUrn());
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.imageUri = image.getImageUri();
        this.license = image.getLicense();
        this.sourceName = image.getSourceName();


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

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
