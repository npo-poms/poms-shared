package nl.vpro.berlijn.domain.availability;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.berlijn.domain.Parser;
import nl.vpro.berlijn.util.kafka.KafkaDumpReader;


@Log4j2
class AvailabilityTest {


    public static Stream<byte[]> messages() {
        return KafkaDumpReader.read(AvailabilityTest.class.getResourceAsStream("/availability/availability-messages.table")).map(KafkaDumpReader.Record::bytes).limit(1000);
    }

    static Parser parser = Parser.getInstance();

    @ParameterizedTest
    @MethodSource("messages")
    void json(byte[] json) throws IOException {

        Availability availability = parser.parseAvailability(json);

        log.debug("{}", availability);


        //assertThat(epg.contents().entries()).hasSize(38);
        //assertThat(epg.contents().date()).isEqualTo(LocalDate.of(2024, 2, 14));

    }

}
