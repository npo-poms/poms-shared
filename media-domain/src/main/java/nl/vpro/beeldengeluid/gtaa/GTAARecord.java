/**
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.beeldengeluid.gtaa;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Embeddable
public class GTAARecord {

    @Column(nullable = true, name = "gtaa_uri")
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
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

    public String getUri() {
        return uri;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isKnownAs() {
        return knownAs != null ? knownAs : false;
    }

    public void setKnownAs(boolean knownAs) {
        this.knownAs = knownAs;
    }

    @Override
    public String toString() {
        return uri + " (" + status + ")";
    }
}
