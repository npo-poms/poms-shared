/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class BroadcasterEditorIdentifier implements OrganizationEditorIdentifier<Broadcaster> {

    @Column(name = "editor_principalid")
    @Getter
    @Setter
    private String editorPrincipalId;

    @Column(name = "organization_id")
    @Getter
    @Setter
    private String organizationId;

    public BroadcasterEditorIdentifier() {
    }

    public BroadcasterEditorIdentifier(String editor, String broadcaster) {
        this.editorPrincipalId = editor;
        this.organizationId = broadcaster;
    }

    @Override
    public String toString() {
        return editorPrincipalId + ":" + organizationId;
    }


}
