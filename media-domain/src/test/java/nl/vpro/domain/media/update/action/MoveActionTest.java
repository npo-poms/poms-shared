package nl.vpro.domain.media.update.action;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

class MoveActionTest {


    @Test
    public void xml() {
        MoveAction action = MoveAction.builder()
            .from("from")
            .to("to")
            .type(MoveAction.Type.REFERENCE)
            .build();

        JAXBTestUtil.roundTripAndSimilar(action,
            """
                <move xmlns="urn:vpro:media:update:2009" type="REFERENCE">
                    <from>from</from>
                    <to>to</to>
                </move>
                """
            );
    }

    @Test
    public void json() {
        MoveAction action = MoveAction.builder()
            .from("MID_123")
            .to("MID_456")
            .type(MoveAction.Type.REFERENCE)
            .build();

        Jackson2TestUtil.roundTripAndSimilar(action,
            """
                {
                      "from" : "MID_123",
                      "to" : "MID_456",
                      "type" : "REFERENCE"
                }
                """
            );
    }

}
