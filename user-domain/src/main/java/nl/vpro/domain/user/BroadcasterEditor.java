/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(BroadcasterEditorIdentifier.class)
public class BroadcasterEditor extends OrganizationEditor<Broadcaster> {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    @Getter
    protected Editor editor;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    @Getter
    protected Broadcaster organization;

    @Column(nullable = false, name="isActive")
    @NotNull
    @Setter
    protected Boolean active = false;


    @Column(nullable = false, name="isEmployee")
    @NotNull
    @Setter
    private Boolean employee = false;


    protected BroadcasterEditor() {
    }

    public BroadcasterEditor(Editor editor, Broadcaster broadcaster) {
        this.editor = editor;
        this.organization = broadcaster;
    }

    public BroadcasterEditor(Editor editor, Broadcaster broadcaster, Boolean isEmployee) {
        this(editor, broadcaster);
        this.employee = isEmployee;
        this.active = isEmployee;
    }


    @Override
    public Boolean isActive() {
        return active;

    }
    public Boolean isEmployee() {
        return employee;

    }

    @Override
    public BroadcasterEditorIdentifier getId() {
        return new BroadcasterEditorIdentifier(editor, organization);
    }
}
