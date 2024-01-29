package nl.vpro.domain.classification.bind;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;

import nl.vpro.domain.classification.Term;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlTransient
public abstract class AbstractTermWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private String termId;

    private String name;

    public AbstractTermWrapper() {
    }

    public AbstractTermWrapper(String name) {
        this.name = name;
    }

    public AbstractTermWrapper(Term term) {
        this.termId = term.getTermId();
        this.name = term.getName(Locale.getDefault());
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String id) {
        this.termId = id;
    }
    @XmlValue
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
