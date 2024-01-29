/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;
import nl.vpro.validation.PrePersistValidatorGroup;

@MappedSuperclass
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationType", namespace = Xmlns.MEDIA_NAMESPACE)
public abstract class Organization implements Serializable, Identifiable<String>, Comparable<Organization>, Displayable {

    @Serial
    private static final long serialVersionUID = 80331611639307640L;
    @Id
    @XmlAttribute
    protected String id;


    @Column(nullable = false)
    @NotNull(message = "displayName not set", groups = {PrePersistValidatorGroup.class})
    @Size(min = 1, max = 255, message = "0 < displayName length < 256")
    @XmlValue
    @JsonProperty("value")
    protected String displayName;

    protected Organization() {
    }

    public Organization(String id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    @Override
    @Size(min = 1, max = 255, message = "2 < id < 256")
    @jakarta.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,}", message = "type should conform to: [A-Z0-9_-]{2,}")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.toUpperCase().trim();
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        Organization that = (Organization)o;

        if(!Objects.equals(id, that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(@NonNull Organization organization) {
        return id == null ? (organization.getId() == null ? 0 : -1) :  id.toLowerCase().compareTo(organization.getId().toLowerCase());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("displayName", displayName)
            .toString();
    }
}
