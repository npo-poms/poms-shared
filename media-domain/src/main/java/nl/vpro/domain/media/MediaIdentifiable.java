package nl.vpro.domain.media;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.util.locker.ObjectLocker;

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
    default List<String> getCrids() {
        return Collections.emptyList();
    }

    @JsonIgnore
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

    @Override
    @JsonIgnore
    default String getCorrelationId() {
        return Optional.ofNullable(getCorrelation()).map(c -> c.id).orElse(null);
    }

    /**
     * The correlation of this {@code MediaIdentifiable} is the best id currently available.
     * Oftentimes that will be the {@link #getMid()}, but in certain situation that is not (yet) available, and it may fall back
     * to (the first) {@link #getCrids()}.
     */
    @Override
    @JsonIgnore
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


    /**
     * A 'correlation' for a {@code MediaIdentifiable} is a {@code mid}, or a {@code crid}
     */
    @Getter
    class Correlation implements Serializable, ObjectLocker.DefinesType {
        @Serial
        private static final long serialVersionUID = -8973548054279104343L;

        final String id;
        final Type type;

        public Correlation(@NonNull String id, @NonNull Type type) {
            this.id = id;
            this.type = type;
        }

        private Correlation() {
            this.id = null;
            this.type = Type.NO_LOCK;
        }

        public static Correlation mid(String mid) {
            if (mid == null) {
                return null;
            } else {
                return new Correlation(mid, Type.MID);
            }
        }

        public static Correlation crid(String crid) {
            if (crid == null) {
                return null;
            } else {
                return new Correlation(crid, Type.CRID);
            }
        }

        public enum Type {
            MID,
            CRID,
            HASH,
            NO_LOCK
        }
        @Override
        public String toString() {
            return type + ":" + id;
        }

        public static final Correlation NO_LOCK = new Correlation();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Correlation that = (Correlation) o;

            if (!Objects.equals(id, that.id)) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result = id == null ? 0 : id.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
