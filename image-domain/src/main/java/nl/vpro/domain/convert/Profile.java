package nl.vpro.domain.convert;

import java.util.function.Predicate;

import nl.vpro.domain.image.Dimension;

/**
 * Defines one conversion pattern entry in the poms image server
 */
public interface Profile<O> extends Predicate<String> {


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

    @Override
    default  boolean test(String request) {
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
