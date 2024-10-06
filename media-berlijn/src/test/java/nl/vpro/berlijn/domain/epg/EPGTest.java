package nl.vpro.berlijn.domain.epg;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.berlijn.domain.Parser;
import nl.vpro.berlijn.domain.Util;
import nl.vpro.berlijn.util.kafka.KafkaDumpReader;


@Log4j2
public class EPGTest {


    public static Stream<byte[]> epg() {
        return KafkaDumpReader.read(Util.getTable(("/epg/epg-messages.table"))).map(KafkaDumpReader.Record::bytes).limit(1000);
    }

    static Parser parser = Parser.getInstance();

    @ParameterizedTest
    @MethodSource("epg")
    void json(byte[] json) throws IOException {

        EPG epg = parser.parseEpg(json);

        log.debug("{}", epg);


        //assertThat(epg.contents().entries()).hasSize(38);
        //assertThat(epg.contents().date()).isEqualTo(LocalDate.of(2024, 2, 14));

    }

}
