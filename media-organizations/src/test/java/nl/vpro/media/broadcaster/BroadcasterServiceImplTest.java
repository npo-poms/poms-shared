package nl.vpro.media.broadcaster;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.user.BroadcasterService;

import static org.fest.assertions.Assertions.assertThat;

@Ignore("gvd")
public class BroadcasterServiceImplTest {


    BroadcasterService broadcasterService = new BroadcasterServiceImpl("classpath:/broadcasters.properties");

    @Test
    public void testFind() throws Exception {
        assertThat(broadcasterService.find("VPRO").getDisplayName()).isEqualTo("VPRO");

    }

    @Test
    public void testFindAll() throws Exception {
        assertThat(broadcasterService.findAll()).hasSize(63);
    }

    @Test
    public void testMisId() {
        BroadcasterService broadcasterService = new BroadcasterServiceImpl("http://poms.omroep.nl/broadcasters/");

        assertThat(broadcasterService.find("RTUT").getMisId()).isEqualTo("RTV Utrecht");

    }
}
