/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;


/**
 * We would like to use this as base class for the varous *EditorIdentifiers. but:
 *Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sessionFactory' defined in URL [jar:file:/Users/michiel/.m2/repository/nl/vpro/media/media-backend/1.1-SNAPSHOT/media-backend-1.1-SNAPSHOT.jar!/META-INF/vpro/hibernate-config.xml]: Invocation of init method failed; nested exception is org.hibernate.AnnotationException: nl.vpro.domain.user.ThirdPartyEditorIdentifier has no persistent id property
 at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1420)
 *
 * @param <T>
 */

@MappedSuperclass
public abstract class OrganizationEditorIdentifier<T extends Organization> implements Serializable {

    private static final long serialVersionUID = 0L;

    protected Editor editor;

    protected T organization;

    protected OrganizationEditorIdentifier() {

    }

    protected OrganizationEditorIdentifier(Editor e, T o) {
        editor = e;
        organization = o;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public  T getOrganization() {
        return organization;
    }

    public void setOrganization(T organization) {
        this.organization= organization;
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        OrganizationEditorIdentifier that = (OrganizationEditorIdentifier)o;

        if(!getOrganization().equals(that.getOrganization())) {
            return false;
        }
        if(!editor.equals(that.editor)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = editor.hashCode();
        result = 31 * result + getOrganization().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "OrganizationEditorIdentifier{" +
            "editor=" + editor +
            ", organization=" + organization +
            '}';
    }

}
