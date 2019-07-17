/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.Embargos;
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
public abstract class PublishableListItem implements MutableEmbargo {
    @XmlAttribute
    protected Long id;

    @XmlAttribute
    private String urn;

    @XmlAttribute
    protected Workflow workflow;

    protected Editor lastModifiedBy;

    protected Editor createdBy;

    protected Instant lastModified;

    protected Instant creationDate;

    protected Instant publishStart;

    protected Instant publishStop;

    protected Instant lastPublished;


    protected PublishableListItem() {
    }

    protected PublishableListItem(PublishableObject mediaObject) {
        this.id       = mediaObject.getId();
        this.workflow = mediaObject.getWorkflow();

        this.lastModified = mediaObject.getLastModifiedInstant();
        this.lastModifiedBy = mediaObject.getLastModifiedBy();

        this.creationDate = mediaObject.getCreationInstant();
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
        return Workflow.DELETES.contains(workflow) ? Boolean.TRUE : null;
    }

    public void setDeletedAttributeValue(Boolean deleted) {
        // Dummy JAXB setter. Why do we expose this property through XML?
    }

    public boolean isDeleted() {
        return getDeletedAttributeValue() == Boolean.TRUE;
    }

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

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public PublishableListItem setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override

    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public PublishableListItem setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }

    public Instant getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }
}
