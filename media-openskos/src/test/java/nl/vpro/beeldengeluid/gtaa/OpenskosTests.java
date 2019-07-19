package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.gtaa.GTAAConflict;
import nl.vpro.domain.gtaa.GTAANewPerson;
import nl.vpro.domain.gtaa.GTAARepository;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class OpenskosTests {
    @Ignore
    @Test
    public void testPost1() {
        GTAARepository impl = getRealInstance();

        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .build();
        impl.submit(pietjePuk, "POMS2");
    }

    @Test
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk"  + System.currentTimeMillis())
                .build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test(expected = GTAAConflict.class)
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test
    @Ignore
    public void testFindPerson() {
        GTAARepository impl = getRealInstance();
        List<Description> persons = impl.findPersons("pietje", 100);
        assertThat(persons).isNotEmpty();
        System.out.println(persons);
        assertThat(persons.get(0).getStatus()).isNotNull();
    }

    @Test
    @Ignore
    public void testFindAnything() {
        GTAARepository impl = getRealInstance();
        List<Description> items = impl.findAnything("hilversum", 100);
        assertThat(items).isNotEmpty();
        System.out.println(items);
        assertThat(items.get(0).getStatus()).isNotNull();
    }

    @Test
    @Ignore
    public void testChanges() {
        GTAARepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (!record.isDeleted())
                assertThat(record.getMetaData().getFirstDescription().isPerson()).isTrue();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
        assertThat(count).isGreaterThan(0);
    }

    @Test
    @Ignore
    public void testAllChanges() {
        GTAARepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();

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
        // TODO check out why count doesn't match
        // assertThat(count).isEqualTo(updates.getSize().get());
    }

    @Test
    @Ignore
    public void addPerson() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson p = new GTAANewPerson();
        p.setFamilyName("asdasd");
        p.setGivenName("asdasd");
        //p.setListIndex(0);
        impl.submit(p, "demo-cms:gtaa-user");
    }

    @Test
    @Ignore
    public void testChangesRecent() {
        GTAARepository impl = getRealInstance();
        Instant start = Instant.now().minusSeconds(3600000);
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            System.out.println(record);
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }

    @Test
    public void testStatus() {
        GTAARepository impl = getRealInstance();
        impl.retrieveItemStatus("bla");
    }

    private GTAARepository getRealInstance() {
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

        //String host = "http://localhost:8080";
        //String host = "http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/";
        // String host = "http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/";
        String host = "http://test.openskos.beeldengeluid.nl.pictura-dp.nl/";
        // String host = "http://openskos.beeldengeluid.nl/";
        // String host = "http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/";
        // String host =
        // "http://production-v2.openskos.beeldengeluid.nl.pictura-dp.nl/";
        String code = "1dX1nJHX5GNeT8O7";
        // String code = "8il3Ut09weJ4h1GQ";
        String spec = "beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2";
        // String spec = "beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352";
        boolean useXL = true;
        OpenskosRepository impl = new OpenskosRepository(host, code, template);
        impl.setUseXLLabels(useXL);

        impl.init();
        impl.setPersonsSpec(spec);
        return impl;
    }
}
