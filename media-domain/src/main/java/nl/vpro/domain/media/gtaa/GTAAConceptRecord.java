package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.validation.NoHtml;

@MappedSuperclass
@Getter
@Setter
public abstract class GTAAConceptRecord<SELF extends GTAAConceptRecord<SELF>> implements Serializable, Comparable<SELF> {

    @Column(name = "gtaa_uri")
    @Getter
    @Id
    @lombok.NonNull
    private String uri;

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

    GTAAConceptRecord(@lombok.NonNull String uri, GTAAStatus status, @lombok.NonNull String name, String scopeNotes) {
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
        int result = uri.compareToIgnoreCase(gtaaConceptRecord.getUri());
        return result == 0 ? uri.compareTo(gtaaConceptRecord.getUri()) : result;
    }
}
