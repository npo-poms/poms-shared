package nl.vpro.domain.constraint;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3.1
 */
public class DateConstraintTest {

    DateConstraint constraint = new DateConstraint() {
        @Override
        public String getESPath() {
            return null;

        }
    };
    Instant MILLENIUM_START = LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();

    @Test
    public void parser() {

        constraint.setDate(" 2000-01-01 midnight +1");
        assertThat(constraint.getDateAsInstant()).isEqualTo(MILLENIUM_START);
    }

    @Test
    public void applyDateOperators() {

        constraint.setDate("2000-01-01 midnight +1");
        constraint.setOperator(Operator.GT);

        assertThat(constraint.applyDate(MILLENIUM_START)).isFalse();
        assertThat(constraint.applyDate(MILLENIUM_START.minusMillis(1))).isFalse();
        assertThat(constraint.applyDate(MILLENIUM_START.plusMillis(1))).isTrue();


        constraint.setOperator(Operator.GTE);

        assertThat(constraint.applyDate(MILLENIUM_START)).isTrue();
        assertThat(constraint.applyDate(MILLENIUM_START.minusMillis(1))).isFalse();
        assertThat(constraint.applyDate(MILLENIUM_START.plusMillis(1))).isTrue();


        constraint.setOperator(Operator.LT);

        assertThat(constraint.applyDate(MILLENIUM_START)).isFalse();
        assertThat(constraint.applyDate(MILLENIUM_START.minusMillis(1))).isTrue();
        assertThat(constraint.applyDate(MILLENIUM_START.plusMillis(1))).isFalse();

        constraint.setOperator(Operator.LTE);

        assertThat(constraint.applyDate(MILLENIUM_START)).isTrue();
        assertThat(constraint.applyDate(MILLENIUM_START.minusMillis(1))).isTrue();
        assertThat(constraint.applyDate(MILLENIUM_START.plusMillis(1))).isFalse();

    }

}
