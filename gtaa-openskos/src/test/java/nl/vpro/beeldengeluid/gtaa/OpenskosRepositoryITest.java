package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.*;
import java.util.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.util.Env;
import nl.vpro.w3.rdf.Description;

import static nl.vpro.beeldengeluid.gtaa.OpenskosTests.getRealInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Contains tests which you can run to debug problems with connection to actual open skos server.
 * @since 5.5 (copied from dropped GTAARepositoryImplTest)
 * @author Michiel Meeuwissen
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
public class OpenskosRepositoryITest {


    public static Object[] envs() {
        return new Object[]{Env.ACC, Env.PROD, Env.TEST};
    }

    @ParameterizedTest
    @MethodSource("envs")
    public void testPost1(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Pukdasdfsadf"  + System.currentTimeMillis())
            .scopeNotes(new ArrayList<>())
            .build();

        GTAAConcept poms2 = impl.submit(pietjePuk, "POMS2");
        log.info("Received {}", poms2);
    }

    @ParameterizedTest
    @MethodSource("envs")
    public void testPostGeographicName(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        impl.setTenant("beng");
        String name = "Driedorp3" + System.currentTimeMillis();
        GTAANewGenericConcept geographicName = GTAANewGenericConcept
                .builder()
                .name(name)
                .scopeNote("Buurtschap binnen de gemeente Nijkerk")
                .scheme(Scheme.geographicname)
                .build();

        GTAAConcept concept = impl.submit(geographicName, "poms_test");
        assertThat(concept.getScopeNotes()).isNotEmpty();
        assertThat(concept.getName()).isEqualTo(name);

        String result = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:openskos=\"http://openskos.org/xmlns#\" openskos:numFound=\"1\" openskos:rows=\"20\" openskos:start=\"0\">\n" +
                "<rdf:Description rdf:about=\"http://data.beeldengeluid.nl/gtaa/1711531\">\n" +
                "<rdf:type rdf:resource=\"http://www.w3.org/2004/02/skos/core#Concept\"/>\n" +
                "<openskos:modifiedBy rdf:resource=\"http://openskos.beeldengeluid.nl/api/users/f43f43a7-1b02-4d5c-8bdc-e180c112a918\"/>\n" +
                "<openskos:status>candidate</openskos:status>\n" +
                "<openskos:tenant>beng</openskos:tenant>\n" +
                "<dc:creator>poms_test</dc:creator>\n" +
                "<dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2018-10-31T12:57:48+00:00</dcterms:modified>\n" +
                "<openskos:set rdf:resource=\"http://data.beeldengeluid.nl/gtaa\"/>\n" +
                "<skos:prefLabel xml:lang=\"nl\">Driedorp</skos:prefLabel>\n" +
                "<skos:editorialNote xml:lang=\"nl\">Buurtschap binnen de gemeente Nijkerk</skos:editorialNote>\n" +
                "<openskos:uuid>9e06b597-3bb8-4c4b-88a4-5c1dbf8f0741</openskos:uuid>\n" +
                "<dcterms:publisher rdf:resource=\"http://tenant/9bebebb3-d50b-466b-8cd3-a42c39cc8ffc\"/>\n" +
                "<skos:inScheme rdf:resource=\"http://data.beeldengeluid.nl/gtaa/GeografischeNamen\"/>\n" +
                "<skos:notation>1711531</skos:notation>\n" +
                "<dcterms:dateSubmitted>2018-10-31T13:57:47.331+01:00</dcterms:dateSubmitted>\n" +
                "</rdf:Description>\n" +
                "</rdf:RDF>";
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("Vervuilt GTAA")
    public void test409ConflictResolution(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        String label = "Pietje, Puk" + System.currentTimeMillis();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk")
                .scopeNotes(new ArrayList<>()).build();
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }


    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException(Env env) {
        assertThatThrownBy(() -> {
            OpenskosRepository impl = getRealInstance(env);
            GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk" + System.currentTimeMillis())
                .scopeNotes(new ArrayList<>()).build();

            impl.submit(pietjePuk, "POMS");
            impl.submit(pietjePuk, "POMS");
            impl.submit(pietjePuk, "POMS");
        }).isInstanceOf(GTAAConflict.class);
    }


    @ParameterizedTest
    @MethodSource("envs")
    public void testFindPerson(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> persons = impl.findPersons("johan c", 100);
        assertThat(persons).isNotEmpty();
        assertThat(persons.get(0).getStatus()).isNotNull();
        for (Description person : persons)  {
            log.info("{}", person);
        }
    }

    @ParameterizedTest
    @MethodSource("envs")
    public void testFindAnyThing(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> concepts = impl.findAnything("hasselt", 100);
        assertThat(concepts).isNotEmpty();
        assertThat(concepts.get(0).getStatus()).isNotNull();
        for (Description concept : concepts) {
            log.info("{}", concept);

        }
    }

    @ParameterizedTest
    @MethodSource("envs")
    public void testFindGeo(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> geonames = impl.findForSchemes("amsterdam", 1000, new GTAARepository.SchemeOrNot(Scheme.geographicname));
        assertThat(geonames).isNotEmpty();
        assertThat(geonames.get(0).getStatus()).isNotNull();
        for (Description geoname : geonames)  {
            log.info("{}", geoname);
        }
    }


    @ParameterizedTest
    @MethodSource("envs")
    public void testFindByGtaaUrl(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Optional<GTAAConcept> person = impl.get("http://data.beeldengeluid.nl/gtaa/1715195");
        log.info("person: {}", person);
        assertThat(person.get().getId()).isEqualTo(URI.create("http://data.beeldengeluid.nl/gtaa/1715195"));
    }



    @ParameterizedTest
    @MethodSource("envs")
    public void testFindByNotExistingGtaaUrl(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Optional<GTAAConcept> person = impl.get("http://data.beeldengeluid.nl/gtaa/12345");
        log.info("person: {}", person);
        assertThat(person.get().getId()).isEqualTo(URI.create("http://data.beeldengeluid.nl/gtaa/1715195"));
    }


    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testChanges(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = LocalDate.of(2017, 10, 4).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.of(2017, 10, 4).atTime(9, 20).atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (!record.isDeleted())
                assertThat(record.getMetaData().getFirstDescription().isPerson()).isTrue();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), GTAAPerson.create(record.getMetaData().getFirstDescription()));

        }
        assertThat(count).isEqualTo(updates.getSize().get());
        assertThat(count).isGreaterThan(0);
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testGeoLocationsChanges(Env env) {
        GTAARepository impl = getRealInstance(env);
        Instant start = LocalDate.of(2018, 1, 1).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getGeoLocationsUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (!record.isDeleted())
                assertThat(record.getMetaData().getFirstDescription().isGeoLocation()).isTrue();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
        assertThat(count).isGreaterThan(0);
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testAllChanges(Env env) {
        GTAARepository impl = getRealInstance(env);
        Instant start = LocalDate.of(2019, 1, 1).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.of(2019, 3, 1).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getAllUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (record.getMetaData() == null) {
                assertThat(record.getHeader().getStatus()).isEqualTo("deleted");
            }
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);
        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void addPerson(Env env) {
        GTAARepository impl = getRealInstance(env);
        GTAANewPerson p = new GTAANewPerson();
        p.setFamilyName("asdasd");
        p.setGivenName("asdasd");
        //p.setListIndex(0);
        impl.submit(p, "demo-cms:gtaa-user");
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testChangesPersonsRecent(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = Instant.now().minus(Duration.ofDays(70));
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }


   @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testChangesGeolocationsRecent(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = Instant.now().minus(Duration.ofDays(70));
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getGeoLocationsUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);


        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }



    @ParameterizedTest
    @MethodSource("envs")
    @Disabled("This is not a junit test")
    public void testChangesRecent(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = Instant.now().minus(Duration.ofDays(70));
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getAllUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }

    @ParameterizedTest
    @MethodSource("envs")
//    @Disabled("This is not a junit test")
    public void retrieveItemStatus(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Optional<Description> description = impl.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1711640");
        log.info("{} ", description.get());
    }

}
