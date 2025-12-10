package nl.vpro.domain.media;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TargetGroupTypeTest {

    @Test
    void getAgeRatings() {
        assertThat(Arrays.stream(TargetGroupType.values())
            .map(t -> t.name() + ":" + t.getAgeRatings())
            .toList()
        ).containsExactly(
            "KIDS_6:[ALL]",
            "KIDS_9:[ALL, _6]",
            "KIDS_12:[ALL, _6, _9]",
            "KIDS_14:[ALL, _6, _9, _12]",
            "KIDS_16:[ALL, _6, _9, _12, _14]",
            "YOUNG_ADULTS:[ALL, _6, _9, _12, _14, _16]",
            "ADULTS:[ALL, _6, _9, _12, _14, _16]",
            "ADULTS_WITH_KIDS_6:[ALL, _6, _9, _12, _14, _16]",
            "ADULTS_WITH_KIDS_12:[ALL, _6, _9, _12, _14, _16]",
            "EVERYONE:[ALL, _6, _9, _12, _14, _16]"
        );

    }
}
