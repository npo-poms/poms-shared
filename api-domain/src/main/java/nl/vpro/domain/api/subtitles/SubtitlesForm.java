/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.subtitles;

import java.util.function.Predicate;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.api.Form;
import nl.vpro.domain.api.FormUtils;
import nl.vpro.domain.subtitles.StandaloneCue;

/**
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlRootElement(name = "subtitlesForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesFormType",
    propOrder = {
        "searches"})
public class SubtitlesForm implements Form, Predicate<StandaloneCue> {



    @XmlElement
    @Valid
    private SubtitlesSearch searches;


    public SubtitlesForm() {

    }
    @lombok.Builder
    private SubtitlesForm(SubtitlesSearch searches) {
        this.searches = searches;
    }


    public SubtitlesSearch getSearches() {
        return searches;
    }

    public void setSearches(SubtitlesSearch searches) {
        this.searches = searches;
    }

    @Override
    public boolean test(StandaloneCue standaloneCue) {
        return searches == null || searches.test(standaloneCue);
    }

    @Override
    public boolean isHighlight() {
        return false;

    }

    @Override
    public boolean isFaceted() {
        return false;
    }

    @Override
    public String getText() {
        return FormUtils.getText(searches);

    }
}
