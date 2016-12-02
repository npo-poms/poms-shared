/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class BroadcasterEditorIdentifier extends OrganizationEditorIdentifier<Broadcaster> implements Serializable {


    protected BroadcasterEditorIdentifier() {
    }

    public BroadcasterEditorIdentifier(Editor editor, Broadcaster broadcaster) {
        super(editor, broadcaster);
    }

}
