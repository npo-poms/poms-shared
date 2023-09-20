/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
public class Suggest {

    @JsonProperty
    private String input;

    @JsonProperty
    private String output;

    @JsonProperty
    private Integer weight;

    public Suggest() {
    }

    public Suggest(Query query) {
        input = query.getId();
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
