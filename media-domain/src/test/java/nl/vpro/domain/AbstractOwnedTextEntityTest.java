package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class AbstractOwnedTextEntityTest {

    static class OwnedTextEntity extends AbstractOwnedTextEntity<OwnedTextEntity, Serializable> {

        @Serial
        private static final long serialVersionUID = -4862022349042021564L;

        @Getter
        @Setter
        @NotNull
        Serializable parent;

        public OwnedTextEntity(String value, OwnerType owner, TextualType type) {
            super(value, owner, type);
        }
    }
    @Test
    public void equals() {
        OwnedTextEntity a = new OwnedTextEntity("a", OwnerType.BROADCASTER, TextualType.MAIN);
        OwnedTextEntity b = new OwnedTextEntity("b", OwnerType.BROADCASTER, TextualType.MAIN);

        assertThat((CharSequence) a).isEqualTo(b);
        a.setParent("x");
        b.setParent("y");
        assertThat((CharSequence) a).isNotEqualTo(b);
        b.set("a");
        assertThat((CharSequence) a).isEqualTo(b);
        b.set("b");
        b.setParent("x");
        assertThat((CharSequence) a).isEqualTo(b);
        b.setOwner(OwnerType.CERES);
        assertThat((CharSequence) a).isNotEqualTo(b);
        b.setOwner(OwnerType.BROADCASTER);
        assertThat((CharSequence) a).isEqualTo(b);
        b.setType(TextualType.ABBREVIATION);
        assertThat((CharSequence) a).isNotEqualTo(b);


    }

}
