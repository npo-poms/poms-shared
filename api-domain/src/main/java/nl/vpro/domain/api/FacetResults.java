package nl.vpro.domain.api;

import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class FacetResults {

    private FacetResults() {
    }

    public static Map<String, Long> toSimpleMap(List<TermFacetResultItem> results) {
        Map<String, Long> values = new LinkedHashMap<>();
        if (results != null) {
            for (TermFacetResultItem b : results) {
                values.put(b.getId(), b.getCount());
            }
        }
        return values;
    }

}
