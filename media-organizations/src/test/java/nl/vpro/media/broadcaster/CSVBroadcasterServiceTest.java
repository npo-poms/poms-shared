package nl.vpro.media.broadcaster;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CSVBroadcasterServiceTest {
    private final BroadcasterService broadcasterService = new CSVBroadcasterService();


    @Test
    void forDomain() {
        assertThat(broadcasterService.findForDomain("vpro.nl")).contains(new Broadcaster("VPRO"));
    }

    @Test
    void get() {
        assertThat(broadcasterService.find("VPRO")).isEqualTo(new Broadcaster("VPRO"));
        assertThat(broadcasterService.find("VPRO").asRange()).isEqualTo(Range.atLeast(LocalDate.of(1926, 5, 29)));
    }

}
