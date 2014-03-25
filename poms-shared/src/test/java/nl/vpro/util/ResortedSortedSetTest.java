package nl.vpro.util;

import java.util.*;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class ResortedSortedSetTest {


    @Test
    public void test() {

        Collection<String> test = new ArrayList<>();
        test.addAll(Arrays.asList("b", "a"));

        SortedSet<String> resorted = new ResortedSortedSet<>(test, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);

            }
        });
        {
            Iterator<String> i = resorted.iterator();
            assertThat(i.next()).isEqualTo("a");
            assertThat(i.next()).isEqualTo("b");
        }

        {
            Iterator<String> i = resorted.iterator();
            assertThat(i.next()).isEqualTo("a");
            i.remove();
            assertThat(i.next()).isEqualTo("b");
            assertThat(test).hasSize(1);

        }


    }
}
