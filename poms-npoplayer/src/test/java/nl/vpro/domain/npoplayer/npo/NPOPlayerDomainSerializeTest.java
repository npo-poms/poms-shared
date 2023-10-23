package nl.vpro.domain.npoplayer.npo;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.npoplayer.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author r.jansen
 */
@Deprecated
public class NPOPlayerDomainSerializeTest {

    @Test
    public void testJSON() {
        NPOPlayerApiRequest request = NPOPlayerApiRequest.builder()
            .id("eenid")
            .stylesheet("https://www.vpro.nl/flitsend.css")
            .startAt(20)
            .endAt(60)
            .noAds(true)
            .autoplay(true)
            .subtitleLanguage("nl")
            .color("00ff00")
            .comscore(NPOPlayerComscore.builder().npoIngelogd("nee").build())
            .styling(NPOPlayerStyling.builder().subtitles(NPOPlayerSubtitlesStyling.builder().size("10px").build()).build())
            .sterReferralUrl("aap")
            .sterSiteId("noot")
            .sterIdentifier("mies")
            .hasAdConsent(true)
            .pageUrl("http://bla")
            .smarttag(NPOPlayerAtinternet.builder().siteId("wereld-draait-doorrr").build())
            .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(request, """
            {
              "id" : "eenid",
              "stylesheet" : "https://www.vpro.nl/flitsend.css",
              "autoplay" : true,
              "startAt" : 20,
              "endAt" : 60,
              "noAds" : true,
              "subtitleLanguage" : "nl",
              "styling" : {
                "subtitles" : {
                  "size" : "10px"
                }
              },
              "color" : "00ff00",
              "comscore" : {
                "npoIngelogd" : "nee"
              },
              "sterReferralUrl" : "aap",
              "sterSiteId" : "noot",
              "sterIdentifier" : "mies",
              "hasAdConsent" : true,
              "pageUrl" : "http://bla",
              "smarttag" : {
                "siteId" : "wereld-draait-doorrr"
              }
            }
            """
        );
    }


    @Test
    public void testResponse() {
        NPOPlayerApiResponse playerResponse = NPOPlayerApiResponse.builder()
            .token("b8a1458f-2ece-488f-8178-c0511dec39a8")
            .embedUrl("https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8")
            .embedCode("<script>var urlForIframe = \"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\"; var elementId = \"player-KN_1688939\"; var mediaId = \"KN_1688939\";</script><script src=\"https://start-player.npo.nl//js/embed.js\"></script>")
            .build();
        Jackson2TestUtil.roundTripAndSimilarAndEquals(playerResponse,
            """
                {
                  "token" : "b8a1458f-2ece-488f-8178-c0511dec39a8",
                  "embedUrl" : "https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8",
                  "embedCode" : "<script>var urlForIframe = \\"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\\"; var elementId = \\"player-KN_1688939\\"; var mediaId = \\"KN_1688939\\";</script><script src=\\"https://start-player.npo.nl//js/embed.js\\"></script>"
                }""");
    }
}
