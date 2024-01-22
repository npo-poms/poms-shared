package nl.vpro.domain.media;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CollectionUtilsTest {

    @Test
    void nullSafeSet() {

        Set<String> set = Set.of("a", "b");

        assertThatThrownBy(() -> set.contains(null)).isInstanceOf(NullPointerException.class);

        Set<String> nullSafeSet = CollectionUtils.nullSafeSet(set);

        assertThat(nullSafeSet.contains(null)).isFalse();
        assertThat(nullSafeSet.contains("a")).isTrue();


    }
}
