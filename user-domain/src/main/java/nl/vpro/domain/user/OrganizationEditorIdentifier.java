/*
 * Copyright (C) 2012 All rights reserved
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
