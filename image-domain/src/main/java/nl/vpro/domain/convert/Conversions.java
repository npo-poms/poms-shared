package nl.vpro.domain.convert;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.configuration.FixedSizeMap;

import com.google.common.annotations.Beta;

import nl.vpro.domain.image.Dimension;
import nl.vpro.domain.image.ImageSource;

/**
 * Utilities related to image conversion.
 * @since 7.2
 */
@Beta
@Slf4j
public class Conversions {


    public static final Map<ImageSource.Key, String[]> MAPPING;
    static {
        Map<ImageSource.Key, String[]> mapping = new LinkedHashMap<>();
        try (InputStream resourceAsStream = Conversions.class.getResourceAsStream("/image-conversions.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            properties.forEach((k, v) -> {
                try {
                    mapping.put(
                        new ImageSource.Key(k.toString()),
                        v.toString().split("/")
                    );
                } catch (Exception e) {
                    log.debug("Ignored {}={}", k, v);
                }
            });

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }

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
                switch(result.test()) {
                    case NO_MATCH:
                        continue;
                    case MATCH:
                        out = profile.convertedDimension(result.object(), in);
                        continue CONVERSION;
                    case MATCH_AND_STOP:
                        out = profile.convertedDimension(result.object(), in);
                        break CONVERSION;
                }
            }
            // just skip, suppose this is an unrecognized conversion, that just
            // doesn't change the dimensions of the image
        }
        return out;

    }
}
