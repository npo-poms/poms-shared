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
public class PortalEditor  implements OrganizationEditor<Portal> {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @Getter
    private PortalEditorIdentifier id;


    @MapsId("editorPrincipalId")
    @ManyToOne(optional = false)
    @Getter
    private Editor editor;

    @MapsId("organizationId")
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    private Portal organization;

    @Column(nullable = false)
    @Setter
    @Getter
    private boolean active = false;

    protected PortalEditor() {
    }

    public PortalEditor(Editor editor, Portal portal) {
        this.editor = editor;
        this.organization = portal;
        id = new PortalEditorIdentifier(editor.getPrincipalId(), portal.getId());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalEditor that = (PortalEditor) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
