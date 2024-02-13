package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.classification.*;
import nl.vpro.domain.classification.bind.TermWrapper;
import nl.vpro.i18n.Displayable;
import nl.vpro.validation.GenreValidation;

/**
 *  TODO, the existence of this table is a bit silly. It just contains an id field, and nothing else.
 *
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "genreType")
@JsonPropertyOrder({"id", "terms"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
// TODO cache configuration can be put in a hibernate-config.xml. See https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch06.html
@GenreValidation
@Slf4j
public class Genre implements Displayable, Comparable<Genre>, Serializable {

    @Serial
    private static final long serialVersionUID = 433263994376700689L;

    @Id
    @Column(name = "termid", nullable = false, updatable = false)
    @Pattern(regexp = "3\\.(?:[0-9]+\\.){2,}[0-9]+") // at least 4 entries.
    private String termId;

    @Transient
    private List<TermWrapper> terms;

    public Genre() {
    }

    public Genre(String termId) {
        this.termId = termId;
        this.terms = null;
    }

    public Genre(Integer... termId) {
        this(StringUtils.join(Arrays.asList(termId), "."));
    }

    public Genre(Term term) {
        this(term.getTermId());
    }

    @XmlAttribute(name = "id")
    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
        this.terms = null;
    }

    public static Genre epg(String id) {
        return new Genre(MediaClassificationService.getTermByEpgCode(id).getTermId());
    }

    public Collection<String> getMisGenres() {
        return MediaClassificationService.getLegacyMisGenres(termId);
    }

    @Override
    public boolean display() {
        Term term = ClassificationServiceLocator.getInstance().getTerm(termId);
        do {
            if(!Boolean.TRUE.equals(term.getValidityFlag())) {
                return false;
            }
            term = term.getParent();
        } while (term != null);

        return true;
    }

    /**
     * @since 5.25
     */
    public LocalDate getFirstVersionDate() {
        Term  term =  ClassificationServiceLocator.getInstance().getTerm(termId);
        do {
            LocalDate date = term.getFirstVersionDate();
            if (date != null) {
                return date;
            }
            term = term.getParent();
        } while (term != null);
        return getChangeVersionDate();
    }


    /**
     * @since 5.25
     */
    public LocalDate getChangeVersionDate() {
        Term  term =  ClassificationServiceLocator.getInstance().getTerm(termId);
        do {
            LocalDate date = term.getChangeVersionDate();
            if (date != null) {
                return date;
            }
            term = term.getParent();
        } while (term != null);
        return null;
    }

/*
    public void setTerms(List<TermWrapper> terms) {
    }
*/

    @XmlElement(name = "term")
    @JsonProperty("terms")
    public List<TermWrapper> getTerms() {
        if(terms == null && termId != null) {
            terms = getTerms(termId);
        }
        return terms;
    }

    public String getSingleName()  {
        try {
            Term term = ClassificationServiceLocator.getInstance().getTerm(termId);
            return term != null ? term.getName(Locale.getDefault()) : "";
        } catch (TermNotFoundException tnf) {
            return "";
        }
    }

    @Override
    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        List<TermWrapper> terms = getTerms();
        if(terms != null) {
            for(TermWrapper wrapper : terms) {
                if(builder.length() > 0) {
                    builder.append(" - ");
                }
                builder.append(wrapper.getName());
            }
        }
        return builder.toString();
    }


    public static SortedSet<Genre> valueOf(Collection<String> termIds) {
        SortedSet<Genre> result = new TreeSet<>();
        if (termIds != null) {
            for (String id : termIds) {
                if (StringUtils.isNotBlank(id)) {
                    result.add(new Genre(id));
                }
            }
        }
        return result;
    }


    public static SortedSet<Genre> valueOfEpg(Collection<EpgGenreType> types) {
        SortedSet<Genre> result = new TreeSet<>();
        for(EpgGenreType epgGenreType : types) {
            result.add(new Genre(MediaClassificationService.getTermByEpgCode(epgGenreType.name().substring(1)).getTermId()));
        }
        return result;
    }


    public static Collection<Genre> valueOfMis(MisGenreType... id) {
        SortedSet<Genre> result = new TreeSet<>();
        for(MisGenreType mis : id) {
            List<Term> termsByMisCodes = MediaClassificationService.getTermsByMisGenreType(mis.name());
            for(Term termsByMisCode : termsByMisCodes) {
                result.add(new Genre(termsByMisCode.getTermId()));
            }
        }
        return result;
    }


    public static SortedSet<Genre> valueOfMis(Collection<MisGenreType> id) {
        SortedSet<Genre> result = new TreeSet<>();
        for(MisGenreType mis : id) {
            List<Term> termsByMisCodes = MediaClassificationService.getTermsByMisGenreType(mis.name());
            for(Term termsByMisCode : termsByMisCodes) {
                result.add(new Genre(termsByMisCode.getTermId()));
            }
        }
        return result;
    }


    public static Collection<Genre> terms(Collection<Term> terms) {
        Collection<Genre> result = new ArrayList<>();
        for(Term t : terms) {
            result.add(new Genre(t.getTermId()));
        }
        return result;
    }


    @Override
    public int compareTo(@NonNull Genre o) {
        try {
            return termId == null ? this.hashCode() - o.hashCode() : new TermId(termId).compareTo(new TermId(o.termId));
        } catch (NumberFormatException nfe) {
            return Objects.compare(termId, o.termId, Comparator.nullsFirst(String::compareTo));
        }
    }

    @Override
    public String toString() {
        return "Genre{termId='" + termId + "', " + "display='" + getDisplayName() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Genre genre = (Genre)o;

        if(termId == null) {
            return genre.termId == null;
        }

        return termId.equals(genre.termId);
    }

    @Override
    public int hashCode() {
        return termId != null ? termId.hashCode() : 0;
    }

    private static ArrayList<TermWrapper> getTerms(String termId) {
        final ArrayList<TermWrapper> result = new ArrayList<>();
        if(termId != null) {
            Term term;
            try {
                term = ClassificationServiceLocator.getInstance().getTerm(termId);
            } catch (TermNotFoundException iae) {
                log.error("No such term " + termId);
                return result;
            }
            result.add(new TermWrapper(term));
            Term parent = term.getParent();
            while(parent != null && !parent.isTopTerm()) {
                result.add(0, new TermWrapper(parent));
                parent = parent.getParent();
            }
        }
        return result;
    }
}

