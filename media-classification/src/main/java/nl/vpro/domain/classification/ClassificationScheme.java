/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

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

    public ClassificationScheme(String uri, List<Term> rootTerms) {
        this.terms = rootTerms;
        this.uri = uri;
    }

    private ClassificationScheme() {

    }

    @XmlAttribute
    private String uri;

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
