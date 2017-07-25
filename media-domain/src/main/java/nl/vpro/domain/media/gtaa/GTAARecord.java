/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Embeddable
@ToString
public class GTAARecord {

    @Column(nullable = true, name = "gtaa_uri")
    @Getter
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    @Column(name = "gtaa_knownas")
    private Boolean knownAs = false;

    protected GTAARecord() {
    }

    public GTAARecord(String uri, Status status) {
        this.uri = uri;
        this.status = status;
    }

    public GTAARecord(String uri, Status status, boolean knownAs) {
        this.uri = uri;
        this.status = status;
        this.knownAs = knownAs;
    }

    public boolean isKnownAs() {
        return knownAs != null ? knownAs : false;
    }

    public void setKnownAs(boolean knownAs) {
        this.knownAs = knownAs;
    }
}
