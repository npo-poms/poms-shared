package nl.vpro.domain.media;

import java.util.HashSet;
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

        Set<String> nullSafeSetWithNull = CollectionUtils.nullSafeSet(set, true);

        assertThat(nullSafeSetWithNull.contains(null)).isTrue();
        assertThat(nullSafeSetWithNull.contains("a")).isTrue();
        assertThat(nullSafeSetWithNull.contains("c")).isFalse();
        assertThat(nullSafeSetWithNull.contains(2)).isFalse();

    }

    @Test
    void nullSafeSetWithHashSet() {

        Set<String> set = new HashSet<>(Set.of("a", "b"));
        set.add(null);

        Set<String> nullSafeSet = CollectionUtils.nullSafeSet(set);

        assertThat(nullSafeSet.contains(null)).isTrue();


    }
}
