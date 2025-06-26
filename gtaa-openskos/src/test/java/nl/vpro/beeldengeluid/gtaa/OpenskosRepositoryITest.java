package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.*;
import java.util.*;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.util.Env;
import nl.vpro.w3.rdf.Description;

import static nl.vpro.beeldengeluid.gtaa.OpenskosTests.getRealInstance;
import static nl.vpro.domain.gtaa.Scheme.genrefilmmuseum;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Contains tests which you can run to debug problems with connection to actual open skos server.
 * @since 5.5
 * @author Michiel Meeuwissen
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "HttpUrlsUsage"})
@Slf4j
public class OpenskosRepositoryITest {


    public static Object[] envs() {
        return new Object[]{Env.ACC, Env.PROD};
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

        JAXB.marshal(concept, System.out);

        String result = """
            <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:skosxl="http://www.w3.org/2008/05/skos-xl#" xmlns:openskos="http://openskos.org/xmlns#" openskos:numFound="1" openskos:rows="20" openskos:start="0">
            <rdf:Description rdf:about="http://data.beeldengeluid.nl/gtaa/1711531">
            <rdf:type rdf:resource="http://www.w3.org/2004/02/skos/core#Concept"/>
            <openskos:modifiedBy rdf:resource="http://openskos.beeldengeluid.nl/api/users/f43f43a7-1b02-4d5c-8bdc-e180c112a918"/>
            <openskos:status>candidate</openskos:status>
            <openskos:tenant>beng</openskos:tenant>
            <dc:creator>poms_test</dc:creator>
            <dcterms:modified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime">2018-10-31T12:57:48+00:00</dcterms:modified>
            <openskos:set rdf:resource="http://data.beeldengeluid.nl/gtaa"/>
            <skos:prefLabel xml:lang="nl">Driedorp</skos:prefLabel>
            <skos:editorialNote xml:lang="nl">Buurtschap binnen de gemeente Nijkerk</skos:editorialNote>
            <openskos:uuid>9e06b597-3bb8-4c4b-88a4-5c1dbf8f0741</openskos:uuid>
            <dcterms:publisher rdf:resource="http://tenant/9bebebb3-d50b-466b-8cd3-a42c39cc8ffc"/>
            <skos:inScheme rdf:resource="http://data.beeldengeluid.nl/gtaa/GeografischeNamen"/>
            <skos:notation>1711531</skos:notation>
            <dcterms:dateSubmitted>2018-10-31T13:57:47.331+01:00</dcterms:dateSubmitted>
            </rdf:Description>
            </rdf:RDF>""";
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
        List<Description> persons = impl.findPersons("Mies Bouwman", 100);
        assertThat(persons).isNotEmpty();
        assertThat(persons.getFirst().getStatus()).isNotNull();
        for (Description person : persons)  {
            log.info("{}", person);
        }
    }


    private static List<Object[]> envsAnd() {
        List<String> names = List.of("Lutjebroek", "Gendringen");
        return Arrays.stream(envs()).map(e -> names.stream().map(n -> new Object[] {e, n}).toList()).flatMap(Collection::stream)
            .toList();
    }


    @ParameterizedTest
    @MethodSource("envsAnd")
    public void testFindGeographicalNAme(Env env, String name) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> names = impl.findForSchemes(name, 100, GTAARepository.SchemeOrNot.of(Scheme.geographicname));
        assertThat(names).withFailMessage(() -> "Could not find descriptions for %s".formatted(name)).hasSizeGreaterThan(0);
        for (Description desc : names) {
            log.info("{} -> {}", name, desc);
        }
    }


    @ParameterizedTest
    @MethodSource("envs")
    public void testFindAnyThing(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> concepts = impl.findAnything("hasselt", 100);
        assertThat(concepts).isNotEmpty();
        assertThat(concepts.getFirst().getStatus()).isNotNull();
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
        assertThat(geonames.getFirst().getStatus()).isNotNull();
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
    public void testFindByGtaaUrlGeo(Env env) {
        OpenskosRepository impl = getRealInstance(env);

        Optional<GTAAConcept> geo = impl.get("http://data.beeldengeluid.nl/gtaa/1723598");
        assertThat(geo.get().getId()).isEqualTo(URI.create("http://data.beeldengeluid.nl/gtaa/1723598"));

    }

    @ParameterizedTest
    @MethodSource("envs")
    public void testFindByNotExistingGtaaUrl(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Optional<GTAAConcept> person = impl.get("http://data.beeldengeluid.nl/gtaa/1234567890123456789012345678901234567890");
        log.info("person: {}", person);
        assertThat(person).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("envs")
    @Disabled
    public void testFindEyeGenre(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> concepts = impl.findForSchemes("science", 100, GTAARepository.SchemeOrNot.of(genrefilmmuseum));
        assertThat(concepts).isNotEmpty();
        assertThat(concepts.getFirst().getStatus()).isNotNull();
        for (Description concept : concepts) {
            log.info("{}", concept);

        }
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
        Instant start = Instant.now().minus(Duration.ofDays(200));
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
    //@Disabled("This is not a junit test")
    public void testChangesGeolocationsRecent(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = Instant.now().minus(Duration.ofDays(700));
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
    //@Disabled("This is not a junit test")
    public void testChangesRecent(Env env) {
        OpenskosRepository impl = getRealInstance(env);
        Instant start = Instant.now().minus(Duration.ofDays(300));
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
