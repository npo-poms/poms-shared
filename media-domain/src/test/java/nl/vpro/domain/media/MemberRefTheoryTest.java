package nl.vpro.domain.media;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.experimental.theories.DataPoint;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.test.theory.ComparableTest;

import static nl.vpro.domain.media.MediaType.CLIP;
import static org.assertj.core.api.Assertions.assertThat;

public class MemberRefTheoryTest extends ComparableTest<MemberRef> {

    private static Program member = new Program(1L);

    private static Group owner = new Group(2L);

    @DataPoint
    public static MemberRef nullArgument = null;

    @DataPoint
    public static MemberRef nullFields = new MemberRef();

    @DataPoint
    public static MemberRef midReference = memberRefWithMid();

    @DataPoint
    public static MemberRef urnReference = memberRefWithUrn();

    @DataPoint
    public static MemberRef cridReference = memberRefWithCrid();

    @DataPoint
    public static MemberRef duplicateA = new MemberRef(member, owner, 1, null);

    @DataPoint
    public static MemberRef duplicateB = new MemberRef(member, owner, 1, null);

    @DataPoint
    public static MemberRef duplicateWithHigherPosition = new MemberRef(member, owner, 2, null);

    @DataPoint
    public static MemberRef duplicateWithId10 = new MemberRef(10L, member, owner, 1, null);

    @DataPoint
    public static MemberRef duplicateWithId20 = new MemberRef(20L, member, owner, 1, null);

    @Test
    public void testEqualsOnDuplicates() {
        assertThat(duplicateA).isEqualTo(duplicateB);
    }

    @Test
    public void testCompareToOnDuplicates() {
        assertThat(duplicateA.compareTo(duplicateB)).isEqualTo(0);
    }

    @Test
    public void testEqualsOnDuplicateWithOtherNumber() {
        assertThat(duplicateA).isNotEqualTo(duplicateWithHigherPosition);
    }

    @Test
    public void testCompareToOnDuplicateWithOtherNumber() {
        assertThat(duplicateA.compareTo(duplicateWithHigherPosition)).isLessThan(0);
    }

    @Test
    public void testEqualsIgnoreId() {
        assertThat(duplicateWithId10).isEqualTo(duplicateWithId20);
    }

    @Test
    public void testCompareToIgnoreId() {
        assertThat(duplicateWithId10.compareTo(duplicateWithId20)).isEqualTo(0);
    }

    @Test
    public void testTypeRoundTrip() {
        MemberRef memberRef = new MemberRef();
        memberRef.setType(CLIP);
        MemberRef result = JAXBTestUtil.roundTripAndSimilar(memberRef, "<memberRef highlighted=\"false\" type=\"CLIP\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"/>\n");
        assertThat(result.getType()).isEqualTo(CLIP);
    }

    private static MemberRef memberRefWithMid() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setMidRef("VPROWON_12345");
        return memberRef;
    }

    private static MemberRef memberRefWithUrn() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setUrnRef("urn:12345");
        return memberRef;
    }

    private static MemberRef memberRefWithCrid() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setCridRef("crid://somedomain");
        return memberRef;
    }
}
