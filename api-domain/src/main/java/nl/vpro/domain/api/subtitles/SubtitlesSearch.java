/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.subtitles;

import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractTextSearch;
import nl.vpro.domain.api.Matchers;
import nl.vpro.domain.api.SimpleTextMatcher;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.domain.subtitles.StandaloneCue;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesSearchType")
public class SubtitlesSearch extends AbstractTextSearch implements Predicate<StandaloneCue> {




    @Valid
    private TextMatcherList mids;

    @Valid
    private TextMatcherList types;

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

    public void setTypes(TextMatcherList types) {
        this.types = types;
    }


    public TextMatcherList getLanguages() {
        return languages;
    }

    public void setLanguages(TextMatcherList languages) {
        this.languages = languages;
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
