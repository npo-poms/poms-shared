package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Contains tests which you can run to debug problems with connection to actual open skos server.
 * @since 5.5 (copied from dropped GTAARepositoryImplTest)
 * @author Michiel Meeuwissen
 */
@Slf4j
public class OpenskosRepositoryITest {

    @Ignore
    @Test
    public void testPost1() {
        OpenskosRepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .notes(new ArrayList<>())
            .build();

        impl.submit(pietjePuk, "POMS2");
    }

    @Test
    //@Ignore
    public void testPostGeographicName() {
        OpenskosRepository impl = getRealInstance();
        impl.setTenant("beng");

        GTAANewGenericConcept geographicName = GTAANewGenericConcept
                .builder()
                .value("Driedorp")
                .note("Buurtschap binnen de gemeente Nijkerk")
                .scheme(Scheme.geographicname)
                .build();

        ;
        impl.submit(geographicName, "poms_test");


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
    public void testGeographicName() {

    }

    @Test
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution() {
        OpenskosRepository impl = getRealInstance();
        String label = "Pietje, Puk" + System.currentTimeMillis();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk")
                .notes(new ArrayList<>()).build();
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test(expected = GTAAConflict.class)
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException() {
        OpenskosRepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk"  + System.currentTimeMillis())
                .notes(new ArrayList<>()).build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test
    @Ignore("This is not a junit test")
    public void testFindPerson() {
        OpenskosRepository impl = getRealInstance();
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
        OpenskosRepository impl = getRealInstance();
        List<Description> persons = impl.findAnything("johan c", 100);
        assertThat(persons).isNotEmpty();
        assertThat(persons.get(0).getStatus()).isNotNull();
        for (Description person : persons)  {
            log.info("{}", person);

        }


    }

    @Test
    @Ignore
    public void testChanges() {
        OpenskosRepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 10, 4).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.of(2017, 10, 4).atTime(9, 20).atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), GTAAPerson.create(record.getMetaData().getFirstDescription()));

        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }

    @Test
    @Ignore
    public void testChangesRecent() {
        OpenskosRepository impl = getRealInstance();
        Instant start = Instant.now().minusSeconds(3600);
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
    @Ignore
    public void retrieveItemStatus() {
        OpenskosRepository impl = getRealInstance();
        Optional<Description> description = impl.retrieveConceptStatus("http://data.beeldengeluid.nl/gtaa/1711640");
        log.info("{} ", description.get());
    }


    private OpenskosRepository getRealInstance() {
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("nl.vpro.beeldengeluid.gtaa", "nl.vpro.w3.rdf", "nl.vpro.openarchives.oai");

        try {
            jaxb2Marshaller.afterPropertiesSet();
        } catch (Exception ex) {
            /* Ignore */
        }
        marshallingHttpMessageConverter.setMarshaller(jaxb2Marshaller);
        marshallingHttpMessageConverter.setUnmarshaller(jaxb2Marshaller);

        RestTemplate template = new RestTemplate();
        template.setMessageConverters(Collections.singletonList(marshallingHttpMessageConverter));


        //Nieuw
        //GTAARepositoryImpl impl = new GTAARepositoryImpl("http://production-v2.openskos.beeldengeluid.nl.pictura-dp.nl/", "8il3Ut09weJ4h1GQ", template);


        // Acceptatie
        //OpenskosRepository impl = new OpenskosRepository("http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/", "***REMOVED***", template);

        // test
//        OpenskosRepository impl = new OpenskosRepository("http://test.openskos.beeldengeluid.nl.pictura-dp.nl/", "***REMOVED***", template);


        // Acceptatie
        //GTAARepositoryImpl impl = new GTAARepositoryImpl("http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/", "***REMOVED***", template);

        // productie
        //OpenskosRepository impl = new OpenskosRepository("http://openskos.beeldengeluid.nl/", "***REMOVED***", template);


        // dev

        OpenskosRepository impl = new OpenskosRepository("http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/", "***REMOVED***", template);

        impl.setUseXLLabels(true);

        impl.setTenant("beng");
        impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        //impl.setPersonsSpec("beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352");

        impl.init();
        /* Acceptatie */
        //impl.setPersonsSpec("beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352");


        /* Productie */
        //impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        return impl;
    }



}
