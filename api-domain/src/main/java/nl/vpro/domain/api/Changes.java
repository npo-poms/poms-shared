/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 */
@Getter
@XmlType(name = "changesType", propOrder = {"changes"})
@XmlRootElement(name = "changes")
public class Changes {

    @XmlElement(name = "change")
    @JsonProperty("changes")
    private List<MediaChange> changes;

    public void setChanges(List<MediaChange> changes) {
        this.changes = changes;
    }
}
