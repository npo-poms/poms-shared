package nl.vpro.domain.convert;

import java.util.Arrays;
import java.util.List;

import nl.vpro.domain.image.Dimension;

public class Conversions {

    private final static List<Profile<?>> conversions;
    static {
        conversions = Arrays.asList(
            new ResizeProfile() {
                @Override
                public int getMaxSize() {
                    return Integer.MAX_VALUE;
                }
            },
            new CropProfile() {
                @Override
                public int getMaxSize() {
                    return Integer.MAX_VALUE;
                }
            },
            new PromoLandscapeProfile() {
                @Override
                public Geometry getGeometry() {
                    return DEFAULT_GEOMETRY;
                }
            },
            new PromoPortraitProfile() {
                @Override
                public Geometry getGeometry() {
                    return DEFAULT_GEOMETRY;
                }
            }
        );
    }

    public static Dimension predictDimensions(Dimension in, String... conversion) {
        Dimension out = in;
        CONVERSION:
        for (String con : conversion) {
            for (Profile<?> p : conversions) {
                TestResult<?> result = p.dynamicTest(con);
                if (result.test()) {
                    out = p.convertedDimension(result.object(), in);
                    continue CONVERSION;
                }
            }
            throw new IllegalArgumentException();
        }
        return out;

    }
}
