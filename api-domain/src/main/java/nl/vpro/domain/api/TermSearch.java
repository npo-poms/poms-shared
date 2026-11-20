/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "termSearchType")
public class TermSearch extends AbstractSearch<String> {

    @Valid
    private TextMatcherList ids;

    public TermSearch() {
    }

    public TermSearch(TextMatcherList ids) {
        this.ids = ids;
    }


    @lombok.Builder(builderClassName = "Builder")
    private TermSearch(
        Match match,
        TextMatcherList _ids) {
        super(match);
        this.ids = _ids;
    }

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(ids);
    }

    @Override
    public boolean test(String s) {
        // TODO
        return true;

    }

    public static class Builder {

        public Builder ids(TextMatcherList ids) {
            return _ids(ids);
        }
        public Builder must(TextMatcher... ids) {
            return _ids(TextMatcherList.must(ids));
        }
        public Builder regexps(String ... ids) {
            TextMatcher[] array = Arrays.stream(ids).map(i -> TextMatcher.must(i, StandardMatchType.REGEX)).toArray(TextMatcher[]::new);
            return must(array);
        }
        public Builder wildcards(String ... ids) {
            TextMatcher[] array = Arrays.stream(ids).map(i -> TextMatcher.must(i, StandardMatchType.WILDCARD)).toArray(TextMatcher[]::new);
            return must(array);
        }

    }
}
