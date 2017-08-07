package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;

import nl.vpro.beeldengeluid.gtaa.GTAARepository;
import nl.vpro.beeldengeluid.gtaa.OpenskosRepository;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.gtaa.GTAAConflict;
import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

import static org.assertj.core.api.Assertions.assertThat;

//@Ignore("This are not junit tests")
@Slf4j
@Ignore
public class OpenskosTests {
    @Ignore
    @Test
    public void testPost1() {
        GTAARepository impl = getRealInstance();
        String label = "Pietje, Puk" + System.currentTimeMillis();
        impl.submit(label, new ArrayList<>(), "POMS2");
    }

    @Test
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution() {
        GTAARepository impl = getRealInstance();
        String label = "Pietje, Puk" + System.currentTimeMillis();
        impl.submit(label, new ArrayList<>(), "POMS");
        impl.submit(label, new ArrayList<>(), "POMS");
    }

    @Test(expected = GTAAConflict.class)
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException() {
        GTAARepository impl = getRealInstance();
        String label = "Pietje, Puk" + System.currentTimeMillis();
        impl.submit(label, new ArrayList<>(), "POMS");
        impl.submit(label, new ArrayList<>(), "POMS");
        impl.submit(label, new ArrayList<>(), "POMS");
    }

    @Test
    @Ignore("This is not a junit test")
    public void testFindPerson() {
        GTAARepository impl = getRealInstance();
        List<Description> persons = impl.findPersons("pietje", 100);
        assertThat(persons).isNotEmpty();
        System.out.println(persons);
        assertThat(persons.get(0).getStatus()).isNotNull();

    }

    @Test
    @Ignore
    public void testChanges() {
        GTAARepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(Schedule.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getUpdates(start, stop);
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
    public void addPerson() {
        GTAARepository impl = getRealInstance();
        GTAAPerson p = new GTAAPerson();
        p.setFamilyName("asdasd");
        p.setGivenName("asdasd");
        p.setListIndex(0);
        impl.submit(p, "demo-cms:gtaa-user");
    }

    @Test
    @Ignore
    public void testChangesRecent() {
        GTAARepository impl = getRealInstance();
        Instant start = Instant.now().minusSeconds(3600000);
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            System.out.println(record);
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
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

        // Nieuw
        // GTAARepositoryImpl impl = new
        // GTAARepositoryImpl("http://production-v2.openskos.beeldengeluid.nl.pictura-dp.nl/",
        // "8il3Ut09weJ4h1GQ", template);

        // Acceptatie
        // GTAARepositoryImpl impl = new
        // GTAARepositoryImpl("http://accept.openskos.beeldengeluid.nl.pictura-dp.nl/",
        // "1dX1nJHX5GNeT8O7", template);

        // impl.setUseXLLabels(true);
        // Acceptatie
        // GTAARepositoryImpl impl = new
        // GTAARepositoryImpl("http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/",
        // "1dX1nJHX5GNeT8O7", template);

        // Test
         OpenskosRepository impl = new
         OpenskosRepository("http://test.openskos.beeldengeluid.nl.pictura-dp.nl/", "1dX1nJHX5GNeT8O7", template);
        impl.setUseXLLabels(true);
        // productie
        // GTAARepositoryImpl impl = new
        // GTAARepositoryImpl("http://openskos.beeldengeluid.nl/", "1dX1nJHX5GNeT8O7",
        // template);

        // dev

        // GTAARepositoryImpl impl = new
        // GTAARepositoryImpl("http://accept-v1.openskos.beeldengeluid.nl.pictura-dp.nl/",
        // "1dX1nJHX5GNeT8O7", template);

        impl.init();
        /* Acceptatie */
        // impl.setPersonsSpec("beng:gtaa:8fcb1c4f-663d-00d3-95b2-cccd5abda352");
        impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        /* Productie */
        // impl.setPersonsSpec("beng:gtaa:138d0e62-d688-e289-f136-05ad7acc85a2");
        return impl;
    }
}
