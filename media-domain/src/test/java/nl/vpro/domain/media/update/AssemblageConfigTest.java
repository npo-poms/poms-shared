package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import nl.vpro.util.Version;

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
    public void backwards() {
        AssemblageConfig build = AssemblageConfig.builder()
            .build();

        assertThat(build.isCopyPredictions()).isFalse();

        build.backwardsCompatible(Version.of(6, 0));
        assertThat(build.isCopyPredictions()).isTrue();

    }
    @Test
    public void backwards2() {
        AssemblageConfig build = AssemblageConfig.builder()
            .copyPredictions(false)
            .build();
        assertThat(build.isCopyPredictions()).isFalse();

        build.backwardsCompatible(Version.of(4, 0));
        assertThat(build.isCopyPredictions()).isFalse();
    }

}
