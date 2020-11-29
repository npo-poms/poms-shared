package nl.vpro.domain.npoplayer.npo;

import nl.vpro.domain.npoplayer.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import org.junit.jupiter.api.Test;

/**
 * @author r.jansen
 */
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

           Jackson2TestUtil.roundTripAndSimilarAndEquals(request, "{\n" +
               "  \"id\" : \"eenid\",\n" +
               "  \"stylesheet\" : \"https://www.vpro.nl/flitsend.css\",\n" +
               "  \"autoplay\" : true,\n" +
               "  \"startAt\" : 20,\n" +
               "  \"endAt\" : 60,\n" +
               "  \"noAds\" : true,\n" +
               "  \"subtitleLanguage\" : \"nl\",\n" +
               "  \"styling\" : {\n" +
               "    \"subtitles\" : {\n" +
               "      \"size\" : \"10px\"\n" +
               "    }\n" +
               "  },\n" +
               "  \"color\" : \"00ff00\",\n" +
               "  \"comscore\" : {\n" +
               "    \"npoIngelogd\" : \"nee\"\n" +
               "  },\n" +
               "  \"sterReferralUrl\" : \"aap\",\n" +
               "  \"sterSiteId\" : \"noot\",\n" +
               "  \"sterIdentifier\" : \"mies\",\n" +
               "  \"hasAdConsent\" : true,\n" +
               "  \"pageUrl\" : \"http://bla\",\n" +
               "  \"smarttag\" : {\n" +
               "    \"siteId\" : \"wereld-draait-doorrr\"\n" +
               "  }\n" +
               "}\n"
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
            "{\n" +
                "  \"token\" : \"b8a1458f-2ece-488f-8178-c0511dec39a8\",\n" +
                "  \"embedUrl\" : \"https://start-player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\",\n" +
                "  \"embedCode\" : \"<script>var urlForIframe = \\\"https://player.npo.nl/embed/b8a1458f-2ece-488f-8178-c0511dec39a8\\\"; var elementId = \\\"player-KN_1688939\\\"; var mediaId = \\\"KN_1688939\\\";</script><script src=\\\"https://start-player.npo.nl//js/embed.js\\\"></script>\"\n" +
                "}");
    }
}
