package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.validation.NoHtml;

@MappedSuperclass
@Getter
@Setter
public abstract class GTAAConceptRecord<SELF extends GTAAConceptRecord<SELF>> implements Serializable, Comparable<SELF> {

    @Column(name = "gtaa_uri", columnDefinition="varchar(255)", length = 255)
    @Getter
    @Id
    @lombok.NonNull
    private URI uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    @Setter
    private GTAAStatus status;

    @NoHtml
    @XmlElement
    @lombok.NonNull
    private String name;

    @NoHtml
    @XmlElement
    private String scopeNotes;

    GTAAConceptRecord() {}

    GTAAConceptRecord(@lombok.NonNull URI uri, GTAAStatus status, @lombok.NonNull String name, String scopeNotes) {
        this.name = name;
        this.scopeNotes = scopeNotes;
        this.uri = uri;
        this.status = status;
    }


    @Override
    public int compareTo(@NonNull SELF gtaaConceptRecord) {
        if (uri == null) {
            if (gtaaConceptRecord.getUri() == null) {
                return 0;
            }
            return -1;
        }
        return uri.compareTo(gtaaConceptRecord.getUri());
    }
}
