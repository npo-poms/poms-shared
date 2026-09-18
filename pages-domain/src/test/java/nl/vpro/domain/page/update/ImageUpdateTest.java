package nl.vpro.domain.page.update;

import org.junit.jupiter.api.Test;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
class ImageUpdateTest {


    @Test
    void builder() {
        ImageUpdate.builder().credits("bla").build();
    }
}
