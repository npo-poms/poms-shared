/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest.update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2x
 */
public class SuggestUpdate {

    @JsonProperty
    @NotNull
    @Size(min = 1)
    private List<String> input;

    @JsonProperty
    private String output;

    public SuggestUpdate() {
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
