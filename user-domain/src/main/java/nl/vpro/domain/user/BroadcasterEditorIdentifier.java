/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.*;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class BroadcasterEditorIdentifier implements OrganizationEditorIdentifier<Broadcaster> {

    @Serial
    private static final long serialVersionUID = 1429763489903175742L;
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
