package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum ScheduleSortField implements SortField {
    guideDay(Type.STRING);


    private final Type type;

    ScheduleSortField(Type type) {
        this.type = type;
    }

    @Override
    public Type type() {
        return type;
    }
}
