/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa.persistence;

import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import nl.vpro.domain.gtaa.Status;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Embeddable
@ToString
public class EmbeddablePerson extends EmbeddableGTAARecord implements Serializable {


    @Column(name = "gtaa_knownas")
    private Boolean knownAs = false;

    protected EmbeddablePerson() {
        super();
    }

    public EmbeddablePerson(String uri, Status status) {
        super(uri, status);
    }

    @lombok.Builder
    public EmbeddablePerson(String uri, Status status, boolean knownAs) {
        super(uri, status);
        this.knownAs = knownAs;
    }

    public boolean isKnownAs() {
        return knownAs != null ? knownAs : false;
    }

    public void setKnownAs(boolean knownAs) {
        this.knownAs = knownAs;
    }
}
