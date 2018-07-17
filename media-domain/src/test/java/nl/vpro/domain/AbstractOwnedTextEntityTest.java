package nl.vpro.domain;

import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class AbstractOwnedTextEntityTest {

    class OwnedTextEntity extends AbstractOwnedTextEntity<OwnedTextEntity, Object> {
        public OwnedTextEntity(String value, OwnerType owner, TextualType type) {
            super(null, value, owner, type);
        }
    }
    @Test
    public void equals() throws Exception {
        OwnedTextEntity a = new OwnedTextEntity("a", OwnerType.BROADCASTER, TextualType.MAIN);
        OwnedTextEntity b = new OwnedTextEntity("b", OwnerType.BROADCASTER, TextualType.MAIN);

        assertThat(a).isEqualTo(b);
        a.setParent("x");
        b.setParent("y");
        assertThat(a).isNotEqualTo(b);
        b.set("a");
        assertThat(a).isEqualTo(b);
        b.set("b");
        b.setParent("x");
        assertThat(a).isEqualTo(b);
        b.setOwner(OwnerType.CERES);
        assertThat(a).isNotEqualTo(b);
        b.setOwner(OwnerType.BROADCASTER);
        assertThat(a).isEqualTo(b);
        b.setType(TextualType.ABBREVIATION);
        assertThat(a).isNotEqualTo(b);


    }

}
