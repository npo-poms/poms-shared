package nl.vpro.domain.constraint;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class EnumConstraintTest {

    public enum AnEnum {
        a,
        B,
        ANDC,
        AndD,
        ANDD
    }
    public static class AnEnumConstraint extends EnumConstraint<AnEnum, AnEnum> {

        protected AnEnumConstraint() {
            super(AnEnum.class);
        }

        @Override
        public String getESPath() {
            return null;

        }

        @Override
        protected Collection<AnEnum> getEnumValues(AnEnum input) {
            return asCollection(input);

        }
    }

    AnEnumConstraint anEnumConstraint = new AnEnumConstraint();

    @Test
    public void getValue() {
        anEnumConstraint.setValue("A");
        assertThat(anEnumConstraint.getEnumValue()).isEqualTo(AnEnum.a);
        assertThat(anEnumConstraint.getValue()).isEqualTo("a");
        anEnumConstraint.setValue("ANDC");
        assertThat(anEnumConstraint.getEnumValue()).isEqualTo(AnEnum.ANDC);
        assertThat(anEnumConstraint.getValue()).isEqualTo("ANDC");
        anEnumConstraint.setValue("ANDD");
        assertThat(anEnumConstraint.getEnumValue()).isEqualTo(AnEnum.ANDD);
        assertThat(anEnumConstraint.getValue()).isEqualTo("ANDD");

    }


}
