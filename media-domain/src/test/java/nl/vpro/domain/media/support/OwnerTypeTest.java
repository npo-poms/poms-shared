package nl.vpro.domain.media.support;

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

}
