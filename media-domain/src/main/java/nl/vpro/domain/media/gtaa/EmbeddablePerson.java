/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


/**
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Setter
@Embeddable
@ToString(callSuper = true)
public class EmbeddablePerson extends EmbeddableGTAARecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 2983827493130215257L;

    @Column(name = "gtaa_knownas")
    private Boolean knownAs = false;

    public EmbeddablePerson() {
        super();
    }

    public EmbeddablePerson(String uri, GTAAStatus status) {
        super(uri, status);
    }

    @lombok.Builder(builderClassName = "Builder")
    public EmbeddablePerson(String uri, GTAAStatus status, boolean knownAs) {
        super(uri, status);
        this.knownAs = knownAs;
    }

    public boolean isKnownAs() {
        return knownAs != null ? knownAs : false;
    }

}
