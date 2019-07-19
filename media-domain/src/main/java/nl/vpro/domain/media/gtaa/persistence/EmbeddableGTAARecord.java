/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import nl.vpro.domain.media.gtaa.Status;
import nl.vpro.domain.media.gtaa.ThesaurusObject;

/**
 *
 * @since 5.11
 */
@Embeddable
@ToString
public abstract class EmbeddableGTAARecord implements Serializable {

    @Column(nullable = true, name = "gtaa_uri")
    @Getter
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    EmbeddableGTAARecord(String uri, Status status) {
        this.uri = uri;
        this.status = status;
    }

    EmbeddableGTAARecord(ThesaurusObject thesaurusObject) {
        this.uri = thesaurusObject.getId();
        this.status = thesaurusObject.getStatus();
    }

    EmbeddableGTAARecord() {

    }
}
