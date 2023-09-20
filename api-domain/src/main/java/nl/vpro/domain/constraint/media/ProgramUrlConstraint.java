/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.*;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import nl.vpro.domain.constraint.AbstractTextConstraint;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "programUrlConstraintType")
@XmlDocumentation("Constraints on the program url field of locations")
public class ProgramUrlConstraint extends AbstractTextConstraint<MediaObject> {

    private boolean exact;

    public ProgramUrlConstraint() {
        caseHandling = CaseHandling.ASIS;
        exact = true;
    }

    public ProgramUrlConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
        exact = true;
    }


    @Override
    public String getESPath() {
        return "locations.programUrl";
    }

    public Boolean getExact() {
        return exact;
    }

    @XmlAttribute
    public void setExact(Boolean exact) {
        this.exact = exact != null ? exact : false;
    }

    @Override
    public boolean test(MediaObject input) {
        for(Location location : input.getLocations()) {
            if(exact && location.getProgramUrl().contains(value)) {
                return true;
            } else if(location.getProgramUrl().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
