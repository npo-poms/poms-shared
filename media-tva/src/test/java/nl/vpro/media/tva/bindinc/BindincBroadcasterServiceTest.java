package nl.vpro.media.tva.bindinc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.domain.user.Broadcaster;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.33
 */
@WireMockTest
class BindincBroadcasterServiceTest {

    BindincBroadcasterService bs;

    @BeforeEach
    public void setup(WireMockRuntimeInfo wiremock) throws IOException {
        stubFor(get(urlPathEqualTo("/broadcasters/")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.properties")), StandardCharsets.UTF_8))));
        stubFor(get(urlPathEqualTo("/broadcasters/mis")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.mis.properties")), StandardCharsets.UTF_8))));
        stubFor(get(urlPathEqualTo("/broadcasters/whats_on")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.mis.properties")), StandardCharsets.UTF_8))));


        bs = new BindincBroadcasterService(wiremock.getHttpBaseUrl()  + "/broadcasters");
    }


    @Test
    public void find() {



        assertThat(bs.find("BNNVARA")).isNotNull();

        assertThat(bs.find("VER")).isEqualTo(new Broadcaster(""));

        assertThat(bs.find("OMROP FRYSLAN")).isEqualTo(new Broadcaster("ROFR", "Omrop Fryslân"));
    }

    @Test
    public void testToString(WireMockRuntimeInfo wiremock) {
        assertThat(bs.toString()).isEqualTo(
            """
                BindincBroadcasterService[http://localhost:%d/broadcasters/(/mis,whatson)]  67 broadcasters (overriding: [VER, SBS9, BNNVARA, SBS6, AVROTROS, OMROP FRYSLAN, KRO-NCRV, NET5])""".formatted(wiremock.getHttpPort()));

    }

}
