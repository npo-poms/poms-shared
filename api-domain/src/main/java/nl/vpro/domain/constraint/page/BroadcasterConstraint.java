/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "broadcasterConstraintType")
public class BroadcasterConstraint extends AbstractTextConstraint<Page> {

    public BroadcasterConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    @lombok.Builder
    public BroadcasterConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    @Size(min = 1, max = 255, message = "2 < id < 256")
    @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,}", message = "type should conform to: [A-Z0-9_-]{2,}")
    public void setValue(String v) {
        super.setValue(v);
    }

    @Override
    public String getESPath() {
        return "broadcasters.id";
    }

    @Override
    public boolean test(Page input) {
        for(Broadcaster broadcaster : input.getBroadcasters()) {
            if(value.equals(broadcaster.getDisplayName())) {
                return true;
            }
        }
        return false;
    }
}
