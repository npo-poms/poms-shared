/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
@XmlRootElement(name = "suggestResult")
@XmlType(name = "suggestResultType")
public class SuggestResult extends Result<Suggestion> {

    public SuggestResult() {
    }

    public static SuggestResult emptyResult() {
        return new SuggestResult(Collections.emptyList(), 0, 0);
    }

    public SuggestResult(List<Suggestion> list, Integer max, long listSizes) {
        super(list, null, max, Total.equalsTo(listSizes));
    }
}
