package nl.vpro.domain.media;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TargetGroupTypeTest {

    @Test
    void getAgeRatings() {
        assertThat(Arrays.stream(TargetGroupType.values())
            .map(t -> t.name() + ":" + t.getAgeRatings().stream().map(Enum::name).sorted().toList())
            .toList()
        ).containsExactlyInAnyOrder(
            "KIDS_6:[ALL]",
            "KIDS_9:[ALL, _6]",
            "KIDS_12:[ALL, _6, _9]",
            "KIDS_14:[ALL, _12, _6, _9]",
            "KIDS_16:[ALL, _12, _14, _6, _9]",
            "YOUNG_ADULTS:[ALL, _12, _14, _16, _6, _9]",
            "ADULTS:[ALL, _12, _14, _16, _6, _9]",
            "ADULTS_WITH_KIDS_6:[ALL, _12, _14, _16, _6, _9]",
            "ADULTS_WITH_KIDS_12:[ALL, _12, _14, _16, _6, _9]",
            "EVERYONE:[ALL]"
        );

    }
}
