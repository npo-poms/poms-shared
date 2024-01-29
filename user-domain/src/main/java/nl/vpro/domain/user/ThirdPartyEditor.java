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
public class ThirdPartyEditor implements OrganizationEditor<ThirdParty> {

    @Serial
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
    @Getter
    @Setter
    private boolean active = false;

    protected ThirdPartyEditor() {
    }

    public ThirdPartyEditor(Editor editor, ThirdParty thirdParty) {
        this.editor = editor;
        this.organization = thirdParty;
        id = new ThirdPartyEditorIdentifier(editor.getPrincipalId(), thirdParty.getId());

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
