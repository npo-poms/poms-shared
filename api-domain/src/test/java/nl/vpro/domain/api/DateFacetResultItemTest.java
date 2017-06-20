package nl.vpro.domain.api;

import java.io.IOException;
import java.time.Instant;

import org.junit.Test;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;


public class DateFacetResultItemTest {


    @Test
    public void json() throws IOException {
        DateFacetResultItem item = new DateFacetResultItem("test", Instant.EPOCH, null, 10);

        assertThatJson(item)
            .isSimilarTo("{\"value\":\"test\",\"begin\":0,\"count\":10,\"selected\":false}")
            .andRounded().isEqualTo(item)
        ;

    }

}
