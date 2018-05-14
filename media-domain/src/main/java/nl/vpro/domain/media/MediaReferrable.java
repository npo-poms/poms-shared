package nl.vpro.domain.media;

import java.util.List;

/**
* @author Michiel Meeuwissen
* @since 5.6
*/
public interface MediaReferrable {

    String getMid();

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
