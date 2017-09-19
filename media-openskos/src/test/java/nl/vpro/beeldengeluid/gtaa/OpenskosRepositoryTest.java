package nl.vpro.beeldengeluid.gtaa;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingXPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okXml;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.HttpServerErrorException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


import nl.vpro.domain.media.gtaa.Label;
import nl.vpro.domain.media.gtaa.Status;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

public class OpenskosRepositoryTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(9999));

    private final OpenskosRepository repo = new OpenskosRepository("http://localhost:9999", "");

    @Test
    public void test() throws IOException {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("/find-person-test.xml"))));

        List<Description> persons = repo.findPersons("test", 1);
        assertThat(persons).isNotEmpty();
        Description description = persons.get(0);
        assertThat(description.getStatus()).isNotNull();
        assertThat(description.getPrefLabel().getValue()).isEqualTo("test2, test.");
        assertThat(description.getStatus().equals(Status.candidate));
    }

    @Test
    public void testAddPerson() throws IOException {
        wireMockRule.stubFor(post(urlPathEqualTo("/api/concept")).willReturn(okXml(f("/submit-person-response.xml")).withStatus(201)));

        repo.setUseXLLabels(true);
        repo.submit("Testlabel1", Arrays.asList(new Label("Note123")), "testCreatorX");
        wireMockRule.verify(postRequestedFor(urlPathEqualTo("/api/concept")).withRequestBody(
                matchingXPath("//skosxl:literalForm[text() = 'Testlabel1']").withXPathNamespace("skosxl", "http://www.w3.org/2008/05/skos-xl#")));
    }

    @Test
    public void updatesNoResults() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("no-updates.xml"))));

        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            assertThat(updates.hasNext()).isFalse();
        }
    }

    @Test
    public void updates() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }

    @Test
    public void anyUpdates() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("any-updates.xml"))));
        try (CountedIterator<Record> updates = repo.getAllUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Giotakes, Nico");
            assertThat(StringUtils.deleteWhitespace(next.getMetaData().getFirstDescription().getChangeNote().get(0)))
                    .isEqualTo("Forward:http://data.beeldengeluid.nl/gtaa/1672578");
            for (int i = 0; i < 200; i++) {
                next = updates.next();
                try {
                    if (next.getMetaData().getFirstDescription().getAbout().equals("http://data.beeldengeluid.nl/gtaa/1011506")) {
                        assertThat(next.getMetaData().getFirstDescription().getRedirectedFrom())
                                .isEqualTo("http://data.beeldengeluid.nl/gtaa/29654");
                    }
                } catch (Exception e) {

                }
                if (next.getMetaData() == null) {
                    assertThat(next.getHeader().getStatus()).isEqualTo("deleted");
                }
            }
        }
    }

    private String f(String file) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(StringUtils.prependIfMissing(file, "/")), StandardCharsets.UTF_8);
    }

    @Test
    public void testRetrieveItemStatus() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("retrieve-status.xml"))));
        Optional<Description> description = repo.retrieveItemStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        assertThat(description.get().getStatus().toString()).isEqualTo("approved");
    }

    @Test
    public void retrieveItemStatusShouldReturnIllegalArgumentEx() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody(f("retrieve-status-not-found.xml"))));
        Optional<Description> desc = repo.retrieveItemStatus("blabla");
        assertThat(desc.isPresent()).isFalse();
    }

    @Test(expected = HttpServerErrorException.class)
    public void retrieveItemStatusShouldReturnUnexpectedError() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody("Random error")));
        Optional<Description> desc = repo.retrieveItemStatus("http://data.beeldengeluid.nl/gtaa/1672723");
    }

}
