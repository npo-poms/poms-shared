package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.*;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class OwnableListsTest {

    public static class OwnableStringSupplier implements Supplier<String>, Ownable {
        private final String string;
        @Getter
        private final OwnerType owner;

        public OwnableStringSupplier(String string, OwnerType owner) {
            this.string = string;
            this.owner = owner;
        }

        @Override
        public String get() {
            return string;
        }

        @Override
        public String toString() {
            return owner + ":" + string;
        }
    }


    public static class AOwnable implements Ownable {
        private final OwnerType ownerType;

        public AOwnable(OwnerType ownerType) {
            this.ownerType = ownerType;
        }

        @NonNull
        @Override
        public OwnerType getOwner() {
            return ownerType;

        }
    }

    @Test
    public void containsDuplicateOwner() {
        assertThat(OwnableLists.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER), new AOwnable(OwnerType.AUTHORITY)))).isFalse();
        assertThat(OwnableLists.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER), new AOwnable(OwnerType.BROADCASTER)))).isTrue();
        assertThat(OwnableLists.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER)))).isFalse();
        assertThat(OwnableLists.containsDuplicateOwner(Arrays.asList())).isFalse();
    }


    @Test
    public void copy() {
        List<OwnableStringSupplier> source = Arrays.asList(new OwnableStringSupplier("a", OwnerType.BROADCASTER), new OwnableStringSupplier("b", OwnerType.BROADCASTER));
        List<OwnableStringSupplier> dest = new ArrayList<>(Arrays.asList(new OwnableStringSupplier("a", OwnerType.MIS)));

        OwnableLists.copy(source, dest);

        assertThat(dest).hasSize(2);
        assertThat(dest.toString()).isEqualTo("[MIS:a, BROADCASTER:b]");
    }

}
