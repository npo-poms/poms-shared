package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.persistence.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.persistence.StringListConverter;
import nl.vpro.validation.NoHtml;

@MappedSuperclass
@Getter
@Setter
public abstract class GTAAConceptRecord<SELF extends GTAAConceptRecord<SELF>> implements Serializable, Comparable<SELF> {

    @Column(columnDefinition="varchar(255)", length = 255)
    //@Convert(converter = URIConverter.class)
    @Id
    String uri;

    @Column(nullable = true, length = 30)
    @Enumerated(EnumType.STRING)
    private GTAAStatus status;

    @Column
    @NoHtml
    private String name;

    @Column
    @Convert(converter = StringListConverter.class)
    @NoHtml
    private List<String> scopeNotes;

    GTAAConceptRecord() {}

    GTAAConceptRecord(
        @lombok.NonNull URI uri, GTAAStatus status, @lombok.NonNull String name,
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
    public int compareTo(@NonNull SELF gtaaConceptRecord) {
        if (uri == null) {
            if (gtaaConceptRecord.getUri() == null) {
                return 0;
            }
            return -1;
        }
        return uri.compareTo(gtaaConceptRecord.uri);
    }
}
