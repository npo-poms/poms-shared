/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest.update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a user search query (document) with extra fields specifically added for Elastic Searches completion
 * suggester.
 *
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
public class QueryUpdate {

    @JsonProperty
    @NotNull
    @Size(min = 1)
    private String qid;

    @JsonProperty
    private String profile;

    @JsonProperty
    @NotNull
    private SuggestUpdate suggest;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public SuggestUpdate getSuggest() {
        return suggest;
    }

    public void setSuggest(SuggestUpdate suggest) {
        this.suggest = suggest;
    }
}
