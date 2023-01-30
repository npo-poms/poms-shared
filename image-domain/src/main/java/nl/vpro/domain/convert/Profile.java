package nl.vpro.domain.convert;

import nl.vpro.domain.image.Dimension;

/**
 * Defines one conversion pattern entry in the poms image server
 * <p>
 * Override either {@link #dynamicTest(String)} or {@link #test(String)} Otherwise circular reference!
 */
public interface Profile<O> {

    /**
     * Given a string representing the profile, returns some object that would be left after parsing it.
     */
    @SuppressWarnings("unchecked")
    default O toObject(String request) {
        return (O) request;
    }


    default TestResult<O> dynamicTest(String request) {
        O o = toObject(request);
        return new TestResult<>(test(request), o);
    }


    default TestResult.MatchResult test(String request) {
        return dynamicTest(request).test();
    }

    /**
     * Some conversions may change the dimensions of an image
     *
     */
    default Dimension convertedDimension(Object s, Dimension in) {
        return in;
    }

}
