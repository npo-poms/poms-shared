/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import nl.vpro.domain.Identifiable;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class OrganizationEditor<T extends Organization>
    implements Comparable<OrganizationEditor<T>>, Identifiable<OrganizationEditorIdentifier<T>>, Serializable {

    public abstract T getOrganization();

    public abstract Editor getEditor();

    public abstract Boolean isActive();

    public abstract void setActive(Boolean active);

    @Override
    public abstract OrganizationEditorIdentifier<T> getId();

    @Override
    public int compareTo(@NonNull OrganizationEditor<T> organizationEditor) {
        T org = getOrganization();
        return org == null ?
            (organizationEditor.getOrganization() == null ? 0 : -1):
            org.compareTo(organizationEditor.getOrganization());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BroadcasterEditor");
        sb.append("{organization=").append(getOrganization().getId());
        sb.append(", editor=").append(getEditor().getPrincipalId());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganizationEditor that = (OrganizationEditor)o;

        return getEditor().equals(that.getEditor()) && getOrganization() != null ? getOrganization().equals(that.getOrganization()) : that.getOrganization() == null;
    }

    @Override
    public int hashCode() {
        int result = getEditor().hashCode();
        if(getOrganization() != null) {
            result = 31 * result + getOrganization().hashCode();
        }
        return result;
    }
}
