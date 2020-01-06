/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(PortalEditorIdentifier.class)
public class PortalEditor  extends OrganizationEditor<Portal> {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    private Editor editor;

    @Id
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private Portal organization;

    @Column(nullable = false)
    @NotNull
    private Boolean isActive = false;

    protected PortalEditor() {
    }

    public PortalEditor(Editor editor, Portal portal) {
        this.editor = editor;
        this.organization = portal;
    }

    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    public Portal getOrganization() {
        return organization;
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
    public PortalEditorIdentifier getId() {
        return new PortalEditorIdentifier(editor, organization);
    }


}
