/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.function.Predicate;

/**

 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface Matcher<V> extends Predicate<V> {

    Match getMatch();

}
