package nl.vpro.domain.media;

import lombok.Getter;

import java.io.Serializable;
import java.util.*;

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
        return getCorrelation().id;
    }

    default Correlation getCorrelation () {
        String mid = getMid();
        if (mid != null) {
            return Correlation.mid(mid);
        }
        List<String> crids = getCrids();
        if (crids != null && !crids.isEmpty()) {
            return Correlation.crid(crids.get(0));
        }
        return new Correlation(String.valueOf(hashCode()), Correlation.Type.HASH);
    }



    @Getter
    class Correlation implements Serializable {
        final String id;
        final Type type;

        public Correlation(String id, Type type) {
            this.id = id;
            this.type = type;
        }

        static Correlation mid(String mid) {
            return new Correlation(mid, Type.MID);
        }

        static Iterable<Correlation> mids(Iterable<String> mids) {
            List<MediaIdentifiable.Correlation> correlations = new ArrayList<>();
            mids.forEach(m -> correlations.add(MediaIdentifiable.Correlation.mid(m)));
            return correlations;
        }

        static Correlation crid(String crid) {
            return new Correlation(crid, Type.CRID);
        }

        enum Type {
            MID,
            CRID,
            HASH
        }
        @Override
        public String toString() {
            return type + ":" + id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Correlation that = (Correlation) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }
}
