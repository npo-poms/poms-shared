/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;


public interface OrganizationEditorIdentifier<T extends Organization> extends Serializable, Comparable<OrganizationEditorIdentifier<T>> {
    String getEditorPrincipalId();

    String getOrganizationId();

    @Override
    default int compareTo(OrganizationEditorIdentifier<T> o) {
        return toString().compareTo(o.toString());
    }
}
