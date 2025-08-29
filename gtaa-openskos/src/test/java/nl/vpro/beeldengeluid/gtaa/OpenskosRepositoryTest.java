package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static nl.vpro.beeldengeluid.gtaa.OpenskosTests.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"HttpUrlsUsage", "OptionalGetWithoutIsPresent"})
@Slf4j
@WireMockTest
public class OpenskosRepositoryTest {


    @Test
    public void getInstance() {
        OpenskosRepository.instance = null;
        assertThatThrownBy(OpenskosRepository::getInstance).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void findPersons(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        assertThat(repo.toString()).startsWith("OpenskosRepository http://localhost:");

        WireMock.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("/find-person-test.xml"))));

        List<Description> persons = repo.findPersons("test", 1);
        assertThat(persons).isNotEmpty();
        Description description = persons.getFirst();
        assertThat(description.getStatus()).isNotNull();
        assertThat(description.getPrefLabel().getValue()).isEqualTo("test2, test.");
        assertThat(description.getStatus()).isEqualTo(Status.candidate);
    }

    @Test
    public void testAddItem(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(post(urlPathEqualTo("/api/concept")).willReturn(okXml(f("/submit-person-response.xml")).withStatus(201)));

        GTAANewGenericConcept testNameX = GTAANewGenericConcept.builder()
            .name("Testlabel1")
            .scopeNote("Note123")
            .scheme(Scheme.name)
            .build();

        GTAAConcept testCreatorX = repo.submit(testNameX, "testCreatorX");
        WireMock.verify(postRequestedFor(urlPathEqualTo("/api/concept")).withRequestBody(
                matchingXPath("//skosxl:literalForm[text() = 'Testlabel1']").withXPathNamespace("skosxl", "http://www.w3.org/2008/05/skos-xl#")));

        assertThat(testCreatorX.getName()).isEqualTo("Testlabel1");
    }

    @Test
    public void submitDuplicate(WireMockRuntimeInfo wmRuntimeInfo) {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(post(urlPathEqualTo("/api/concept")).willReturn(ok("Concept 'Puk, Pietje (nl)' already exists").withStatus(409)));

        GTAANewPerson pietje = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk")
            .build();

        assertThatThrownBy(() ->
            repo.submit(pietje, "testCreatorX")
        ).isInstanceOf(GTAAConflict.class);
    }


    @Test
    public void updatesNoResults(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("no-updates.xml"))));

        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            assertThat(updates.hasNext()).isFalse();
        }
    }

    @Test
    public void updates(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }

    @Test
    public void getUpdatesAndApplyThem(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(get(urlPathEqualTo("/oai-pmh")).willReturn(okXml(f("updates.xml"))));
        try (CountedIterator<Record> updates = repo.getPersonUpdates(Instant.EPOCH, Instant.now())) {
            Record next = updates.next();
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Benoist, André");
        }
    }


    @Test
    public void anyUpdates(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(get(urlPathEqualTo("/oai-pmh"))
            .willReturn(okXml(f("any-updates.xml"))));
        try (CountedIterator<Record> updates = repo.getAllUpdates(Instant.EPOCH, Instant.now())) {
            int count = 0;
            Record next = updates.next(); // update 1
            count++;
            assertThat(next).isNotNull();
            assertThat(next.getHeader().getDatestamp()).isNotNull();
            assertThat(next.getMetaData().getFirstDescription().getPrefLabel().getValue()).isEqualTo("Giotakes, Nico");
            assertThat(StringUtils.deleteWhitespace(next.getMetaData().getFirstDescription().getChangeNote().getFirst()))
                    .isEqualTo("Forward:http://data.beeldengeluid.nl/gtaa/1672578");
            while(updates.hasNext()) {
                count++;
                next = updates.next();
                try {
                    if (next.getMetaData().getFirstDescription().getAbout().equals(URI.create("http://data.beeldengeluid.nl/gtaa/1011506)"))) {
                        assertThat(next.getMetaData().getFirstDescription().getRedirectedFrom())
                                .contains(URI.create("http://data.beeldengeluid.nl/gtaa/29654"));
                    }
                } catch (Exception ignored) {

                }
                if (next.getMetaData() == null) {
                    assertThat(next.getHeader().getStatus()).isEqualTo("deleted");
                }
            }
            assertThat(count).isEqualTo(200);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    static String f(String file) throws IOException {
        try (InputStream input = OpenskosRepositoryTest.class.getResourceAsStream(Strings.CS.prependIfMissing(file, "/"))) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    public void testRetrieveItemStatus(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(okXml(f("retrieve-status.xml"))));
        Optional<Description> description = repo.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        assertThat(description.get().getStatus().toString()).isEqualTo("approved");
    }

    @Test
    public void retrieveItemStatusShouldReturnIllegalArgumentEx(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(status(500).withBody(f("retrieve-status-not-found.xml"))));
        Optional<Description> desc = repo.retrieveConceptStatus("blabla");
        assertThat(desc.isPresent()).isFalse();
    }

    @Test
    public void retrieveItemStatusShouldReturnUnexpectedError(WireMockRuntimeInfo wmRuntimeInfo) {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        assertThatThrownBy(() -> {
            WireMock.stubFor(get(urlPathEqualTo("/api/find-concepts")).willReturn(status(500).withBody("Random error")));
            Optional<Description> desc = repo.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1672723");
        }).isInstanceOf(GTAAError.class);
    }

    @Test
    public void getPerson(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(okXml(f("1715195.xml"))));

        Optional<GTAAConcept> concept = repo.get("http://data.beeldengeluid.nl/gtaa/1715195");
        assertThat(concept).isPresent();
        GTAAPerson person = (GTAAPerson) concept.get();
        assertThat(person.getName()).isEqualTo("Delft, Matthijs van"); //  This seems odd
    }

    @Test
    public void getPersonNotFoundOldImpl(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
         OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

         WireMock.stubFor(
             get(urlPathEqualTo("/api/find-concepts"))
                 .willReturn(status(500).withBody(f("not-found-old-response.html"))));

         Optional<GTAAConcept> concept = repo.get("http://data.beeldengeluid.nl/gtaa/1715195");
         assertThat(concept).isNotPresent();
    }

    @Test
    public void findAnyThing(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        OpenskosRepository repo = create(wmRuntimeInfo.getHttpBaseUrl());

        WireMock.stubFor(
            get(urlPathEqualTo("/api/find-concepts"))
                .willReturn(okXml(f("findHasselt.xml"))));

        List<Description> hasselt = repo.findAnything("hasselt", 100);

        assertThat(hasselt).hasSize(15);

    }


}
