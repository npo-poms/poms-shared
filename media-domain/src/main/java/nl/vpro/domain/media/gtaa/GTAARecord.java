package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.persistence.*;

import nl.vpro.domain.DomainObject;
import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.persistence.StringListConverter;
import nl.vpro.validation.NoHtml;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class GTAARecord extends DomainObject implements Serializable, Comparable<GTAARecord> {

    private static final long serialVersionUID = 0L;

    @Column(columnDefinition="varchar(255)")
    String uri;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private GTAAStatus status;

    @Column
    @NoHtml
    private String name;

    @Column
    @Convert(converter = StringListConverter.class)
    @NoHtml
    private List<String> scopeNotes;

    public GTAARecord() {
    }

    @lombok.Builder(builderClassName = "Builder")
    public GTAARecord(@lombok.NonNull URI uri,
                      GTAAStatus status,
                      @lombok.NonNull String name,
                      List<String> scopeNotes) {

        this.name = name;
        this.scopeNotes = scopeNotes;
        this.uri = uri.toString();
        this.status = status;
    }

    public URI getUri() {
        return URI.create(uri);
    }

    public void  setUri(URI uri) {
        this.uri = uri.toString();
    }

    @Override
    public int compareTo(@NonNull GTAARecord gtaaConceptRecord) {

        if (uri == null) {
            if (gtaaConceptRecord.getUri() == null) {
                return 0;
            }
            return -1;
        }
        return uri.compareTo(gtaaConceptRecord.uri);
    }

    public static class Builder {

        public Builder gtaaUri(String uri) {
            return uri(URI.create(uri));
        }
    }
}
