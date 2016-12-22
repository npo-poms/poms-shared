/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
public class CountResult {
    private String date;

    private Long value;

    public CountResult() {
    }

    public CountResult(String date, long value) {
        this.date = date;
        this.value = value;
    }

    public CountResult(CountStat stat) {
        this.date = stat.getDate().toString();
        this.value = stat.getCount();
    }

    @JsonProperty
    public String getDate() {
        return date;
    }

    @JsonProperty
    public Long getValue() {
        return value;
    }
}
