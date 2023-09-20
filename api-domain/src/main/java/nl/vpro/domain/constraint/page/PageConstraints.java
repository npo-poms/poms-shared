/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.constraint.Constraints;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class PageConstraints {

    @SafeVarargs
    public static And and(Constraint<Page>... constraints) {
        return new And(constraints);
    }

    @SafeVarargs
    public static Or or(Constraint<Page>... constraints) {
        return new Or(constraints);
    }

    public static Not not(Constraint<Page> constraint) {
        return new Not(constraint);
    }

    public static Constraint<Page> alwaysTrue() {
        return Constraints.alwaysTrue();
    }

    public static Constraint<Page> alwaysFalse() {
        return Constraints.alwaysFalse();
    }

    public static BroadcasterConstraint broadcaster(Broadcaster broadcaster) {
        return broadcaster(broadcaster.getDisplayName());
    }

    public static BroadcasterConstraint broadcaster(String broadcaster) {
        return new BroadcasterConstraint(broadcaster);
    }
}
