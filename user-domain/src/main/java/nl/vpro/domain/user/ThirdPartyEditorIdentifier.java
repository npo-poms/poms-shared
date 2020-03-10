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
public class ThirdPartyEditorIdentifier implements  OrganizationEditorIdentifier<ThirdParty>  {

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

}
