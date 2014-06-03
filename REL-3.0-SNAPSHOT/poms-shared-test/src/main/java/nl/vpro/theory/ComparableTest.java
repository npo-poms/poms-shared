package nl.vpro.theory;

import org.junit.experimental.theories.Theory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;

/**
 * @author Michiel Meeuwissen
 * @since 1.6
 */
public class ComparableTest<T> extends ObjectTest<T> {

    @Theory
    public final void equalsConstistentWithComparable(T x, T y) {
        assumeNotNull(y);
        assumeThat(x instanceof Comparable, is(true));
        Comparable<T> comparableX = (Comparable<T>) x;
        //Comparable<T> comparableY = (Comparable<T>) y;
        assertThat(comparableX.compareTo(y) == 0, is(x.equals(y)));
    }

    @Theory
    public final void compareToNull(T x) {
        assumeNotNull(x);
        assumeThat(x instanceof Comparable, is(true));
        Comparable<T> comparableX = (Comparable<T>) x;
        try {
            comparableX.compareTo(null);
            fail("Compare to null should throw NPE");
        } catch (NullPointerException npe) {
            // ok
        }
    }
}
