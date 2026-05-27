package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ScheduleEventIdentifierTest {

    @Test
    void parse() {
        ScheduleEventIdentifier ev = ScheduleEventIdentifier.parse("NED3:2026-04-16T05:38:00Z");
        assertThat(ev.getChannel()).isEqualTo(Channel.NED3);

    }

    @Test
    void parseOnDemand() {
        assertThatThrownBy(() -> ScheduleEventIdentifier.parse("NVOD:2026-04-16T05:38:00Z"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("NVOD events should have a mid");

        ScheduleEventIdentifier ev = ScheduleEventIdentifier.parse("NVOD:2026-04-16T05:38:00Z\tMID_123");
        assertThat(ev.getChannel()).isEqualTo(Channel.NVOD);

        assertThat(ev.onDemandMid).isEqualTo("MID_123");

    }
}
