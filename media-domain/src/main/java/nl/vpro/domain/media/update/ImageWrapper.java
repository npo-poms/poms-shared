package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.validation.URI;

/**
 * @since 8.14
 * @author Michiel Meeuwissen
 */
@Getter
@Setter
public class ImageWrapper {

    @Valid
    ImageData data;
    @Valid
    ImageLocation location;

    @URI(schemes = "urn", patterns = {"urn:vpro:image:\\d+"})
    String urn;

    static ImageWrapper withData(ImageData imageData) {
        ImageWrapper result = new ImageWrapper();
        result.data = imageData;
        return result;
    }

    static ImageWrapper withLocation(ImageLocation imageLocation) {
        ImageWrapper result = new ImageWrapper();
        result.location = imageLocation;
        return result;
    }

    static ImageWrapper withUrn(String urn) {
        ImageWrapper result = new ImageWrapper();
        result.urn = urn;
        return result;
    }

    static ImageWrapper of(ImageLocation imageLocation, ImageData imageData, String urn) {
        ImageWrapper result = new ImageWrapper();
        result.location = imageLocation;
        result.data = imageData;
        result.urn = urn;
        return result;
    }

    static ImageWrapper fromXmlValue(Object xmlImage) {
        if (xmlImage == null) {
            return null;
        }
        if (xmlImage instanceof ImageLocation imageLocation) {
            return withLocation(imageLocation);
        }
        if (xmlImage instanceof ImageData imageData) {
            return withData(imageData);
        }
        if (xmlImage instanceof String urn) {
            return withUrn(urn);
        }
        throw new IllegalArgumentException("Invalid image value " + xmlImage);
    }

    Object asXmlValue() {
        if (location != null) {
            return location;
        }
        if (data != null) {
            return data;
        }
        return urn;
    }

    @AssertTrue
    protected boolean isValid() {
        int count = 0;
        if (location != null) {
            count++;
        }
        if (data != null) {
            count++;
        }
        if (urn != null) {
            count++;
        }
        return count == 1;
    }
}
