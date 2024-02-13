/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.constraint.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileDefinitionType", propOrder = {"filter"})
public class ProfileDefinition<T> implements DelegatingDisplayablePredicate<T>, Comparable<ProfileDefinition<T>> {

    @XmlTransient
    @Getter
    @Setter
    private Profile profile;

    @XmlElements({
        @XmlElement(name = "filter", namespace = Xmlns.MEDIA_CONSTRAINT_NAMESPACE, type = nl.vpro.domain.constraint.media.Filter.class),
        @XmlElement(name = "filter", namespace = Xmlns.PAGE_CONSTRAINT_NAMESPACE, type = nl.vpro.domain.constraint.page.Filter.class)
    })
    @Getter
    private AbstractFilter<T> filter;

    public ProfileDefinition() {
    }

    public ProfileDefinition(AbstractFilter<T> filter) {
        this.filter = filter;
    }

    public static <S> ProfileDefinition<S> of(AbstractFilter<S> filter) {
        return new ProfileDefinition<>(filter);
    }

    public boolean hasConstraint() {
        return filter != null && filter.getConstraint() != null;
    }

    public String getName() {
        return profile == null ? null : profile.getName();
    }



    @Override
    public DisplayablePredicate<T> getPredicate() {
        return filter;

    }

    public Instant getTimeStamp() {
        return profile == null ? null : profile.getTimestamp();
    }

    @Override
    public String toString() {
        return "ProfileDefinition{name='" + getName() + "'}";
    }

    @Override
    public int compareTo(ProfileDefinition<T> o) {
        return getName() == null ? (o.getName() == null ? 0 : 1) : getName().compareTo(o.getName());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ProfileDefinition that)) {
            return false;
        }

        if(getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }

        return getTimeStamp() != null ? getTimeStamp().equals(that.getTimeStamp()) : that.getTimeStamp() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Profile) {
            profile = (Profile)parent;
        }
    }
}
