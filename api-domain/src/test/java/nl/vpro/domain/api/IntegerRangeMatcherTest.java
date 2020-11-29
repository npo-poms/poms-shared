package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class IntegerRangeMatcherTest extends RangeMatcherTest<Integer, RangeMatcher<Integer, Integer>> {

    @Override
    IntegerRangeMatcher getInstance() {
        return new IntegerRangeMatcher(1, 3);
    }

    @Override
    Integer getValue() {
        return 10;
    }

    @Test
    public void testApply() {
        IntegerRangeMatcher instance = getInstance();
        assertTrue(instance.test(2));
        assertTrue(instance.test(1));
        assertFalse(instance.test(0));
        assertFalse(instance.test(4));
        assertFalse(instance.test(3));
    }


    @Override
    public void testHashCode() {
        IntegerRangeMatcher instance = getInstance();
        assertEquals(34, instance.hashCode());
    }
}
