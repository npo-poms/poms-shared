package nl.vpro.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Getter
public class VersionedChange<T> extends Change<T> {

    Integer version;

    protected VersionedChange() {

    }
    protected VersionedChange(Instant publishDate, String id, Boolean deleted, Boolean tail, Integer version, T object) {
        super(publishDate, id, deleted, tail, false, object);
        this.version = version;
    }
}
