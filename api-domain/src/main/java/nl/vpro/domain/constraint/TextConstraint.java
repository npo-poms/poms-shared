package nl.vpro.domain.constraint;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public interface TextConstraint<T> extends FieldConstraint<T> {


    String getValue();

    void setValue(String value);




}
