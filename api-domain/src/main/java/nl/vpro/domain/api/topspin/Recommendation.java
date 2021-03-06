package nl.vpro.domain.api.topspin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class Recommendation {
    @NonNull
    private String midRef;

    private Float score;

    private String recommender;

    public Recommendation(String m) {
        this.midRef = m;
    }


}
