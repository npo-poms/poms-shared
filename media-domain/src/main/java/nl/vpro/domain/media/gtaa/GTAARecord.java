package nl.vpro.domain.media.gtaa;


import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

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

    @NoHtml
    // not applying StringListConverter here, because JPAMetaModelEntityProcessor get's confused.
    private String scopeNotes = null;

    public GTAARecord() {
    }

    @lombok.Builder
    private GTAARecord(String uri, GTAAStatus status, String name, List<String> scopeNotes) {

        this.name = name;
        this.scopeNotes = scopeNotes != null ? convertToDatabaseColumn(scopeNotes) : "";
        this.uri = uri;
        this.status = status;
    }

    public List<String> getScopeNotes() {
        return convertToEntityAttribute(scopeNotes);
    }

    public void setScopeNotes(List<String> scopeNotes) {
        this.scopeNotes = convertToDatabaseColumn(scopeNotes);
    }

    private static final String SPLIT_CHAR = "\t";

    private static  String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        return String.join(SPLIT_CHAR, stringList);
    }

    private static  List<String> convertToEntityAttribute(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        return Arrays.asList(string.split(SPLIT_CHAR));
    }
}
