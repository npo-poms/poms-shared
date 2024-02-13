/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.LocalDateXmlAdapter;
import nl.vpro.xml.bind.ZeroOneBooleanAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "localizedName",
    "localizedDefinition",
    "references",
    "changeVersionDate",
    "firstVersionDate",
    "validityFlag",
    "terms"
})
public class Term implements Comparable<Term>, TermContainer {

    @XmlAttribute(name = "termID")
    protected String termId;


    @XmlElement(name = "Name")
    private List<LocalizedString> localizedName;

    @XmlElement(name = "Definition")
    private List<LocalizedString> localizedDefinition;

    @XmlElement(name = "Term")
    private List<Term> terms;

    @XmlElement(name = "Reference")
    private List<Reference> references;

    @XmlElement(name = "ChangeVersionDate")
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @Getter
    @Setter
    private LocalDate changeVersionDate;

    @XmlElement(name = "FirstVersionDate")
    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    @Getter
    @Setter
    private LocalDate firstVersionDate;

    @XmlElement(name = "ValidityFlag")
    @XmlJavaTypeAdapter(ZeroOneBooleanAdapter.class)
    @Getter
    @Setter
    private Boolean validityFlag;


    private Term parent;

    public Term(String id) {
        this.termId = id;
    }
    public Term() {
        // jaxb
    }

    public String getTermId() {
        return termId;
    }


    public String getName() {
        return getName(Locale.getDefault());
    }
    public String getName(Locale locale) {
        return LocalizedString.get(locale, localizedName);
    }

    public String getDefinition() {
        return getDefinition(Locale.getDefault());
    }

    public String getDefinition(Locale locale) {
        return LocalizedString.get(locale, localizedDefinition);
    }

    public List<Reference> getReferences() {
        if(references == null) {
            references = new ArrayList<>();
        }
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public boolean isTopTerm() {
        return parent == null || parent.getTermId().equals("3.0");
    }


    public Term getParent() {
        return parent;
    }

    public void setParent(Term parent) {
        if (this.parent != null) {
            throw new IllegalStateException();
        }
        this.parent = parent;
    }

    @Override
    public List<Term> getTerms() {
        if (terms == null) {
            terms = new ArrayList<>();
        }
        return Collections.unmodifiableList(terms);
    }

    void addTerm(Term term) {
        if (term == null) {
            terms = new ArrayList<>();
        }
        terms.add(term);
        Collections.sort(terms);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Term{");
        sb.append("termId='").append(termId).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Term o) {
        return this.getTermId().compareTo(o.getTermId());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Term term = (Term)o;

        if(!termId.equals(term.termId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return termId.hashCode();
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Term) {
            this.parent = (Term)parent;
        }
    }
}
