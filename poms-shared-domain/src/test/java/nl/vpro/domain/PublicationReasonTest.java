package nl.vpro.domain;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static java.time.Instant.ofEpochMilli;
import static org.assertj.core.api.Assertions.assertThat;

class PublicationReasonTest implements ComparableTheory<PublicationReason> {


    @Test
    public void marshall() {
        PublicationReason reason = new PublicationReason("foobar", Instant.EPOCH);
        Jackson2TestUtil.roundTripAndSimilar(reason, """
            {
              "value" : "foobar",
              "publishDate" : 0
            }""");
    }

    @Test
    public void marshall2() {
        PublicationReason reason = new PublicationReason("foobar", Instant.EPOCH);
        ObjectNode node = Jackson2Mapper.getInstance().createObjectNode();
        node.putPOJO("test", reason);

        Jackson2TestUtil.roundTripAndSimilar(node, """
            {
              "test" : {
                "value" : "foobar",
                "publishDate" : 0
              }
            }""");
    }
    static List<PublicationReason> reasons = List.of(
        new PublicationReason("a", ofEpochMilli(1)),
        new PublicationReason("a", ofEpochMilli(2)),
        new PublicationReason("b", ofEpochMilli(3)),
        new PublicationReason("b", ofEpochMilli(4)),
        new PublicationReason("a", ofEpochMilli(5)),
        new PublicationReason("c", ofEpochMilli(6))
    );

    @Test
    public void toRecordsWithMerge() {
        String records = PublicationReason.toRecords(
            Instant.EPOCH,
            reasons,
            1000,
            "mid_123",
            true
        );
        assertThat(records).isEqualTo("a␟2␞b␟4␞a␟5␞c␟6");
    }


    @Test
    public void toRecords() {
        String records = PublicationReason.toRecords(
            Instant.EPOCH,
            reasons,
            1000,
            "mid_123",
            false
        );
        assertThat(records).isEqualTo("a␟1␞a␟2␞b␟3␞b␟4␞a␟5␞c␟6");

        List<PublicationReason> roundTrip = List.of(PublicationReason.parseList(records, "test"));
        assertThat(roundTrip).containsExactlyElementsOf(reasons);
    }


    @Test
    public void toRecordsWithMax() {
        String records = PublicationReason.toRecords(
            Instant.EPOCH,
            reasons,
            8,
            "mid_123",
            false
        );
        assertThat(records).isEqualTo("a␟1␞a␟2␞b␟3");
    }

    @Override
    public Arbitrary<Object> datapoints() {
        return Arbitraries.of(reasons);
    }

}
