package nl.vpro.domain.convert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.image.Dimension;
import nl.vpro.domain.image.ImageSource;

import static nl.vpro.domain.image.Dimension.of;
import static org.assertj.core.api.Assertions.assertThat;


class ConversionsTest {


    public static Stream<Arguments> tests() {
        return Stream.of(
            Arguments.of(of(2048, 1360),of(150, 100), "s150" ),
            Arguments.of(of(2048, 1360),of(2048, 1360), "s3000>" )
        );
    }

    @ParameterizedTest
    @MethodSource("tests")
    void predictDimensions(Dimension in, Dimension out, String conv) {
        String[] conversions = conv.split("/");
        assertThat(Conversions.predictDimensions(in, conversions)).isEqualTo(out);
    }

    @Test
    void yaml() {
        Pattern pa = Pattern.compile(".*?(\\d+).*");
        for (Map.Entry<ImageSource.Key, String[]> entry : Conversions.MAPPING.entrySet()) {
            System.out.printf("'%s':%n", entry.getKey().getShortName());
            Matcher matcher = pa.matcher(entry.getValue()[0]);
            matcher.matches();
            int width = Integer.parseInt(matcher.group(1));
            if (entry.getKey().getFormat() == null) {
                System.out.printf("  width: %d%n", width);
                System.out.printf("  class: info.magnolia.templating.imaging.variation.SimpleResizeVariation%n");
            } else {
                System.out.printf("  width: %d%n", width);
                System.out.printf("  class: nl.vpro.magnolia.module.images.generation.ResizeVariationWithOutputFormat%n");
                System.out.printf("  outputFormat:%n");
                System.out.printf("     formatName: webp%n");
                System.out.printf("     progressive: false%n");
            }
        }
    }
}
