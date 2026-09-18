package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
abstract  class RangeMatcherTest<S extends Comparable<S>, T extends RangeMatcher<S, S>> {

    abstract T getInstance();

    abstract S getValue();



    @Test
    void setInclusiveEnd() {
        T instance = getInstance();
        getInstance().setInclusiveEnd(false);
        assertFalse(instance.includeEnd());
        instance.setInclusiveEnd(true);
        assertTrue(instance.includeEnd());
    }

    @Test
    void stringRepresentation() {
        T instance = getInstance();
        instance.toString();
    }

    @Test
    void equality() {
        T instance = getInstance();
        assertEquals(instance, instance);
        // todo
    }

    @Test
    abstract void hashCodeConsistency();


    @Test
    void getBegin() {
        T instance = getInstance();
        S value = getValue();
        instance.setBegin(value);
        assertEquals(value, instance.getBegin());
    }

    @Test
    void getEnd() {
        T instance = getInstance();
        S value = getValue();
        instance.setEnd(value);
        assertEquals(value, instance.getEnd());
    }

    @Test
    void setEnd() {

    }
}
