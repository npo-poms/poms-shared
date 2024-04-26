package nl.vpro.domain.media.support;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.2
 */
public class OwnerTypeTest {
    @Test
    public void comparator() {
        Comparator<OwnerType> ot = OwnerType.comparator(OwnerType.NPO);
        assertThat(ot.compare(OwnerType.NPO, OwnerType.BROADCASTER)).isNegative();
    }

    @Test
    public void defaultorder() {
        Comparator<OwnerType> ot = OwnerType.comparator();
        assertThat(ot.compare(OwnerType.BROADCASTER, OwnerType.NPO)).isNegative();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void deprecated() {
        assertThat(OwnerType.IMMIX.isDeprecated()).isTrue();
    }

    @Test
    public void after() {
        assertThat(OwnerType.after((OwnerType.BROADCASTER))).isEqualTo(OwnerType.values());
        assertThat(OwnerType.after((OwnerType.NEBO))).isEqualTo(Arrays.asList(OwnerType.values()).subList(1, OwnerType.values().length).toArray());

    }

}
