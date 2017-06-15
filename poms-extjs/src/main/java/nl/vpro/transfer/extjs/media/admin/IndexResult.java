/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.admin;

public class IndexResult {

    private String name;

    private Integer results;

    public IndexResult(String name, Integer results) {
        this.name = name;
        this.results = results;
    }

    public String getName() {
        return name;
    }

    public Integer getResults() {
        return results;
    }
}
