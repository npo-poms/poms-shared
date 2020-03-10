/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;



@Entity
public class ThirdPartyEditor implements OrganizationEditor<ThirdParty> {

    private static final long serialVersionUID = 0L;

    @EmbeddedId
    @Getter
    private ThirdPartyEditorIdentifier id;

    @MapsId("editorPrincipalId")
    @ManyToOne(optional = false)
    @Getter
    private Editor editor;

    @MapsId("organizationId")
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Getter
    private ThirdParty organization;

    @Column(nullable = false)
    @NotNull
    private Boolean isActive = false;

    protected ThirdPartyEditor() {
    }

    public ThirdPartyEditor(Editor editor, ThirdParty thirdParty) {
        this.editor = editor;
        this.organization = thirdParty;
        id = new ThirdPartyEditorIdentifier(editor.getPrincipalId(), thirdParty.getId());

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

        ThirdPartyEditor that = (ThirdPartyEditor) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
