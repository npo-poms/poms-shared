/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(ThirdPartyEditorIdentifier.class)
public class ThirdPartyEditor extends OrganizationEditor<ThirdParty> {

    private static final long serialVersionUID = 0L;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn
    private Editor editor;

    @Id
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private ThirdParty organization;

    @Column(nullable = false)
    @NotNull
    private Boolean isActive = false;

    protected ThirdPartyEditor() {
    }

    public ThirdPartyEditor(Editor editor, ThirdParty thirdParty) {
        this.editor = editor;
        this.organization = thirdParty;
    }

    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    public ThirdParty getOrganization() {
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
    public ThirdPartyEditorIdentifier getId() {
        return new ThirdPartyEditorIdentifier(editor, organization);
    }

}
