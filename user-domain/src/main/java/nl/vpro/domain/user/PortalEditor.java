/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class PortalEditor  implements OrganizationEditor<Portal> {

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
    @NotNull
    private Boolean isActive = false;

    protected PortalEditor() {
    }

    public PortalEditor(Editor editor, Portal portal) {
        this.editor = editor;
        this.organization = portal;
        id = new PortalEditorIdentifier(editor.getPrincipalId(), portal.getId());

    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(Boolean active) {
        this.isActive = active;
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
