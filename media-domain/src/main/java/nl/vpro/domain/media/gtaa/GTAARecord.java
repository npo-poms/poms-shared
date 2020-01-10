package nl.vpro.domain.media.gtaa;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

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
    @Column(columnDefinition="varchar(255)")
    @NoHtml
    private String uri;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private GTAAStatus status;

    @NoHtml
    private String name;

    @Convert(converter = StringListConverter.class)
    @NoHtml
    private List<String> scopeNotes;

    public GTAARecord() {
        scopeNotes = new ArrayList<>();
    }

    @lombok.Builder
    private GTAARecord(String uri, GTAAStatus status, String name, List<String> scopeNotes) {

        this.name = name;
        this.scopeNotes = scopeNotes != null ? scopeNotes : new ArrayList<>();
        this.uri = uri;
        this.status = status;
    }
}
