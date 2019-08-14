package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class GTAAGeoLocationRecord extends GTAAConceptRecord<GTAAGeoLocationRecord> {
    private static final long serialVersionUID = 0L;

    public GTAAGeoLocationRecord() {

    }

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGeoLocationRecord(
        @lombok.NonNull URI uri,
        GTAAStatus status,
        @lombok.NonNull String name,
        String scopeNotes) {
        super(uri, status, name, scopeNotes);
    }

    public static class Builder {

        public Builder gtaaUri(String uri) {
            return uri(URI.create(uri));
        }

    }

}
