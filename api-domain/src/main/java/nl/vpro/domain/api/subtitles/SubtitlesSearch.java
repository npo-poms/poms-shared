/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.subtitles;

import lombok.Setter;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.api.*;
import nl.vpro.domain.subtitles.StandaloneCue;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesSearchType")
@ToString
public class SubtitlesSearch extends AbstractTextSearch<StandaloneCue> {

    @Valid
    private TextMatcherList mids;

    @Setter
    @Valid
    private TextMatcherList types;

    @Setter
    @Valid
    private TextMatcherList languages;

    public SubtitlesSearch() {

    }

    @lombok.Builder(builderClassName = "Builder")
    private SubtitlesSearch(TextMatcherList mids, TextMatcherList types, TextMatcherList languages, SimpleTextMatcher text) {
        this.text = text;
        this.mids = mids;
        this.types = types;
        this.languages = languages;
    }


    public TextMatcherList getMediaIds() {
        return mids;
    }

    public void setMediaIds(TextMatcherList mediaIds) {
        this.mids= mediaIds;
    }

    public TextMatcherList getTypes() {
        return types;
    }


    public TextMatcherList getLanguages() {
        return languages;
    }

    @Override
    public boolean hasSearches() {
        return text != null || atLeastOneHasSearches(mids, types, languages);

    }

    @Override
    public boolean test(@Nullable StandaloneCue input) {
        return input != null && (
            applyText(input) &&
                applyMediaIds(input) &&
                applyTypes(input) &&
                applyLanguages(input));
    }


    protected boolean applyTypes(StandaloneCue input) {
        if(input.getType() == null) {
            return types == null;
        }
        return Matchers.listPredicate(types).test(input.getType().name());
    }

    protected boolean applyText(StandaloneCue input) {
        if(text == null) {
            return true;
        }
        return Matchers.tokenizedPredicate(text).test(input.getContent());
    }

    protected boolean applyMediaIds(StandaloneCue input) {
        return Matchers.listPredicate(mids).test(input.getParent());
    }


    protected boolean applyLanguages(StandaloneCue input) {
        return Matchers.listPredicate(languages).test(input.getLanguage().toString());
    }

}
