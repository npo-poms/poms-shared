/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class ThirdPartyEditorIdentifier implements  OrganizationEditorIdentifier<ThirdParty>  {

    @Serial
    private static final long serialVersionUID = 2106652877090820172L;

    @Column(name = "editor_principalid")
    @Getter
    private String editorPrincipalId;

    @Column(name = "organization_id")
    @Getter
    private String organizationId;

    public ThirdPartyEditorIdentifier() {
    }

    public ThirdPartyEditorIdentifier(String editor, String thirdparty) {
        this.editorPrincipalId = editor;
        this.organizationId = thirdparty;
    }

    @Override
    public String toString() {
        return editorPrincipalId + ":" + organizationId;
    }

}
