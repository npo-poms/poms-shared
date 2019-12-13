package nl.vpro.domain.api;

import java.util.Arrays;

import org.junit.Test;

import static nl.vpro.domain.api.Result.Total.equalsTo;
import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class ResultTest {
    @Test
    public void testGetList() {
        Result<String> result = new Result<>(Arrays.asList("a", "b"), 10L, 5, equalsTo(20L));
        assertEquals(Arrays.asList("a", "b"), result.getItems());
        assertEquals(Long.valueOf(10), result.getOffset());
        assertEquals(Long.valueOf(20), result.getTotal());
        assertEquals(Integer.valueOf(2), result.getSize());
        assertEquals(Integer.valueOf(5), result.getMax());

    }


    @Test
    public void testIterator() {
        Result<String> result = new Result<>(Arrays.asList("a", "b"), 10L, 5, equalsTo(20L));
        StringBuilder build = new StringBuilder();
        for(String s : result) {
            build.append(s);
        }
        assertEquals("ab", build.toString());
    }
}
