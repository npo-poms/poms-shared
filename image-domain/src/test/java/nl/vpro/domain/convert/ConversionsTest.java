package nl.vpro.domain.convert;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.image.Dimension;

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
}
