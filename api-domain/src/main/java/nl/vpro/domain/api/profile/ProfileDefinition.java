/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import java.time.Instant;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.constraint.DelegatingDisplayablePredicate;
import nl.vpro.domain.constraint.DisplayablePredicate;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileDefinitionType", propOrder = {"filter"})
public class ProfileDefinition<T> implements DelegatingDisplayablePredicate<T>, Comparable<ProfileDefinition<T>> {

    @XmlTransient
    private Profile profile;

    @XmlAttribute(name = "since")
    @XmlJavaTypeAdapter(value = InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant sinceDate;

    @XmlElements({
        @XmlElement(name = "filter", namespace = Xmlns.MEDIA_CONSTRAINT_NAMESPACE, type = nl.vpro.domain.constraint.media.Filter.class),
        @XmlElement(name = "filter", namespace = Xmlns.PAGE_CONSTRAINT_NAMESPACE, type = nl.vpro.domain.constraint.page.Filter.class)
    })
    private AbstractFilter<T> filter;

    public ProfileDefinition() {
    }

    public ProfileDefinition(AbstractFilter<T> filter) {
        this.filter = filter;
    }

    public ProfileDefinition(AbstractFilter<T> filter, Instant since) {
        this.filter = filter;
        this.sinceDate = since;
    }

    public static <S> ProfileDefinition<S> of(AbstractFilter<S> filter) {
        return new ProfileDefinition<>(filter);
    }

    public boolean hasConstraint() {
        return filter != null && filter.getConstraint() != null;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getName() {
        return profile == null ? null : profile.getName();
    }

    public Instant getSince() {
        return sinceDate == null ? Instant.EPOCH : sinceDate;
    }

    public void setSince(Instant since) {
        this.sinceDate = since;
    }

    public AbstractFilter<T> getFilter() {
        return filter;
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
        return "ProfileDefinition{name='" + getName() + "', since=" + sinceDate + "}";
    }

    @Override
    public int compareTo(ProfileDefinition<T> o) {
        // newest first
        return o.getSince().compareTo(getSince());
    }

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

        if(sinceDate != null ? !sinceDate.equals(that.sinceDate) : that.sinceDate != null) {
            return false;
        }

        return getTimeStamp() != null ? getTimeStamp().equals(that.getTimeStamp()) : that.getTimeStamp() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (sinceDate != null ? sinceDate.hashCode() : 0);
        return result;
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Profile) {
            profile = (Profile)parent;
        }
    }
}
