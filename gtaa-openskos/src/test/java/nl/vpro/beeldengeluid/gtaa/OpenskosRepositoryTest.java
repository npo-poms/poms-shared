package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tomakehurst.wiremock.WireMockServer;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith({
    WiremockResolver.class,
    WiremockUriResolver.class
})
@Slf4j
public class OpenskosRepositoryTest {


    @Test
    public void findPersons(@Wiremock WireMockServer server, @WiremockUri String uri) throws IOException {
        OpenskosRepository repo = create(uri);

        assertThat(repo.toString()).startsWith("OpenskosRepository http://localhost:");

        server.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("/find-person-test.xml"))));

        List<Description> persons = repo.findPersons("test", 1);
        assertThat(persons).isNotEmpty();
        Description description = persons.get(0);
        assertThat(description.getStatus()).isNotNull();
        assertThat(description.getPrefLabel().getValue()).isEqualTo("test2, test.");
        assertThat(description.getStatus()).isEqualTo(Status.candidate);
    }

    @Test
    public void testAddItem(@Wiremock WireMockServer server, @WiremockUri String uri) throws IOException {
        OpenskosRepository repo = create(uri);

        server.stubFor(post(urlPathEqualTo("/api/concept")).willReturn(okXml(f("/submit-person-response.xml")).withStatus(201)));

        GTAANewGenericConcept testNameX = GTAANewGenericConcept.builder()
            .name("Testlabel1")
            .scopeNote("Note123")
            .scheme(Scheme.name)
            .build();

        GTAAConcept testCreatorX = repo.submit(testNameX, "testCreatorX");
        server.verify(postRequestedFor(urlPathEqualTo("/api/concept")).withRequestBody(
                matchingXPath("//skosxl:literalForm[text() = 'Testlabel1']").withXPathNamespace("skosxl", "http://www.w3.org/2008/05/skos-xl#")));

        assertThat(testCreatorX.getName()).isEqualTo("Testlabel1");
    }

    @Test
    public void updatesNoResults(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("no-updates.xml"))));

        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            assertThat(updates.hasNext()).isFalse();
        }
    }

    @Test
    public void updates(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }

    @Test
    public void getUpdatesAndApplyThem(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }


    @Test
    public void anyUpdates(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(get(urlPathEqualTo("/oai-pmh"))
            .willReturn(okXml(f("any-updates.xml"))));
        try (CountedIterator<Record> updates = repo.getAllUpdates(Instant.EPOCH, Instant.now())) {
            int count = 0;
            Record next = updates.next(); // update 1
            count++;
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Giotakes, Nico");
            assertThat(StringUtils.deleteWhitespace(next.getMetaData().getFirstDescription().getChangeNote().get(0)))
                    .isEqualTo("Forward:http://data.beeldengeluid.nl/gtaa/1672578");
            while(updates.hasNext()) {
                count++;
                next = updates.next();
                try {
                    if (next.getMetaData().getFirstDescription().getAbout().equals(URI.create("http://data.beeldengeluid.nl/gtaa/1011506)"))) {
                        assertThat(next.getMetaData().getFirstDescription().getRedirectedFrom())
                                .contains(URI.create("http://data.beeldengeluid.nl/gtaa/29654"));
                    }
                } catch (Exception e) {

                }
                if (next.getMetaData() == null) {
                    assertThat(next.getHeader().getStatus()).isEqualTo("deleted");
                }
            }
            assertThat(count).isEqualTo(200);
        }
    }




    private String f(String file) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(StringUtils.prependIfMissing(file, "/")), StandardCharsets.UTF_8);
    }

    @Test
    public void testRetrieveItemStatus(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("retrieve-status.xml"))));
        Optional<Description> description = repo.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        assertThat(description.get().getStatus().toString()).isEqualTo("approved");
    }

    @Test
    public void retrieveItemStatusShouldReturnIllegalArgumentEx(@Wiremock WireMockServer server, @WiremockUri String uri) throws Exception {
        OpenskosRepository repo = create(uri);

        server.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(status(500).withBody(f("retrieve-status-not-found.xml"))));
        Optional<Description> desc = repo.retrieveConceptStatus("blabla");
        assertThat(desc.isPresent()).isFalse();
    }

    @Test
    public void retrieveItemStatusShouldReturnUnexpectedError(@Wiremock WireMockServer server, @WiremockUri String uri) {
        OpenskosRepository repo = create(uri);

        assertThatThrownBy(() -> {
            server.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody("Random error")));
            Optional<Description> desc = repo.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        }).isInstanceOf(GTAAError.class);
    }

    @Test
    public void getPerson(@Wiremock WireMockServer server, @WiremockUri String uri) throws IOException {
        OpenskosRepository repo = create(uri);

        server.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(okXml(f("1715195.xml"))));

        Optional<GTAAConcept> concept = repo.get("http://data.beeldengeluid.nl/gtaa/1715195");
        assertThat(concept).isPresent();
        GTAAPerson person = (GTAAPerson) concept.get();
        assertThat(person.getName()).isEqualTo("Delft, Matthijs van"); //  This seems odd
    }

    @Test
    public void getPersonNotFoundOldImpl(@Wiremock WireMockServer server, @WiremockUri String uri) throws IOException {
         OpenskosRepository repo = create(uri);

         server.stubFor(
             get(urlPathEqualTo("/api/find-concepts"))
                 .willReturn(status(500).withBody(f("not-found-old-response.html"))));

         Optional<GTAAConcept> concept = repo.get("http://data.beeldengeluid.nl/gtaa/1715195");
         assertThat(concept).isNotPresent();
    }

    @Test
    public void findAnyThing(@Wiremock WireMockServer server, @WiremockUri String uri) throws IOException {
        OpenskosRepository repo = create(uri);

        server.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(okXml(f("findHasselt.xml"))));

        List<Description> hasselt = repo.findAnything("hasselt", 100);

        assertThat(hasselt).hasSize(15);

    }

    OpenskosRepository create(String uri) {
        OpenskosRepository repo = new OpenskosRepository(uri, "");
        repo.init();
        return repo;
    }



}
