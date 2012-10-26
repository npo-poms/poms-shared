package nl.vpro.theory;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * Test for equals contract of objects.
 * Borrowed from http://stackoverflow.com/questions/837484/junit-theory-for-hashcode-equals-contract
 *
 * @author ernst-jan
 *
 * @param <T> Type of object which is tested
 */
@Ignore
@RunWith(Theories.class)
public abstract class ObjectTest<T> {

    /**
     * For any non-null reference value x, x.equals(x) should return true
     */
    @Theory
    public final void equalsIsReflexive(T x) {
        assumeThat(x, is(not(equalTo(null))));
        assertThat(x.equals(x), is(true));
    }

    /**
     * For any non-null reference values x and y, x.equals(y)
     * should return true if and only if y.equals(x) returns true.
     */
    @Theory
    public final void equalsIsSymmetric(T x, T y) {
        assumeThat(x, is(not(equalTo(null))));
        assumeThat(y, is(not(equalTo(null))));
        assumeThat(y.equals(x), is(true));
        assertThat(x.equals(y), is(true));
    }

    /**
     * For any non-null reference values x, y, and z, if x.equals(y)
     * returns true and y.equals(z) returns true, then x.equals(z)
     * should return true.
     */
    @Theory
    public final void equalsIsTransitive(T x, T y, T z) {
        assumeThat(x, is(not(equalTo(null))));
        assumeThat(y, is(not(equalTo(null))));
        assumeThat(z, is(not(equalTo(null))));
        assumeThat(x.equals(y) && y.equals(z), is(true));
        assertThat(z.equals(x), is(true));
    }

    /**
     * For any non-null reference values x and y, multiple invocations
     * of x.equals(y) consistently return true  or consistently return
     * false, provided no information used in equals comparisons on
     * the objects is modified.
     */
    @Theory
    public final void equalsIsConsistent(T x, T y) {
        assumeThat(x, is(not(equalTo(null))));
        boolean alwaysTheSame = x.equals(y);

        for (int i = 0; i < 30; i++) {
            assertThat(x.equals(y), is(alwaysTheSame));
        }
    }

    /**
     * For any non-null reference value x, x.equals(null) should
     * return false.
     */
    @Theory
    public final void equalsReturnFalseOnNull(T x) {
        assumeThat(x, is(not(equalTo(null))));
        assertThat(x.equals(null), is(false));
    }

    /**
     * Whenever it is invoked on the same object more than once
     * the hashCode() method must consistently return the same
     * integer.
     */
    @Theory
    public final void hashCodeIsSelfConsistent(T x) {
        assumeThat(x, is(not(equalTo(null))));
        int alwaysTheSame = x.hashCode();

        for (int i = 0; i < 30; i++) {
            assertThat(x.hashCode(), is(alwaysTheSame));
        }
    }

    /**
     * If two objects are equal according to the equals(Object) method,
     * then calling the hashCode method on each of the two objects
     * must produce the same integer result.
     */
    @Theory
    public final void hashCodeIsConsistentWithEquals(T x, T y) {
        assumeThat(x, is(not(equalTo(null))));
        assumeThat(x.equals(y), is(true));
        assertThat(x.hashCode(), is(equalTo(y.hashCode())));
    }

    /**
     * Test that x.equals(y) where x and y are the same datapoint
     * instance works. User must provide datapoints that are not equal.
     */
    @Theory
    public final void equalsWorks(T x, T y) {
        assumeThat(x, is(not(equalTo(null))));
        assumeThat(x == y, is(true));
        assertThat(x.equals(y), is(true));
    }



}
