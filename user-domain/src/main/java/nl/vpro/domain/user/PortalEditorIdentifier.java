/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class PortalEditorIdentifier implements OrganizationEditorIdentifier<Portal> {

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
}
