package nl.vpro.npoplayer;

import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.npoplayer.NPOPlayerApiRequest;
import nl.vpro.domain.npoplayer.NPOPlayerApiResponse;
import nl.vpro.util.Env;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author r.jansen
 */
@Ignore("Tests running server")
@Slf4j
public class NPOPlayerApiClientTest {

    NPOPlayerApiClient client;

    @Before
    public void init() {
        client = NPOPlayerApiClient.configured(Env.TEST)
            .build();
    }

    @Test
    public void testGetRestService() {
        log.info("{}", client);
        NPOPlayerApiResponse response = client.getRestService().getVideo("KN_1688939", NPOPlayerApiRequest.builder()
            .id("eenid")
            .startAt(10)
            .endAt(100)
            .build());

        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getEmbedUrl()).startsWith("https://start-player.npo.nl/embed");
        assertThat(response.getEmbedCode()).startsWith("<iframe ");
    }

    @Test
    public void testGetRestServiceWithTopSpin() {
        NPOPlayerApiResponse response = client.getRestService().getVideoWithTopspin("KN_1688939", NPOPlayerApiRequest.builder()
            .id("eenid")
            .startAt(10)
            .endAt(100)
            .build());

        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getEmbedUrl()).startsWith("https://start-player.npo.nl/embed");
        assertThat(response.getEmbedCode()).startsWith("<script>");
    }
}
