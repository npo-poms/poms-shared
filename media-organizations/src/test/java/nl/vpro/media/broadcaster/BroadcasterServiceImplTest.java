package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@Slf4j
@WireMockTest
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
    public void testMisId(
        WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
          stubFor(get(urlEqualTo("/broadcasters/"))
              .willReturn(
                aResponse()
                    .withBody(IOUtils.resourceToByteArray("/broadcasters.properties"))
                    .withHeader("Cache-Control", "public, max-age: 3600")
                    .withHeader("Last-Modified", "Wed, 24 Apr 2019 05:55:21 GMT")
            ));

        stubFor(get(urlEqualTo("/broadcasters/mis"))
              .willReturn(
                aResponse()
                    .withBody(IOUtils.resourceToByteArray("/broadcasters.MIS.properties"))
                    .withHeader("Cache-Control", "public, max-age: 3600")
                    .withHeader("Last-Modified", "Wed, 24 Apr 2019 05:55:21 GMT")
            ));
        BroadcasterService broadcasterService = new BroadcasterServiceImpl(wireMockRuntimeInfo.getHttpBaseUrl() + "/broadcasters/", false, true);
        assertThat(broadcasterService.find("RTUT")).isNotNull();
        assertThat(broadcasterService.find("RTUT").getMisId()).isEqualTo("RTV Utrecht");
    }

    @Test
    public void update() {
        Broadcaster vpro = broadcasterService.find("VPRO");
        assertThatThrownBy(() -> {
            broadcasterService.update(vpro);
        }).isInstanceOf(UnsupportedOperationException.class);
         assertThatThrownBy(() -> {
            broadcasterService.delete(vpro);
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void string() {
        assertThat(broadcasterService.toString()).isEqualTo("BroadcasterServiceImpl[classpath:/broadcasters.properties]  63 broadcasters");

    }

}
