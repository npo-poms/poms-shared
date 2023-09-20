/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Identifiable;


public interface  OrganizationEditor<T extends Organization> extends Comparable<OrganizationEditor<T>>, Identifiable<OrganizationEditorIdentifier<T>>, Serializable {

    T getOrganization();

    Editor getEditor();

    boolean isActive();

    void setActive(boolean active);

    @Override
    OrganizationEditorIdentifier<T> getId();

    @Override
    default int compareTo(@NonNull OrganizationEditor<T> organizationEditor) {
        T org = getOrganization();
        return org == null ?
            (organizationEditor.getOrganization() == null ? 0 : -1):
            org.compareTo(organizationEditor.getOrganization());
    }
}
