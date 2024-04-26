/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.*;
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
    @Setter
    @XmlAttribute
    protected Long id;

    @Setter
    @Getter
    @XmlAttribute
    private String urn;

    @Setter
    @Getter
    @XmlAttribute
    protected Workflow workflow;

    @Setter
    @Getter
    protected Editor lastModifiedBy;

    @Setter
    @Getter
    protected Editor createdBy;

    @Setter
    @Getter
    protected Instant lastModifiedInstant;

    @Setter
    @Getter
    protected Instant creationInstant;

    protected Instant publishStart;

    protected Instant publishStop;

    @Setter
    @Getter
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


}
