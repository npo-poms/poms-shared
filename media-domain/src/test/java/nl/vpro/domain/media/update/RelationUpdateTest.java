package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class RelationUpdateTest {
    @Test
    public void testCompareToUrnEquals() {
        RelationUpdate rel1 = new RelationUpdate("type", "vpro", "uri", "text1", "urn:relation:1");
        RelationUpdate rel2 = new RelationUpdate("type", "vpro", "uri", "text2", "urn:relation:1");
        assertThat(rel1.compareTo(rel2)).isEqualTo(0); // same urn , so equals

    }

    @Test
    public void testCompareToUrnsNull() {
        RelationUpdate rel1 = new RelationUpdate("type", "vpro", "uri", "text1");
        RelationUpdate rel2 = new RelationUpdate("type", "vpro", "uri", "text2");
        assertThat(rel1.compareTo(rel2)).isLessThan(0);
    }

    @Test
    public void testCompareToUrnsNullOtherwiseEquals() {
        RelationUpdate rel1 = new RelationUpdate("type", "vpro", "uri", "text");
        RelationUpdate rel2 = new RelationUpdate("type", "vpro", "uri", "text");
        assertThat(rel1.compareTo(rel2)).isNotEqualTo(0); // different objects, so different

    }

}
