package nl.vpro.domain.media.gtaa;


import lombok.*;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.persistence.StringListConverter;
import nl.vpro.validation.NoHtml;

/**
 * The GTAARecord represents a record from the GTAA service.
 */
@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = { "uri" })
public class GTAARecord implements Serializable {

    private static final long serialVersionUID = 0L;

    @Id
    @Column(columnDefinition = "varchar(255)")
    @NoHtml
    private String uri;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private GTAAStatus status;

    @NoHtml
    private String name;

    @Convert(converter = StringListConverter.class)
    private List<@NoHtml @NonNull String> scopeNotes = null;

    public GTAARecord() {
    }

    @lombok.Builder
    private GTAARecord(String uri, GTAAStatus status, String name, List<String> scopeNotes) {

        this.name = name;
        this.scopeNotes = scopeNotes != null ? scopeNotes : new ArrayList<>();
        this.uri = uri;
        this.status = status;
    }
}
