package nl.vpro.domain.media;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class LiveStreamTest {
    @Test
    public void values() throws Exception {
        assertThat(LiveStream.values().length).isGreaterThanOrEqualTo(3);
    }

}
