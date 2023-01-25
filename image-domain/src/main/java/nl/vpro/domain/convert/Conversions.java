package nl.vpro.domain.convert;

import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.configuration.FixedSizeMap;

import com.google.common.annotations.Beta;

import nl.vpro.domain.image.Dimension;
import nl.vpro.domain.image.ImageSource;

import static nl.vpro.domain.image.ImageSource.Key.jpeg;
import static nl.vpro.domain.image.ImageSource.Key.webp;
import static nl.vpro.domain.image.ImageSource.Type.*;
import static nl.vpro.domain.image.ImageSource.Type.LARGE;

/**
 * Utilities related to image conversion.
 * @since 7.2
 */
@Beta
public class Conversions {


    public static final Map<ImageSource.Key, String[]> MAPPING;
    static {
        Map<ImageSource.Key, String[]> mapping = new HashMap<>();
        mapping.put(webp(THUMBNAIL), new String[] {"s100"});
        mapping.put(webp(MOBILE), new String[] {"s414"});
        mapping.put(webp(TABLET), new String[] {"s1024>"});
        mapping.put(webp(LARGE),  new String[] {"s2048>"});

        mapping.put(jpeg(LARGE),  new String[] {"s414"});

        MAPPING = new FixedSizeMap<>(mapping);
    }


    private final static List<Profile<?>> conversionProfiles;
    static {
        conversionProfiles = Arrays.asList(
            new ResizeProfile() {},
            new CropProfile() {},
            new PromoLandscapeProfile() {},
            new PromoPortraitProfile() {}
        );
    }

    /**
     * Given a list of conversion (as they can appear on url), and the {@link Dimension} of the original image, try to figure out what the dimensions will be.
     * <p>
     * For now, this is only partially implemented, basically just good enough to support the conversion in {@link #MAPPING}.
     */
    public static Dimension predictDimensions(@NonNull Dimension in, String... conversions) {
        Dimension out = in;
        CONVERSION:
        for (String con : conversions) {
            for (Profile<?> profile : conversionProfiles) {
                TestResult<?> result = profile.dynamicTest(con);
                if (result.test()) {
                    out = profile.convertedDimension(result.object(), in);
                    continue CONVERSION;
                }
            }
            // just skip, suppose this is an unrecognized conversion, that just
            // doesn't change the dimensions of the image
        }
        return out;

    }
}
