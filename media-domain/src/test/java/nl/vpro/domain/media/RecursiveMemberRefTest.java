package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
class RecursiveMemberRefTest {


    MediaObject m1 = MediaBuilder.program().build();
    MediaObject m2 = MediaBuilder.program().memberOf(m1).build();
    MediaObject m3 = MediaBuilder.program().memberOf(m2).build();

    {
        // force circular
        m1.getMemberOf().add(new MemberRef(m1, m3, 1, OwnerType.BROADCASTER));
    }


    @Test
    public void marshal() {
        JAXBTestUtil.roundTripAndSimilar(m3, "<a />");
    }




}
