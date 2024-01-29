package nl.vpro.media.odi;

import jakarta.servlet.http.HttpServletRequest;

import nl.vpro.domain.media.Location;
import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */

public class FinalLocationHandler implements LocationProducer {

    private final LocationProducer finalHandler;

    public FinalLocationHandler(LocationProducer finalHandler) {
        this.finalHandler = finalHandler;
    }

    @Override
    public int score(Location location, String... pubOptions) {
        if (finalHandler.score(location, pubOptions) > 0) {
            return 0;
        }
        return -1;
    }

    @Override
    public boolean supports(int score) {
        return score >= 0;
    }

    @Override
    public LocationResult produce(Location location, HttpServletRequest request, String... pubOptions) {
        return finalHandler.produce(location, request, pubOptions);

    }

    @Override
    public String toString() {
        return "finally " + finalHandler.toString();
    }
}
