package nl.vpro.media.odi.handler;

import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.odi.util.LocationResult;

import static org.assertj.core.api.Assertions.assertThat;

public class AAPILocationHandlerTest {

    @Test
    @Ignore("Test in trunk")
    public void testHandle() {

        AAPILocationHandler handler = new AAPILocationHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "somepath");
        Location location = new Location("odiw+http://livestreams.omroep.nl/live/npo/thematv/cultura24/cultura24.isml", OwnerType.BROADCASTER);
        LocationResult result = handler.produce(location, request, "m3u8");
        assertThat(result.getProgramUrl()).matches(Pattern.quote("http://adaptive.npostreaming.nl/live/npo/thematv/cultura24/cultura24.isml/cultura24.m3u8?hash=") + ".*?" + Pattern.quote("&protection=url&type=http"));

    }
}
