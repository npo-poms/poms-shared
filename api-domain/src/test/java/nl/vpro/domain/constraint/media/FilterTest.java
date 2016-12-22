package nl.vpro.domain.constraint.media;

import nl.vpro.domain.constraint.PredicateTestResult;
import nl.vpro.domain.media.MediaTestDataBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class FilterTest {

    @Before
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testApply() {
        Filter filter = new Filter();
        filter.setConstraint(MediaConstraints.alwaysFalse());

        PredicateTestResult r = filter.testWithReason(MediaTestDataBuilder.broadcast().build());

        assertThat(r.applies()).isFalse();
        assertThat(r.getDescription(Locale.US)).isEqualTo("Never matches");
        assertThat(r.getReason()).isEqualTo("AlwaysFalse");
    }
}
