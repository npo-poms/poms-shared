package nl.vpro.nep.domain;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class NEPItemizeRequestTest {


    @Test
    public void json() {
        NEPItemizeRequest request = NEPItemizeRequest.builder()
            .starttime("2018-05-09T16:03:01.121")
            .endtime("2018-05-09T16:53:01.122")
            .identifier("npo-1dvr")
            .build();

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getLenientInstance(), request, "{\n" +
            "  \"identifier\" : \"npo-1dvr\",\n" +
            "  \"starttime\" : \"2018-05-09T16:03:01.121\",\n" +
            "  \"endtime\" : \"2018-05-09T16:53:01.122\"\n" +
            "}");

        // {"starttime":"2018-05-09T14:59:25.405","endtime":"2018-05-09T14:59:31.548","identifier":"npo-1dvr"}
    }


    @Test
    public void jsonTruncated() {
        NEPItemizeRequest request = NEPItemizeRequest.builder()
            .starttime("2018-05-09T16:00:00.000")
            .endtime("2018-05-09T16:00:00.000")
            .identifier("npo-1dvr")
            .build();

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getLenientInstance(), request, "{\n" +
            "  \"identifier\" : \"npo-1dvr\",\n" +
            "  \"starttime\" : \"2018-05-09T16:00:00.000\",\n" +
            "  \"endtime\" : \"2018-05-09T16:00:00.000\"\n" +
            "}");

        // {"starttime":"2018-05-09T14:59:25.405","endtime":"2018-05-09T14:59:31.548","identifier":"npo-1dvr"}
    }

}
