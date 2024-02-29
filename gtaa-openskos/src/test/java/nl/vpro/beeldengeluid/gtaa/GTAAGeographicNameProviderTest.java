package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.meeuw.i18n.regions.Region;
import org.meeuw.i18n.regions.RegionService;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.domain.gtaa.GTAAGeographicName;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static nl.vpro.beeldengeluid.gtaa.OpenskosRepositoryTest.f;
import static nl.vpro.beeldengeluid.gtaa.OpenskosTests.create;
import static org.assertj.core.api.Assertions.assertThat;


@WireMockTest
@Slf4j
class GTAAGeographicNameProviderTest {

    @Test
    public void stream(WireMockRuntimeInfo runtimeInfo) throws IOException {
        create(runtimeInfo.getHttpBaseUrl());

        WireMock.stubFor(
            get(urlPathEqualTo("/oai-pmh"))
                .willReturn(okXml(f("all-geo-updates.xml"))));


        assertThat(RegionService.getInstance().values(GTAAGeographicName.Code.class)
            .limit(100)).hasSize(100);

    }

    @Test
    public void getByCode(WireMockRuntimeInfo runtimeInfo) throws IOException {
        create(runtimeInfo.getHttpBaseUrl());

        WireMock.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(okXml(f("tongeren.xml"))));

        Optional<Region> optionalRegion = RegionService.getInstance().getByCode("http://data.beeldengeluid.nl/gtaa/1723598");
        assertThat(optionalRegion).isPresent();

        Region region = optionalRegion.get();
        assertThat(region).isInstanceOf(GTAAGeographicName.Code.class);
    }


}
