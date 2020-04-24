package nl.vpro.domain.api;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public abstract  class RangeMatcherTest<S extends Comparable<S>, T extends RangeMatcher<S, S>> {

    abstract T getInstance();

    abstract S getValue();



    @Test
    public void testSetInclusiveEnd() {
        T instance = getInstance();
        getInstance().setInclusiveEnd(false);
        assertFalse(instance.includeEnd());
        instance.setInclusiveEnd(true);
        assertTrue(instance.includeEnd());
    }

    @Test
    public void testToString() {
        T instance = getInstance();
        instance.toString();
    }

    @Test
    public void testEquals() {
        T instance = getInstance();
        assertEquals(instance, instance);
        // todo
    }

    @Test
    public abstract void testHashCode();


    @Test
    public void testGetBegin() {
        T instance = getInstance();
        S value = getValue();
        instance.setBegin(value);
        assertEquals(value, instance.getBegin());
    }

    @Test
    public void testGetEnd() {
        T instance = getInstance();
        S value = getValue();
        instance.setEnd(value);
        assertEquals(value, instance.getEnd());
    }

    @Test
    public void testSetEnd() {

    }
}
