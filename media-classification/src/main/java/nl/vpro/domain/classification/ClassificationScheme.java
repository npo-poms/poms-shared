/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@XmlType(propOrder = {
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ClassificationScheme")
@ToString
public class ClassificationScheme implements TermContainer {

    @XmlElement(name = "Term")
    private List<Term> terms;

    @XmlAttribute
    @Nullable
    private String uri;

    public ClassificationScheme(@Nullable String uri, List<Term> rootTerms) {
        this.terms = rootTerms;
        this.uri = uri;
    }

    private ClassificationScheme() {
    }

    @Nullable
    public String getUri() {
        return uri;
    }

    @Override
    public List<Term> getTerms() {
        if (terms == null) {
            terms = new ArrayList<>();
        }
        return terms;
    }

}
