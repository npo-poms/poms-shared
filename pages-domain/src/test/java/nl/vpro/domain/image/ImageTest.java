package nl.vpro.domain.image;

import nl.vpro.domain.image.backend.BackendImage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {


    @Test
    public void setTitle() {
        BackendImage image = new BackendImage();
        image.setTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        assertThat(image.getTitle()).hasSize(255);

        assertThat(image.getTitle()).startsWith("Lorem ipsum");

    }

}
