package nl.vpro.domain;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

class PublicationReasonTest {


    @Test
    public void marshall() {
        PublicationReason reason = new PublicationReason("foobar", Instant.EPOCH);
        Jackson2TestUtil.roundTripAndSimilar(reason, "{\n" +
            "  \"value\" : \"foobar\",\n" +
            "  \"publishDate\" : 0\n" +
            "}");
    }

    @Test
    public void marshall2() {
        PublicationReason reason = new PublicationReason("foobar", Instant.EPOCH);
        ObjectNode node = Jackson2Mapper.getInstance().createObjectNode();
        node.putPOJO("test", reason);

        Jackson2TestUtil.roundTripAndSimilar(node, "{\n" +
            "  \"test\" : {\n" +
            "    \"value\" : \"foobar\",\n" +
            "    \"publishDate\" : 0\n" +
            "  }\n" +
            "}");
    }


}
