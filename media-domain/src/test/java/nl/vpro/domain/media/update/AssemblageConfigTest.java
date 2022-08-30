package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
class AssemblageConfigTest {

    @Test
    public void equalsAndHashCode() {
        AssemblageConfig.Builder builder = AssemblageConfig.withAllTrue();
        AssemblageConfig a = builder.build();
        AssemblageConfig b = a.copy();
        assertThat(a).isEqualTo(b);
    }


    @Test
    public void fallback() {
        AssemblageConfig build = AssemblageConfig.builder().build();
        assertThat(build.isCopyPredictions()).isFalse();


    }

}
