package nl.vpro.berlijn.domain.availability;

import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.berlijn.domain.Parser;
import nl.vpro.berlijn.util.kafka.KafkaDumpReader;


@Log4j2
class AvailabilityTest {


    public static Stream<byte[]> messages() {
        return KafkaDumpReader.read(AvailabilityTest.class.getResourceAsStream("/availability/availability-messages.table")).map(KafkaDumpReader.Record::bytes).limit(1000);
    }

    static Parser parser = Parser.getInstance();
    {
        parser.setLenient(false);
    }

    @ParameterizedTest
    @MethodSource("messages")
    void json(byte[] json) {

        Availability availability = parser.parseAvailability(json);

       /* Jackson2TestUtil.roundTripAndSimilarAndEquals(parser.getMapper(), availability, new String(json, StandardCharsets.UTF_8));*/

        log.debug("{}", availability);


        //assertThat(epg.contents().entries()).hasSize(38);
        //assertThat(epg.contents().date()).isEqualTo(LocalDate.of(2024, 2, 14));

    }

    @ParameterizedTest
    @ValueSource(strings = {
        """
            {"type":"update","version":"1.0","metadata":{},"timestamp":"2024-08-16T14:34:30.743Z","contents":{"lastUpdated":"2024-08-16T14:34:30.743Z","notifyTimestamp":"2024-08-16T14:34:01.000Z","predictionCurrentlyIncluded":1,"created":"2024-07-26T00:00:13.000Z","predictionStartTimestamp":"2024-07-26T00:00:13.000Z","prid":"POW_05847737","revoke":null,"availabilityPeriods":null,"source":"postkantoor","notify":true,"platform":"distri-nl","predictionStopped":false}}
            """,
        """
            {"type":
            "update","version":"1.0","metadata":{},"timestamp":"2024-08-16T14:41:46.549Z","contents":{"restrictionsTimestamp":"2024-08-16T14:40:21.000Z","created":"2023-12-28T00:00:08.000Z","predictionEndTimestamp":"2024-01-21T00:00:09.000Z","predictionStartTimestamp":"2023-12-28T00:00:08.000Z","prid":"POW_00289104","revoke":null,"source":"postkantoor","notify":false,
            "platform":"npo-svod","revokedTimestamp":null,"lastUpdated":"2024-08-16T14:41:46.549Z","predictionCurrentlyIncluded":1,"ageRestriction":null,"availabilityPeriods":[{"stop":"2024-09-13T14:25:52.000Z","start":"2024-08-16T14:25:52.000Z","geoIpRestriction":"EU","drm":true}],"predictionStopped":false}}
            """,
        """
        {"version":"1.0","timestamp":1723975903.576000000,"contents":{"restrictionsTimestamp":1723975599.000000000,"notifyTimestamp":null,"lastUpdated":1723975903.576000000,"created":1722124811.000000000,"ageRestriction":null,"prid":"POW_05753710","availabilityPeriods":[{"geoip":null,"encryption":null,"stop":1755511502.000000000,"start":1723975502.000000000,"geoIpRestriction":"EU","drm":true}],"platform":"npo-svod","predictionCurrentlyIncluded":true,"predictionStartTimestamp":1722124811.000000000,"predictionEndTimestamp":null,"predictionUpdatedTimestamp":null,"predictionStopped":false,"source":"postkantoor","restrictions":null,"revoked":null,"revokedTimestamp":null,"date":null,"revoke":null,"notify":false} }
        """,
        """
        {"type":"update","version":"1.0","metadata":{},"timestamp":"2024-09-15T09:36:45.781Z","contents":{"restrictionsTimestamp":"2024-09-15T09:36:37.000Z","created":"2024-08-25T00:00:07.000Z","predictionStartTimestamp":"2024-08-25T00:00:07.000Z","prid":"VPWON_1358139","revoke":null,"broadcasters":["EO"],"source":"postkantoor","notify":false,"platform":"npo-fvod","revokedTimestamp":null,"lastUpdated":"2024-09-15T09:36:45.781Z","predictionCurrentlyIncluded":1,"ageRestriction":null,"availabilityPeriods":[{"stop":"2100-12-31T23:59:00.000Z","start":"2000-01-01T00:00:00.000Z","geoIpRestriction":"WR","drm":true,"isStreamable":true}],"predictionStopped":false}}
        """

    }
    )
    void moreJson(String json) {

        Availability availability = parser.parseAvailability(json.getBytes(StandardCharsets.UTF_8));


        log.debug("{}", availability);
    }


}
