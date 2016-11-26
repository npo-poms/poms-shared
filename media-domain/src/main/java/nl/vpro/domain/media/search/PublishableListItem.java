/**
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.Date;

import javax.xml.bind.annotation.*;

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
public abstract class PublishableListItem {
    @XmlAttribute
    protected Long id;

    @XmlAttribute
    private String urn;

    @XmlAttribute
    protected Workflow workflow;

    protected Editor lastModifiedBy;

    protected Editor createdBy;

    protected Date lastModified;

    protected Date creationDate;

    protected Date publishStart;

    protected Date publishStop;

    protected Date lastPublished;


    protected PublishableListItem() {
    }

    protected PublishableListItem(PublishableObject mediaObject) {
        this.id       = mediaObject.getId();
        this.workflow = mediaObject.getWorkflow();

        this.lastModified = mediaObject.getLastModified();
        Editor lastModifier = mediaObject.getLastModifiedBy();
        this.lastModifiedBy = lastModifier;

        this.creationDate = mediaObject.getCreationDate();
        Editor creator = mediaObject.getCreatedBy();
        this.createdBy = creator;

        this.publishStart = mediaObject.getPublishStart();
        this.publishStop = mediaObject.getPublishStop();
        this.lastPublished = mediaObject.getLastPublished();

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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }

    public Date getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Date lastPublished) {
        this.lastPublished = lastPublished;
    }
}
