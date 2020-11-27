package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tomakehurst.wiremock.WireMockServer;

import nl.vpro.domain.user.BroadcasterService;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@ExtendWith({
    WiremockResolver.class,
    WiremockUriResolver.class
})
public class BroadcasterServiceImplTest {


    BroadcasterService broadcasterService = new BroadcasterServiceImpl("classpath:/broadcasters.properties", false, true);

    @Test
    public void testFind() {
        assertThat(broadcasterService.find("VPRO").getDisplayName()).isEqualTo("VPRO");
    }

    @Test
    public void testFindAll() {
        assertThat(broadcasterService.findAll()).hasSize(63);
    }

    @Test
    public void testMisId(@WiremockResolver.Wiremock WireMockServer server, @WiremockUriResolver.WiremockUri String uri) throws IOException {
          server.stubFor(get(urlEqualTo("/broadcasters/"))
              .willReturn(
                aResponse()
                    .withBody(IOUtils.resourceToByteArray("/broadcasters.properties"))
                    .withHeader("Cache-Control", "public, max-age: 3600")
                    .withHeader("Last-Modified", "Wed, 24 Apr 2019 05:55:21 GMT")
            ));

        server.stubFor(get(urlEqualTo("/broadcasters/mis"))
              .willReturn(
                aResponse()
                    .withBody(IOUtils.resourceToByteArray("/broadcasters.MIS.properties"))
                    .withHeader("Cache-Control", "public, max-age: 3600")
                    .withHeader("Last-Modified", "Wed, 24 Apr 2019 05:55:21 GMT")
            ));
        BroadcasterService broadcasterService = new BroadcasterServiceImpl(uri + "/broadcasters/", false, true);
        assertThat(broadcasterService.find("RTUT")).isNotNull();
        assertThat(broadcasterService.find("RTUT").getMisId()).isEqualTo("RTV Utrecht");

    }
}
