/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.ArrayList;
import java.util.List;

public interface FieldConstraint<T> extends Constraint<T> {

    String getESPath();

    @Override
    default List<String> getDefaultBundleKey() {
        List<String> result = new ArrayList<>();
        result.add(getClass().getSimpleName() + "/" + getESPath());
        result.addAll(Constraint.super.getDefaultBundleKey());
        return result;
    }
 

}
