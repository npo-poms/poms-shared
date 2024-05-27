package nl.vpro.domain.media;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.subtitles.*;
import nl.vpro.xml.bind.LocaleAdapter;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;


/**
 * Sub-field of {@link MediaObject} indicating whether subtitles for a certain language are available
*/
@XmlType(name="availableSubtitlesType")
@XmlAccessorType(XmlAccessType.NONE)
@Entity
public class AvailableSubtitles implements Identifiable<Long>, Comparable<AvailableSubtitles>, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    private static final Comparator<AvailableSubtitles> COMPARATOR  =
        nullsLast(
            comparing(AvailableSubtitles::getLanguage, comparing(Locale::toLanguageTag))
                .thenComparing(AvailableSubtitles::getType, Comparator.naturalOrder()));

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Getter
    private String mid;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlAttribute
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Schema(implementation = String.class, type="string")
    @Column
    private Locale language;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column
    private SubtitlesType type;


    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private SubtitlesWorkflow workflow = null;

    public AvailableSubtitles() {

    }

    public AvailableSubtitles(Locale language, SubtitlesType type) {
        this.language = language;
        this.type = type;
    }

    public static AvailableSubtitles published(Locale language, SubtitlesType type) {
        return AvailableSubtitles.builder()
            .language(language)
            .type(type)
            .workflow(SubtitlesWorkflow.PUBLISHED)
            .build();
    }

    @lombok.Builder
    private AvailableSubtitles(String mid, Locale language, SubtitlesType type, SubtitlesWorkflow workflow) {
        this.mid = mid;
        this.language = language;
        this.type = type;
        this.workflow = workflow == null ? SubtitlesWorkflow.FOR_PUBLICATION : workflow;

    }

    @Override
    public int compareTo(AvailableSubtitles o) {
        return COMPARATOR.compare(this, o);
    }

    @XmlAttribute(name = "workflow")
    protected SubtitlesWorkflow getWorkflow_() {
        return workflow == SubtitlesWorkflow.PUBLISHED ? null : workflow;
    }


    protected void setWorkflow_(SubtitlesWorkflow workflow) {
        this.workflow = workflow == null ? SubtitlesWorkflow.PUBLISHED : workflow;
    }

    @Override
    public String toString() {
        return language + ":" + type + (workflow != null   ? (":"+ workflow) : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof AvailableSubtitles that) {
            if (id != null && that.id != null) {
                return Objects.equals(id, that.id);
            } else {
                return Objects.equals(mid, that.mid) && language.equals(that.language) && type == that.type;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = mid == null ? 0 : mid.hashCode();
        result = 31 * result + language.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
