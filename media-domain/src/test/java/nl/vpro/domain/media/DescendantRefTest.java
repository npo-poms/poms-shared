/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.theory.ObjectTest;

/**
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class DescendantRefTest
    // extends ComparableTest<DescendantRef> // TODO DescendantRef doesn't properly implement Comparable
    extends ObjectTest<DescendantRef> {

    @DataPoint
    public static DescendantRef nullArgument = null;

    @DataPoint
    public static DescendantRef withEmptyFields = new DescendantRef();

    @DataPoint
    public static DescendantRef midOnly = new DescendantRef("MID", null, MediaType.CLIP);

    @DataPoint
    public static DescendantRef urnOnly = new DescendantRef(null, "URN", MediaType.CLIP);

    @DataPoint
    public static DescendantRef withBothMidAndUrn = new DescendantRef("MID", "URN", MediaType.CLIP);
}
