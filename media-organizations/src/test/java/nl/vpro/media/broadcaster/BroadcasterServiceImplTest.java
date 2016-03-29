package nl.vpro.media.broadcaster;

import org.junit.Test;
import nl.vpro.domain.user.BroadcasterService;

import static org.fest.assertions.Assertions.assertThat;

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
}
