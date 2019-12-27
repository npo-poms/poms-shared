package nl.vpro.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class NoDuplicateOwnerValidatorTest {

    public static class A implements Ownable {
        @Getter
        final OwnerType owner;

        A(OwnerType o) {
            this.owner = o;
        }
    }

    @Test
    public void isValid() {
        NoDuplicateOwnerValidator impl = new NoDuplicateOwnerValidator();
        assertThat(impl.isValid(null)).isTrue();
        List<Ownable> list = new ArrayList<>();
        assertThat(impl.isValid(list)).isTrue();
        list.add(new A(OwnerType.NPO));
        assertThat(impl.isValid(list)).isTrue();
        list.add(new A(OwnerType.BROADCASTER));
        assertThat(impl.isValid(list)).isTrue();
        list.add(new A(OwnerType.NPO));
        assertThat(impl.isValid(list)).isFalse();


    }
}
