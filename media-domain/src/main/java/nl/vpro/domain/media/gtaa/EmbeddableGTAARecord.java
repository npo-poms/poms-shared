/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;

/**
 *
 * @since 5.11
 */
@Setter
@MappedSuperclass
@ToString
public abstract class EmbeddableGTAARecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 7031455234466918523L;

    @Column(nullable = true, name = "gtaa_uri")
    @Getter
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    @Getter
    private GTAAStatus status;

    EmbeddableGTAARecord(String uri, GTAAStatus status) {
        this.uri = uri;
        this.status = status;
    }

    EmbeddableGTAARecord() {

    }
}
