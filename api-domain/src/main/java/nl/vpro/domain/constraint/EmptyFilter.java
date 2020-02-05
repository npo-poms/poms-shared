package nl.vpro.domain.constraint;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
public class EmptyFilter<T> extends AbstractFilter<T> {

    public static <T> EmptyFilter<T> instance() {
        return new EmptyFilter<>();
    }

    private EmptyFilter() {
        super(Constraints.alwaysTrue());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof EmptyFilter;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
