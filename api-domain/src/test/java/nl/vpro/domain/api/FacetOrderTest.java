package nl.vpro.domain.api;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FacetOrderTest {

    @Test
    public void comparator() {
        List<TermFacetResultItem> list = Arrays.asList(
            new TermFacetResultItem("aaa", "id1", 0),
            new TermFacetResultItem("cc",  "id2", 1),
            new TermFacetResultItem("bb",  "id0", 2)
        );

        list.sort(FacetOrder.toComparator(FacetOrder.COUNT_ASC));

        assertThat(list.toString()).isEqualTo("[id1:0, id2:1, id0:2]");

        list.sort(FacetOrder.toComparator(FacetOrder.COUNT_DESC));

        assertThat(list.toString()).isEqualTo("[id0:2, id2:1, id1:0]");

        list.sort(FacetOrder.toComparator(FacetOrder.VALUE_ASC));

        assertThat(list.toString()).isEqualTo("[id1:0, id0:2, id2:1]");

        list.sort(FacetOrder.toComparator(FacetOrder.VALUE_DESC));

        assertThat(list.toString()).isEqualTo("[id2:1, id0:2, id1:0]");
    }
}
