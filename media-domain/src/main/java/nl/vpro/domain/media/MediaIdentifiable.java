package nl.vpro.domain.media;

import java.util.List;
import java.util.Optional;

/**
 * An object that contains various fields to identify a POMS media object.
* @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaIdentifiable extends MidIdentifiable {

    @Override
    default Long getId() {
        return null;
    }
    List<String> getCrids();

    default Optional<String> getMainIdentifier() {
        String mid = getMid();
        if (mid != null) {
            return Optional.of(mid);
        }
        List<String> crids = getCrids();
        if (crids != null && !crids.isEmpty()) {
            return Optional.of(crids.get(0));
        }
        return Optional.empty();
    }

    default String getCorrelationId() {
        return getMainIdentifier()
            .orElseGet(() -> "" + hashCode()
            );
    }
}
