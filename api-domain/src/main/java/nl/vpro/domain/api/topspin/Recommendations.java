package nl.vpro.domain.api.topspin;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendations implements Iterable<Recommendation> {
    private List<Recommendation> recommendations = new ArrayList<>();

    @Override
    public Iterator<Recommendation> iterator() {
        return recommendations.iterator();

    }
}
