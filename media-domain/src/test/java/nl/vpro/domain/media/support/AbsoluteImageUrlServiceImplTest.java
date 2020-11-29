package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class AbsoluteImageUrlServiceImplTest {

    public ImageUrlService test = new AbsoluteImageUrlServiceImpl("https://images-test.poms.omroep.nl/image/");

    @Test
    public void test() {
        log.info("{}", test.getOriginalUrl(123L));


        assertThat(test.getOriginalUrl(123L)).isEqualTo("https://images-test.poms.omroep.nl/image/123");
    }

}
