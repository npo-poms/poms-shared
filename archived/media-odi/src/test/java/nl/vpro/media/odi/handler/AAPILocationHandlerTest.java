package nl.vpro.media.odi.handler;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.odi.util.LocationResult;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class AAPILocationHandlerTest {


    @Test
    public void testHandle(WireMockRuntimeInfo runtimeInfo) {


        stubFor(
            post(urlEqualTo("/"))
                .willReturn(
                    ok().withBody("https://adaptive.npostream.nl/live")
                )
        );
        AAPILocationHandler handler = new AAPILocationHandler();
        handler.setAAPIServer(runtimeInfo.getHttpBaseUrl());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "somepath");
        Location location = new Location("odiw+http://livestreams.omroep.nl/live/npo/thematv/cultura24/cultura24.isml", OwnerType.BROADCASTER);
        LocationResult result = handler.produce(location, request, "m3u8");
        //assertThat(result.getProgramUrl()).matches(Pattern.quote("http://adaptive.npostreaming.nl/live/npo/thematv/cultura24/cultura24.isml/cultura24.m3u8?hash=") + ".*?" + Pattern.quote("&protection=url&type=http"));

        assertThat(result.getProgramUrl()).isEqualTo("https://adaptive.npostream.nl/live?protection=url&type=http");

    }
}
