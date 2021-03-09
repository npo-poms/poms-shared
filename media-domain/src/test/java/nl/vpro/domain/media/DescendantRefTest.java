/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import nl.vpro.test.jqwik.BasicObjectTest;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class DescendantRefTest
    // extends ComparableTest<DescendantRef> // TODO DescendantRef doesn't properly implement Comparable
    implements BasicObjectTest<DescendantRef> {

    public static DescendantRef nullArgument = null;

    public static DescendantRef withEmptyFields = new DescendantRef();

    public static DescendantRef midOnly = new DescendantRef("MID", null, MediaType.CLIP);

    public static DescendantRef urnOnly = new DescendantRef(null, "URN", MediaType.CLIP);

    public static DescendantRef withBothMidAndUrn = new DescendantRef("MID", "URN", MediaType.CLIP);

    @Override
    public Arbitrary<? extends DescendantRef> datapoints() {
        return Arbitraries.of(
            nullArgument,
            withEmptyFields,
            midOnly,
            urnOnly,
            withBothMidAndUrn
        );
    }
}
