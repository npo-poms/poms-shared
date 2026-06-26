package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @since 8.14
 * @author Michiel Meeuwissen
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ImageWrapper {

    @Valid
    ImageData imageData;
    @Valid
    ImageLocation imageLocation;

    String urn;

    static ImageWrapper withImageData(ImageData imageData) {
        ImageWrapper result = new ImageWrapper();
        result.imageData = imageData;
        return result;
    }

    static ImageWrapper withImageLocation(ImageLocation imageLocation) {
        ImageWrapper result = new ImageWrapper();
        result.imageLocation = imageLocation;
        return result;
    }

    static ImageWrapper withUrn(String urn) {
        ImageWrapper result = new ImageWrapper();
        result.urn = urn;
        return result;
    }

    static ImageWrapper of(ImageLocation imageLocation, ImageData imageData, String urn) {
        ImageWrapper result = new ImageWrapper();
        result.imageLocation = imageLocation;
        result.imageData = imageData;
        result.urn = urn;
        return result;
    }

    static ImageWrapper fromXmlValue(Object xmlImage) {
        if (xmlImage == null) {
            return null;
        }
        if (xmlImage instanceof ImageLocation imageLocation) {
            return withImageLocation(imageLocation);
        }
        if (xmlImage instanceof ImageData imageData) {
            return withImageData(imageData);
        }
        if (xmlImage instanceof String urn) {
            return withUrn(urn);
        }
        throw new IllegalArgumentException("Invalid image value " + xmlImage);
    }

    Object asXmlValue() {
        if (imageLocation != null) {
            return imageLocation;
        }
        if (imageData != null) {
            return imageData;
        }
        return urn;
    }

    @AssertTrue
    protected boolean isValid() {
        int count = 0;
        if (imageLocation != null) {
            count++;
        }
        if (imageData != null) {
            count++;
        }
        if (urn != null) {
            count++;
        }
        return count == 1;
    }
}
