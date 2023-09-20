/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;

/**
 *
 * @since 5.11
 */
@Embeddable
@MappedSuperclass
@ToString
public abstract class EmbeddableGTAARecord implements Serializable, Cloneable {

    @Column(nullable = true, name = "gtaa_uri")
    @Getter
    @Setter
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private GTAAStatus status;

    EmbeddableGTAARecord(String uri, GTAAStatus status) {
        this.uri = uri;
        this.status = status;
    }

    EmbeddableGTAARecord() {

    }
}
