/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

public class PortalEditorIdentifier extends OrganizationEditorIdentifier<Portal> implements Serializable {

    private static final long serialVersionUID = 0L;


    protected PortalEditorIdentifier() {
    }

    public PortalEditorIdentifier(Editor editor, Portal portal) {
        super(editor, portal);
    }

}
