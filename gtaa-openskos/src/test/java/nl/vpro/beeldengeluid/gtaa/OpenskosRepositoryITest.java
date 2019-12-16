package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;
import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.ConfigUtils;
import nl.vpro.util.CountedIterator;
import nl.vpro.util.Env;
import nl.vpro.w3.rdf.Description;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.CONFIG_FILE;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Contains tests which you can run to debug problems with connection to actual open skos server.
 * @since 5.5 (copied from dropped GTAARepositoryImplTest)
 * @author Michiel Meeuwissen
 */
@Slf4j
@RunWith(Parameterized.class)
public class OpenskosRepositoryITest {

    private final Env env;



    @Parameterized.Parameters
    public static Object[] envs() {
        return new Object[]{Env.DEV, Env.TEST,  Env.ACC, Env.PROD };
    }

    public OpenskosRepositoryITest(Env env) {
        this.env = env;
    }

    @Ignore
    @Test
    public void testPost1() {
        OpenskosRepository impl = getRealInstance(env);
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .scopeNotes(new ArrayList<>())
            .build();

        impl.submit(pietjePuk, "POMS2");
    }

    @Test
    @Ignore("Vervuilt GTAA")
    public void testPostGeographicName() {
        OpenskosRepository impl = getRealInstance(env);
        impl.setTenant("beng");
        String name = "Driedorp3" + System.currentTimeMillis();
        GTAANewGenericConcept geographicName = GTAANewGenericConcept
                .builder()
                .name(name)
                .scopeNote("Buurtschap binnen de gemeente Nijkerk")
                .scheme(Scheme.geographicname)
                .build();

        ;
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

    @Test
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution() {
        OpenskosRepository impl = getRealInstance(env);
        String label = "Pietje, Puk" + System.currentTimeMillis();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk")
                .scopeNotes(new ArrayList<>()).build();
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test(expected = GTAAConflict.class)
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException() {
        OpenskosRepository impl = getRealInstance(env);
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk"  + System.currentTimeMillis())
                .scopeNotes(new ArrayList<>()).build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test
    @Ignore("This is not a junit test")
    public void testFindPerson() {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> persons = impl.findPersons("johan c", 100);
        assertThat(persons).isNotEmpty();
        assertThat(persons.get(0).getStatus()).isNotNull();
        for (Description person : persons)  {
            log.info("{}", person);
        }
    }

    @Test
    @Ignore("This is not a junit test")
    public void testFindAnyThing() {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> concepts = impl.findAnything("hasselt", 100);
        assertThat(concepts).isNotEmpty();
        assertThat(concepts.get(0).getStatus()).isNotNull();
        for (Description concept : concepts) {
            log.info("{}", concept);

        }
    }

    @Test
    @Ignore("This is not a junit test")
    public void testFindGeo() {
        OpenskosRepository impl = getRealInstance(env);
        List<Description> geonames = impl.findForSchemes("amsterdam", 1000, Scheme.geographicname.name());
        assertThat(geonames).isNotEmpty();
        assertThat(geonames.get(0).getStatus()).isNotNull();
        for (Description geoname : geonames)  {
            log.info("{}", geoname);
        }
    }

    @Test
//    @Ignore
    public void testChanges() {
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

    @Test
//    @Ignore
    public void testGeoLocationsChanges() {
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

    @Test
    //@Ignore
    public void testAllChanges() {
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

    @Test
    @Ignore
    public void addPerson() {
        GTAARepository impl = getRealInstance(env);
        GTAANewPerson p = new GTAANewPerson();
        p.setFamilyName("asdasd");
        p.setGivenName("asdasd");
        //p.setListIndex(0);
        impl.submit(p, "demo-cms:gtaa-user");
    }

    @Test
    public void testChangesPersonsRecent() {
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


    @Test
    public void testChangesGeolocationsRecent() {
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



    @Test
    public void testChangesRecent() {
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

    @Test
    @Ignore
    public void retrieveItemStatus() {
        OpenskosRepository impl = getRealInstance(env);
        Optional<Description> description = impl.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1711640");
        log.info("{} ", description.get());
    }

    OpenskosRepository getRealInstance(final Env env) {
        final OpenskosRepository impl = ConfigUtils.configuredInHome(env, OpenskosRepository.class, CONFIG_FILE);
        impl.init();
        return impl;
    }
}
