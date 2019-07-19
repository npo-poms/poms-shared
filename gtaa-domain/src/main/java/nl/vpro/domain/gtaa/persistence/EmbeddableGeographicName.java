/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa.persistence;

import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Embeddable;

import nl.vpro.domain.gtaa.GTAAGeographicName;
import nl.vpro.domain.gtaa.Status;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Embeddable
@ToString
public class EmbeddableGeographicName extends EmbeddableGTAARecord implements Serializable {

    public EmbeddableGeographicName(GTAAGeographicName gtaaGeographicName) {
        super(gtaaGeographicName);
    }

    @lombok.Builder
    public EmbeddableGeographicName(String uri, Status status) {
        super(uri, status);
    }

}
