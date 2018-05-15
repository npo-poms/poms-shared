package nl.vpro.domain.media;

import java.util.List;

import nl.vpro.domain.Identifiable;

/**
 * An object that contains various fields to identify a POMS media object.
* @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaIdentifiable extends Identifiable<Long> {

    String getMid();

    @Override
    default Long getId() {
        return null;
    }

    List<String> getCrids();

    MediaType getMediaType();

    default String getCorrelationId() {
        String mid = getMid();
        if (mid != null) {
            return mid;
        }
        List<String> crids = getCrids();
        if (crids != null && !crids.isEmpty()) {
            return crids.get(0);
        }
        return "" + hashCode();
      }
}
