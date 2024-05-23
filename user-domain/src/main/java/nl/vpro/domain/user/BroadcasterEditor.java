/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.*;

import java.io.Serial;

import jakarta.persistence.*;

@Entity
@ToString
public class BroadcasterEditor implements OrganizationEditor<Broadcaster> {

    @Serial
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

    @Column(nullable = false)
    @Setter
    @Getter
    protected boolean active = false;


    @Column(nullable = false)
    @Setter
    @Getter
    private boolean employee = false;


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
