/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.suggest;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents a user search query (document) with extra fields specifically added for Elastic Searches completion
 * suggester.
 *
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
@Log4j2
public class Query {

    @Getter
    @JsonProperty
    private String text;

    @Getter
    @Setter
    @JsonProperty
    private String profile;

    @JsonProperty
    @Getter
    @Setter
    // Why not Instant?
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

    public static String queryId(String text, @Nullable String profile) {
        if (profile != null && !profile.isEmpty()) {
            return profile + "||" + text;
        } else {
            log.debug("See NPA-637. This doesn't work.");
        }

        return text;
    }

    @JsonProperty
    public String getId() {
        return queryId(text, profile);
    }

    public void setText(String text) {
        this.text = cleanUp(text);
    }

    private static String cleanUp(String input) {
        return input.toLowerCase().replaceAll("[^\\p{IsAlphabetic}|\\d]+", " ").trim();
    }
}
