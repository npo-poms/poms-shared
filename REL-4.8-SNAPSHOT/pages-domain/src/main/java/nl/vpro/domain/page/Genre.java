package nl.vpro.domain.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Genre implements Comparable<Genre>, Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Logger LOG = LoggerFactory.getLogger(Genre.class);


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
    public int compareTo(Genre o) {
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
                LOG.warn(iae.getMessage());
            }
        }
        return result;
    }
}

