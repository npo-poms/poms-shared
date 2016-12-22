package nl.vpro.domain.constraint;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class Constraints {


    @SuppressWarnings("unchecked")
    public static <T> Constraint<T> alwaysTrue() {
        return Constraints.AlwaysTrue.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Constraint<T> alwaysFalse() {
        return Constraints.AlwaysFalse.INSTANCE;
    }


    public static class AlwaysTrue<T> implements Constraint<T> {
        private static AlwaysTrue INSTANCE = new AlwaysTrue();

        @Override
        public boolean test(T mediaObject) {
            return true;
        }
        @Override
        public String toString() {
            return "ALWAYS TRUE";
        }
    }

    public static class AlwaysFalse<T> implements Constraint<T> {
        private static AlwaysFalse INSTANCE = new AlwaysFalse();

        @Override
        public boolean test(T mediaObject) {
            return false;
        }

        @Override
        public String toString() {
            return "ALWAYS FALSE";
        }
    }
}
