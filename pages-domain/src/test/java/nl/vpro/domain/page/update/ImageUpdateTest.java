package nl.vpro.domain.page.update;

import org.junit.jupiter.api.Test;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class ImageUpdateTest {


    @Test
    public void builder() {
        ImageUpdate.builder().credits("bla").build();
    }
}
