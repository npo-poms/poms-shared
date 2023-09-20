/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.page.Page;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "pagePortalConstraintType")
public class PortalConstraint extends AbstractTextConstraint<Page> {

    public PortalConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    @lombok.Builder
    public PortalConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "portal.id";
    }

    @Override
    public boolean test(Page input) {
        if (input == null || input.getPortal() == null) {
            return false;
        }
        String value = input.getPortal().getId();
        return applyValue(value);
    }
}
