/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 */
@XmlType(name = "changesType", propOrder = {"changes"})
@XmlRootElement(name = "changes")
public class Changes {

    @XmlElement(name = "change")
    @JsonProperty("changes")
    private List<MediaChange> changes;

    public List<MediaChange> getChanges() {
        return changes;
    }

    public void setChanges(List<MediaChange> changes) {
        this.changes = changes;
    }
}
