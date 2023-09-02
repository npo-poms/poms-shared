package nl.vpro.media.tva.bindinc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

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

    @Test
    public void find(WireMockRuntimeInfo wiremock) throws IOException {

        stubFor(get(urlPathEqualTo("/broadcasters/")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.properties")), StandardCharsets.UTF_8))));
        stubFor(get(urlPathEqualTo("/broadcasters/mis")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.mis.properties")), StandardCharsets.UTF_8))));
        stubFor(get(urlPathEqualTo("/broadcasters/whats_on")).willReturn(okForContentType("text/plain", IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/broadcasters.mis.properties")), StandardCharsets.UTF_8))));


        BindincBroadcasterService bs = new BindincBroadcasterService(wiremock.getHttpBaseUrl()  + "/broadcasters");

        assertThat(bs.find("BNNVARA")).isNotNull();

        assertThat(bs.find("VER")).isEqualTo(new Broadcaster(""));

        assertThat(bs.find("OMROP FRYSLAN")).isEqualTo(new Broadcaster("ROFR", "Omrop Fryslân"));
    }


}
