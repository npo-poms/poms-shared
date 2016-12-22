/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.page.Page;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageTypeConstraintType")
public class PageTypeConstraint extends TextConstraint<Page> {

    public PageTypeConstraint() {
        caseHandling = CaseHandling.UPPER;
    }

    public PageTypeConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.UPPER;
    }

    @Override
    public String getESPath() {
        return "type";
    }

    @Override
    public boolean test(Page input) {
        return value.toUpperCase().equals(input.getType().name());
    }
}
