package nl.vpro.domain.media;

import net.jqwik.api.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ScheduleEventIdentifierTest implements ComparableTheory<ScheduleEventIdentifier> {

    @Override
    public Arbitrary<ScheduleEventIdentifier> datapoints() {
        return Arbitraries.of(
            new ScheduleEventIdentifier(Channel.NED3, Instant.parse("2026-04-16T05:38:00Z"), null),
            new ScheduleEventIdentifier(Channel.NED3, Instant.parse("2026-04-18T05:38:00Z"), null),

            new ScheduleEventIdentifier(Channel.NVOD, Instant.parse("2026-04-16T05:38:00Z"), "MID_123")
        );
    }

    @Property
    public void asString(@ForAll(ComparableTheory.DATAPOINTS) ScheduleEventIdentifier eve) {
        assertThat(ScheduleEventIdentifier.parse(eve.asString())).isEqualTo(eve);
    }


    @Test
    void parse() {
        ScheduleEventIdentifier ev = ScheduleEventIdentifier.parse("NED3:2026-04-16T05:38:00Z");
        assertThat(ev.getChannel()).isEqualTo(Channel.NED3);

    }

    @Test
    void parseOnDemand() {
        assertThatThrownBy(() -> ScheduleEventIdentifier.parse("NVOD:2026-04-16T05:38:00Z"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("On demand events should have a mid");

        ScheduleEventIdentifier ev = ScheduleEventIdentifier.parse("NVOD:2026-04-16T05:38:00Z\tMID_123");
        assertThat(ev.getChannel()).isEqualTo(Channel.NVOD);

        assertThat(ev.onDemandMid).isEqualTo("MID_123");

    }
}
