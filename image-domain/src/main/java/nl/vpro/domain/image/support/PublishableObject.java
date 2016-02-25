/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.image.support;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.user.Editor;

/**
 * Publishable contains all items for Publishables.
 *
 * @author arne
 * @author roekoe
 * @version $Id$
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "publishableType", namespace = "urn:vpro:image:2009")
public abstract class PublishableObject<T extends PublishableObject> extends AbstractDomainObject<T> implements Modifiable<T> {

    @ManyToOne(optional = false)
    @XmlTransient
    protected Editor createdBy;

    @ManyToOne(optional = false)
    @XmlTransient
    protected Editor lastModifiedBy;

    @Column(nullable = false)
    protected Date creationDate = new Date();

    @Column(nullable = false)
    protected Date lastModified;

    protected Date publishStart = new Date();

    protected Date publishStop;

    protected PublishableObject() {
    }

    public PublishableObject(long id) {
        super(id);
    }

    @XmlTransient
    public Editor getCreatedBy() {
        return createdBy;
    }

    public T setCreatedBy(Editor createdBy0) {
        this.createdBy = createdBy0;
        return (T)this;
    }

    @XmlTransient
    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    public T setLastModifiedBy(Editor lastModifiedBy0) {
        this.lastModifiedBy = lastModifiedBy0;
        return (T)this;
    }

    @XmlAttribute
    public Date getLastModified() {
        return lastModified;
    }

    public T setLastModified(Date lastModified0) {
        this.lastModified = lastModified0;
        return (T)this;
    }

    @XmlAttribute
    public Date getCreationDate() {
        return creationDate;
    }

    public T setCreationDate(Date creationDate0) {
        this.creationDate = creationDate0;
        return (T)this;
    }

    @XmlAttribute
    public Date getPublishStart() {
        return publishStart;
    }

    public PublishableObject setPublishStart(Date publishStart0) {
        this.publishStart = publishStart0;
        return this;
    }

    @XmlAttribute
    public Date getPublishStop() {
        return publishStop;
    }

    public PublishableObject setPublishStop(Date publishStop0) {
        this.publishStop = publishStop0;
        return this;
    }

    @XmlAttribute(name = "urn")
    abstract public String getUrn();
}