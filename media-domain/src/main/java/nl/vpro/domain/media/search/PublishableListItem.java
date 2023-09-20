/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.time.Instant;

import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.*;
import nl.vpro.domain.media.CollectionUtils;
import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.Editor;

/**
 * @author Roelof Jan Koekoek
 * @since 3.4
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "item")
@XmlType(
    name = "publishableListItem")
public abstract class PublishableListItem<S extends PublishableListItem<S>> implements MutableEmbargo<S>, Identifiable<Long> {
    @XmlAttribute
    protected Long id;

    @XmlAttribute
    private String urn;

    @XmlAttribute
    protected Workflow workflow;

    protected Editor lastModifiedBy;

    protected Editor createdBy;

    protected Instant lastModifiedInstant;

    protected Instant creationInstant;

    protected Instant publishStart;

    protected Instant publishStop;

    protected Instant lastPublished;


    protected PublishableListItem() {
    }

    protected PublishableListItem(PublishableObject<?> mediaObject) {
        this.id       = mediaObject.getId();
        this.workflow = mediaObject.getWorkflow();

        this.lastModifiedInstant = mediaObject.getLastModifiedInstant();
        this.lastModifiedBy = mediaObject.getLastModifiedBy();

        this.creationInstant = mediaObject.getCreationInstant();
        this.createdBy = mediaObject.getCreatedBy();

        Embargos.copy(mediaObject, this);

        this.lastPublished = mediaObject.getLastPublishedInstant();

    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @XmlAttribute(name = "deleted")
    public Boolean getDeletedAttributeValue() {
        return CollectionUtils.inCollection(Workflow.DELETES, workflow) ? Boolean.TRUE : null;
    }

    public void setDeletedAttributeValue(Boolean deleted) {
        // Dummy JAXB setter. Why do we expose this property through XML?
    }

    public boolean isDeleted() {
        return getDeletedAttributeValue() == Boolean.TRUE;
    }

    @Override
    public Long getId() {
        if (id == null) {
            if (urn != null) {
                int colon = urn.lastIndexOf(':');
                return Long.parseLong(urn.substring(colon + 1));
            }
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Editor lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Editor getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Editor createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getLastModifiedInstant() {
        return lastModifiedInstant;
    }

    public void setLastModifiedInstant(Instant lastModifiedInstant) {
        this.lastModifiedInstant = lastModifiedInstant;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public void setCreationInstant(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public S setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return (S) this;
    }

    @Override

    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public S setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return (S) this;
    }

    public Instant getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }
}
