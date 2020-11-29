package nl.vpro.domain.npoplayer;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author r.jansen
 */
public class PlayerDomainSerializeTest {

    @Test
    public void testRequest() {
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
        Jackson2TestUtil.roundTripAndSimilarAndEquals(playerRequest,
            "{\n" +
                "  \"mid\" : \"MID\",\n" +
                "  \"id\" : \"324242jl222\",\n" +
                "  \"stylesheet\" : \"https://www.vpro.nl/flistender.css\",\n" +
                "  \"autoplay\" : true,\n" +
                "  \"startAt\" : 10,\n" +
                "  \"subtitleLanguage\" : \"nl\",\n" +
                "  \"sterSiteId\" : \"324242-jongstleden-222\",\n" +
                "  \"atInternetSiteId\" : \"324242-jongstleden-222\"\n" +
                "}");
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
            "{\n" +
                "  \"mid\" : \"eenmid\",\n" +
                "  \"token\" : \"b8a1458f-2ece-488f-8178-c0511dec39a8\",\n" +
                "  \"embedUrl\" : \"https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\",\n" +
                "  \"embedCode\" : \"<script>var urlForIframe = \\\"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\\\"; var elementId = \\\"player-KN_1688939\\\"; var mediaId = \\\"KN_1688939\\\";</script><script src=\\\"https://start-player.npo.nl//js/embed.js\\\"></script>\"\n" +
                "}");
    }
}
