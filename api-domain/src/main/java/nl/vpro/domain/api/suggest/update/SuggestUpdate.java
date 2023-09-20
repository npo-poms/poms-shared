/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest.update;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2x
 */
public class SuggestUpdate {

    @JsonProperty
    @NotNull
    @Size(min = 1)
    @Getter
    private List<String> input;

    @JsonProperty
    @Getter
    @Setter
    private String output;

    public SuggestUpdate() {
    }

    public void setInput(@NotNull List<@NotNull String> input) {
        this.input = input;
    }
}
