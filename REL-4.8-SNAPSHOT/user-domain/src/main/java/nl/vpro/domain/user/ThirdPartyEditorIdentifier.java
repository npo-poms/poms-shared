/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ThirdPartyEditorIdentifier extends OrganizationEditorIdentifier<ThirdParty> implements Serializable {

    private static final long serialVersionUID = 0L;


    protected ThirdPartyEditorIdentifier() {
    }

    public ThirdPartyEditorIdentifier(Editor editor, ThirdParty thirdparty) {
        super(editor, thirdparty);
    }

}
