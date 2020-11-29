package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class LiveStreamTest {
    @Test
    public void values() {
        assertThat(LiveStream.values().length).isGreaterThanOrEqualTo(3);
    }

}
