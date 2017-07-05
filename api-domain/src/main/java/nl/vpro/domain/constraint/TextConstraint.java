package nl.vpro.domain.constraint;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public interface TextConstraint<T> extends FieldConstraint<T> {

    String getValue();

    @Override
    default List<String> getDefaultBundleKey() {
        return Lists.asList(
            getClass().getSimpleName() + "/" + getESPath() + "/" + getValue(),
            FieldConstraint.super.getDefaultBundleKey().toArray(new String[0])
        );
    }
}
