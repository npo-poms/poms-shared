package nl.vpro.domain.api;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;


public class DateFacetResultItemTest {


    @Test
    public void json() {
        DateFacetResultItem item = DateFacetResultItem.builder().value("test").begin(Instant.EPOCH).count(10).build();

        assertThatJson(item)
            .isSimilarTo("{\"value\":\"test\",\"begin\":0,\"count\":10,\"selected\":false}")
            .andRounded()
            .isEqualTo(item)
        ;

    }

}
