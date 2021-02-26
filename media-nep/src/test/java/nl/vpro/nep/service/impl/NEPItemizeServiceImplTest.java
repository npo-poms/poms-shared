package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tomakehurst.wiremock.WireMockServer;

import nl.vpro.nep.domain.ItemizerStatus;
import nl.vpro.nep.domain.ItemizerStatusResponse;
import nl.vpro.nep.service.exception.ItemizerStatusException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * @author Michiel Meeuwissen
 * @since 5.24
 */
@Slf4j

@ExtendWith({
    WiremockResolver.class,
    WiremockUriResolver.class
})
public class NEPItemizeServiceImplTest {

    @Test
    public void getJobStatus404(@WiremockResolver.Wiremock WireMockServer server, @WiremockUriResolver.WiremockUri String uri) {
        Properties properties = new Properties();
        properties.setProperty("nep.itemizer-api.baseUrl", uri);
        properties.getProperty("nep.itemizer-api.key", "bearer ");
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(properties);
        server.stubFor(
            get(urlEqualTo("/api/itemizer/jobs/foobar/status"))
                .willReturn(
                    status(404).withBody("{\"success\":false,\"status\":\"error\",\"errors\":\"No job found for token [foobar] and provider [npo]\"}")
                )
        );
        ItemizerStatusException statusException = catchThrowableOfType(() -> {
                ItemizerStatusResponse job = itemizer.getLiveItemizerJobStatus("foobar");
            }, ItemizerStatusException.class);
        assertThat(statusException).isInstanceOf(ItemizerStatusException.class);
        assertThat(statusException.getStatusCode()).isEqualTo(404);
        assertThat(statusException.getResponse()).isNotNull();
        assertThat(statusException.getResponse().getStatus()).isEqualTo("error");
        assertThat(statusException.getResponse().getErrors()).isEqualTo("No job found for token [foobar] and provider [npo]");
        log.info("{}", statusException.toString());
    }

    @Test
    public void getJobStatus(@WiremockResolver.Wiremock WireMockServer server, @WiremockUriResolver.WiremockUri String uri) {
        Properties properties = new Properties();
        properties.setProperty("nep.itemizer-api.baseUrl", uri);
        properties.getProperty("nep.itemizer-api.key", "bearer ");
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(properties);
        server.stubFor(
            get(urlEqualTo("/api/itemizer/jobs/2c829baf-c310-4f09-81c1-fd9a252ded0c/status"))
                .willReturn(
                    ok().withBody("{\"jobId\":\"2c829baf-c310-4f09-81c1-fd9a252ded0c\",\"status\":\"QUEUED\",\"statusMessage\":null}")
                )
        );
        ItemizerStatusResponse job = itemizer.getLiveItemizerJobStatus("2c829baf-c310-4f09-81c1-fd9a252ded0c");

        assertThat(job).isEqualTo(ItemizerStatusResponse.builder().jobId("2c829baf-c310-4f09-81c1-fd9a252ded0c").status(ItemizerStatus.QUEUED).statusMessage(null).build());
        log.info("{}", job);
    }

}
