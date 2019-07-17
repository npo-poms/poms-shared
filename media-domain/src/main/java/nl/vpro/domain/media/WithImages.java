package nl.vpro.domain.media;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.9
 */
public interface WithImages {

    List<Image> getImages();
    void setImages(List<Image> images);
    boolean hasImages();

    default Image getImage(Image image) {
        if (hasImages()) {
            for (Image existing : getImages()) {
                if (existing.equals(image)) {
                    return existing;
                }
            }
        }
        return null;
    }

    default Image getImage(int index) {
        if (! hasImages()) {
            return null;
        }

        return getImages().get(index);
    }

    default Image getImage(ImageType type) {
        if (hasImages()) {
            for (Image image : getImages()) {
                if (image.getType() == type) {
                    return image;
                }
            }
        }

        return null;
    }

    default Image getMainImage() {
        if (hasImages()) {
            return getImages().get(0);
        }
        return null;
    }

    default void addImage(@NonNull Image image) {
        addImage(image, hasImages() ? getImages().size() : 0);
    }
    void addImage(@NonNull Image image, int index);

    default List<Image> findImages(@NonNull OwnerType owner) {
        return getImages().stream().filter(i -> owner.equals(i.getOwner())).collect(Collectors.toList());
    }

    default Image findImage(@NonNull ImageType type) {
        if (hasImages()){
            for (Image image : getImages()) {
                if (type.equals(image.getType())) {
                    return image;
                }
            }
        }

        return null;
    }

    default Image findImage(ImageType type, Duration offset) {
        if (hasImages()) {
            for (Image image : getImages()) {
                if (type.equals(image.getType()) && offset.equals(image.getOffset())) {
                    return image;
                }
            }
        }
        return null;
    }


    default Image findImage(Long id) {
        if (hasImages() && id != null) {
            for (Image image : getImages()) {
                if (image != null) {
                    if (id.equals(image.getId())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    default Image findImage(String url, OwnerType owner) {
        if (hasImages()) {
            for (Image image : getImages()) {
                if (image != null) {
                    String uri = image.getImageUri();
                    if (uri != null && uri.equals(url) && owner == image.getOwner()) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    default boolean removeImage(Image image) {
        if (hasImages()) {
            image.setParent(null);
            return getImages().remove(image);
        }
        return false;
    }

    default boolean removeImage(Long imageId) {
        boolean success = false;
        if (imageId != null && hasImages()) {

            for (Image image : getImages()) {
                if (imageId.equals(image.getId())) {
                    success = removeImage(image);
                    break;
                }
            }
        }
        return success;
    }

}
