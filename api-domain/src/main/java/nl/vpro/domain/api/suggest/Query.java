/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a user search query (document) with extra fields specifically added for Elastic Searches completion
 * suggester.
 *
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
public class Query {

    @JsonProperty
    private String text;

    @JsonProperty
    private String profile;

    @JsonProperty
    private Date sortDate = new Date();

    @JsonProperty
    private Suggest suggest;

    @JsonProperty
    private Long count;

    private Query() {
    }

    public Query(String text, String profile) {
        setText(text);
        this.profile = profile;
        this.suggest = new Suggest(this);
    }

    public Query(String text) {
        this(text, null);
    }

    public static String queryId(String text, String profile) {
        if(profile != null && !profile.isEmpty()) {
            return profile + "||" + text;
        }

        return text;
    }

    @JsonProperty
    public String getId() {
        return queryId(text, profile);
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = cleanUp(text);
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }

    private String cleanUp(String input) {
        return input.toLowerCase().replaceAll("[^\\p{IsAlphabetic}|\\d]+", " ").trim();
    }
}
