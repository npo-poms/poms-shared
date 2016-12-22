/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
public class TotalResult {
    private String mid;

    private Long value;

    public TotalResult() {
    }

    public TotalResult(String mid, long value) {
        this.mid = mid;
        this.value = value;
    }

    @JsonProperty
    public String getMid() {
        return mid;
    }

    @JsonProperty
    public Long getValue() {
        return value;
    }
}
