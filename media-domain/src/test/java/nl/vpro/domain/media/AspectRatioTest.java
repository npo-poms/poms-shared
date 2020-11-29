package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
public class AspectRatioTest {
    @Test
    public void fromDimension() {
        assertEquals(AspectRatio._16x9, AspectRatio.fromDimension(16, 9));
        assertEquals(AspectRatio._16x9, AspectRatio.fromDimension(3 * 16, 3 * 9));
        assertEquals(AspectRatio._16x9, AspectRatio.fromDimension(3 * 13 * 16, 3 * 13 * 9));
        assertEquals(AspectRatio._16x9, AspectRatio.fromDimension(12345 * 16, 12345 * 9));
        assertEquals(AspectRatio._xCIF, AspectRatio.fromDimension(352, 288));
        assertNull(AspectRatio.fromDimension(0, 0)); // MSE-1949

    }

    @Test
    public void testToString() {
        assertEquals("4:3", AspectRatio._4x3.toString());
    }


    @Test
    public void fromString() {
        assertNull(AspectRatio.fromString(""));
        assertEquals(AspectRatio._4x3, AspectRatio.fromString("4:3"));
        assertEquals(AspectRatio._4x3, AspectRatio.fromString("4x3"));
        assertEquals(AspectRatio._4x3, AspectRatio.fromString("20 x 15"));
        assertEquals(AspectRatio._4x3, AspectRatio.fromString("4 3"));

    }

    @Test
    public void testXmlValue() {
        assertThat(AspectRatio._4x3.getXmlValue()).isEqualTo("4:3");
    }

}
