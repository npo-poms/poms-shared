/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
@AllArgsConstructor
public class Suggestion {

    @XmlValue
    @JsonProperty("text")
    private String text;

    public Suggestion() {
    }


}
