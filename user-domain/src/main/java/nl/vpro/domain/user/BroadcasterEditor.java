/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(BroadcasterEditorIdentifier.class)
public class BroadcasterEditor extends OrganizationEditor<Broadcaster> {

    private static final long serialVersionUID = 1L;


    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    protected Editor editor;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    protected Broadcaster organization;

    @Column(nullable = false)
    @NotNull
    protected Boolean isActive = false;


    @Column(nullable = false)
    @NotNull
    private Boolean isEmployee = false;


    protected BroadcasterEditor() {
    }

    public BroadcasterEditor(Editor editor, Broadcaster broadcaster) {
        this.editor = editor;
        this.organization = broadcaster;
    }

    public BroadcasterEditor(Editor editor, Broadcaster broadcaster, Boolean isEmployee) {
        this(editor, broadcaster);
        this.isEmployee = isEmployee;
        this.isActive   = isEmployee;
    }

    public Boolean isEmployee() {
        return isEmployee;
    }


    @Override
    public Broadcaster getOrganization() {
        return organization;

    }

    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public BroadcasterEditorIdentifier getId() {
        return new BroadcasterEditorIdentifier(editor, organization);
    }
}
