/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.page.Page;

/**
 * @author rico
 * @since 4.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "pageSectionConstraintType")
public class SectionConstraint extends AbstractTextConstraint<Page> {
    public SectionConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public SectionConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "portal.section.path";
    }

    @Override
    public boolean test(Page page) {
        if (page == null || page.getPortal() == null || page.getPortal().getSection() == null) {
            return false;
        }
        String value = page.getPortal().getSection().getPath();
        return applyValue(value);
    }
}
