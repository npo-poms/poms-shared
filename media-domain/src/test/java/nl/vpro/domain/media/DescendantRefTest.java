/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import org.meeuw.util.test.BasicObjectTheory;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class DescendantRefTest
    // extends ComparableTest<DescendantRef> // TODO DescendantRef doesn't properly implement Comparable
    implements BasicObjectTheory<DescendantRef> {


    public static DescendantRef withEmptyFields = new DescendantRef();

    public static DescendantRef midOnly = new DescendantRef("MID", null, MediaType.CLIP);

    public static DescendantRef urnOnly = new DescendantRef(null, "URN", MediaType.CLIP);

    public static DescendantRef withBothMidAndUrn = new DescendantRef("MID", "URN", MediaType.CLIP);

    @Override
    public Arbitrary<? extends DescendantRef> datapoints() {
        return Arbitraries.of(
            withEmptyFields,
            midOnly,
            urnOnly,
            withBothMidAndUrn
        );
    }
}
