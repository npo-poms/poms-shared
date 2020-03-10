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
public class BroadcasterEditor implements OrganizationEditor<Broadcaster> {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Getter
    private BroadcasterEditorIdentifier id;

    @ManyToOne(optional = false)
    @MapsId("editorPrincipalId")
    @Getter
    protected Editor editor;

    @ManyToOne(optional = false)
    @MapsId("organizationId")
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
        id = new BroadcasterEditorIdentifier(editor.getPrincipalId(), broadcaster.getId());
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BroadcasterEditor that = (BroadcasterEditor) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
