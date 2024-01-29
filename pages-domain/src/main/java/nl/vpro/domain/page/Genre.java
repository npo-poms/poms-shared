package nl.vpro.domain.page;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.classification.TermId;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@XmlType(name = "genreType")
@XmlRootElement(name = "genre")
@JsonPropertyOrder({"id", "terms", "displayName"})
@Slf4j
public class Genre implements Comparable<Genre>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @Pattern(regexp = "3\\.(?:[0-9]+\\.){2,}[0-9]+") // at least 4 entries.
    private String termId;

    @Transient
    private List<TermWrapper> terms;

    public static Genre of(String termId) {
        return of(new Term(termId));
    }

    public static Genre of(Term term) {
        return new Genre(term);
    }

    public Genre(Term term) {
        this.termId = term.getTermId();
        this.terms = getTerms(term);
    }

    public Genre() {
    }

    @XmlAttribute(name = "id")
    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
        this.terms = null;
    }


    @XmlElement(name = "term", namespace = Xmlns.PAGE_NAMESPACE)
    @JsonProperty("terms")
    public List<TermWrapper> getTerms() {
        if (terms == null) {
            // To help jaxb
            terms = new ArrayList<>();
        }
        return terms;
    }

    @XmlAttribute
    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        List<TermWrapper> terms = getTerms();
        if (terms != null) {
            for (TermWrapper wrapper : terms) {
                if (builder.length() > 0) {
                    builder.append(" - ");
                }
                builder.append(wrapper.getName());
            }
        }
        return builder.toString();
    }

    public void setDisplayName(String displayName) {
        // ignored
    }

    @Override
    public int compareTo(@NonNull Genre o) {
        return termId == null ? this.hashCode() - o.hashCode() : new TermId(termId).compareTo(new TermId(o.termId));
    }

    @Override
    public String toString() {
        return "Genre{termId='" + termId + "', " + "display='" + "'" + getDisplayName() + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Genre genre = (Genre) o;

        if (termId == null) {
            return genre.termId == null;
        }

        return termId.equals(genre.termId);
    }

    @Override
    public int hashCode() {
        return termId != null ? termId.hashCode() : 0;
    }

    private static ArrayList<TermWrapper> getTerms(Term term) {
        ArrayList<TermWrapper> result = new ArrayList<>();
        if (term != null) {
            try {
                result.add(new TermWrapper(term));
                Term parent = term.getParent();
                while (parent != null && !parent.isTopTerm()) {
                    result.add(0, new TermWrapper(parent));
                    parent = parent.getParent();
                }
            } catch (IllegalArgumentException iae) {
                log.warn(iae.getMessage());
            }
        }
        return result;
    }
}

