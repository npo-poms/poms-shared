/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;


public interface OrganizationEditorIdentifier<T extends Organization> extends Serializable {
    String getEditorPrincipalId();

    String getOrganizationId();
}
