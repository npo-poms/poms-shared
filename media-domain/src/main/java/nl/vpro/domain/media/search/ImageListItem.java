package nl.vpro.domain.media.search;

import lombok.Data;
import lombok.EqualsAndHashCode;

import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.support.License;

/**
 * @since 3.5
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageListItem extends PublishableListItem<ImageListItem> {
    private String title;

    private String description;

    private String imageUri;

    private License license;

    private String sourceName;

    private OwnerType owner;

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
        this.owner = image.getOwner();
    }

}
