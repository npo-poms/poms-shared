/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlRootElement(name = "suggestion")
@XmlType(name = "suggestionType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Suggestion {

    @XmlValue
    @JsonProperty("text")
    private String text;

    public Suggestion() {
    }

    public Suggestion(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
