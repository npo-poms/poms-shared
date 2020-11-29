package nl.vpro.media.odi.handler;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.odi.util.LocationResult;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

public class AAPILocationHandlerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options()
        .dynamicPort()
    );

    @Test
    public void testHandle() {


        wireMockRule.stubFor(
            post(urlEqualTo("/"))
                .willReturn(
                    ok().withBody("https://adaptive.npostream.nl/live")
                )
        );
        AAPILocationHandler handler = new AAPILocationHandler();
        handler.setAAPIServer(wireMockRule.baseUrl());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "somepath");
        Location location = new Location("odiw+http://livestreams.omroep.nl/live/npo/thematv/cultura24/cultura24.isml", OwnerType.BROADCASTER);
        LocationResult result = handler.produce(location, request, "m3u8");
        //assertThat(result.getProgramUrl()).matches(Pattern.quote("http://adaptive.npostreaming.nl/live/npo/thematv/cultura24/cultura24.isml/cultura24.m3u8?hash=") + ".*?" + Pattern.quote("&protection=url&type=http"));

        assertThat(result.getProgramUrl()).isEqualTo("https://adaptive.npostream.nl/live?protection=url&type=http");

    }
}
