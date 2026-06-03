package nl.vpro.domain.npoplayer;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author r.jansen
 */
@Deprecated
public class PlayerDomainSerializeTest {

    PlayerRequest playerRequest = PlayerRequest.builder()
            .mid("MID")
            .id("324242jl222")
            .autoplay(true)
            .startAt(Duration.ofSeconds(10))
            .stylesheet("https://www.vpro.nl/flistender.css")
            .subtitleLanguage("nl")
            .sterSiteId("324242-jongstleden-222")
            .atInternetSiteId("324242-jongstleden-222")
            .build();
    @Test
    public void testRequestJson() {

        Jackson2TestUtil.roundTripAndSimilarAndEquals(playerRequest,
            """
                {
                  "mid" : "MID",
                  "id" : "324242jl222",
                  "stylesheet" : "https://www.vpro.nl/flistender.css",
                  "autoplay" : true,
                  "startAt" : 10,
                  "subtitleLanguage" : "nl",
                  "sterSiteId" : "324242-jongstleden-222",
                  "atInternetSiteId" : "324242-jongstleden-222"
                }""");
    }

    @Test
    public void testResponse() {
        PlayerResponse playerResponse = PlayerResponse.builder()
            .mid("eenmid")
            .token("b8a1458f-2ece-488f-8178-c0511dec39a8")
            .embedUrl("https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8")
            .embedCode("<script>var urlForIframe = \"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\"; var elementId = \"player-KN_1688939\"; var mediaId = \"KN_1688939\";</script><script src=\"https://start-player.npo.nl//js/embed.js\"></script>")
            .build();
        Jackson2TestUtil.roundTripAndSimilarAndEquals(playerResponse,
            """
                {
                  "mid" : "eenmid",
                  "token" : "b8a1458f-2ece-488f-8178-c0511dec39a8",
                  "embedUrl" : "https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8",
                  "embedCode" : "<script>var urlForIframe = \\"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\\"; var elementId = \\"player-KN_1688939\\"; var mediaId = \\"KN_1688939\\";</script><script src=\\"https://start-player.npo.nl//js/embed.js\\"></script>"
                }""");
    }
}
