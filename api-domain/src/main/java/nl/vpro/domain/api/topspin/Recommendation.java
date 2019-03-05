package nl.vpro.domain.api.topspin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendation {
    @NonNull
    private String midRef;

    private Float score;

    private String recommender;

    public Recommendation(String m) {
        this.midRef = m;
    }


}
