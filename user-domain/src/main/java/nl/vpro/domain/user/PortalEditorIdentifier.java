/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class PortalEditorIdentifier implements OrganizationEditorIdentifier<Portal> {

    @Serial
    private static final long serialVersionUID = -5316375953607168725L;
    @Column(name = "editor_principalid")
    @Getter
    private String editorPrincipalId;

    @Column(name = "organization_id")
    @Getter
    private String organizationId;

    public PortalEditorIdentifier() {
    }

    public PortalEditorIdentifier(String editor, String portal) {
        this.editorPrincipalId = editor;
        this.organizationId = portal;
    }

    @Override
    public String toString() {
        return editorPrincipalId + ":" + organizationId;
    }
}
