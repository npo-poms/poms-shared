/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.image.support;

import java.time.Instant;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Accountable;
import nl.vpro.domain.Embargo;
import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Publishable contains all items for Publishables.
 *
 * @author arne
 * @author roekoe
 * TODO duplicated in media-domain. THIS IS HORRIBLE!!
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publishableType", namespace = Xmlns.IMAGE_NAMESPACE)
public abstract class PublishableObject<T extends PublishableObject<T>> extends AbstractDomainObject<T> implements Embargo, Accountable, Identifiable<Long> {

    @ManyToOne(optional = false)
    @XmlTransient
    protected Editor createdBy;

    @ManyToOne(optional = false)
    @XmlTransient
    protected Editor lastModifiedBy;

    @Column(nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant creationDate = Instant.now();

    @Column(nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant lastModified;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStart;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStop;

    protected PublishableObject() {
    }

    public PublishableObject(long id) {
        super(id);
    }

    @Override
    public Editor getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Editor createdBy0) {
        this.createdBy = createdBy0;
    }

    @Override
    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(Editor lastModifiedBy0) {
        this.lastModifiedBy = lastModifiedBy0;
    }




    @Override
    public T setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return (T) this;

    }

    @Override
    public Embargo setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return (T) this;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;

    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStop;

    }

    @Override
    public boolean hasChanges() {
        return true;

    }

    @Override
    public void acceptChanges() {

    }

    @Override

    public Instant getLastModifiedInstant() {
        return lastModified;

    }

    @Override
    public void setLastModifiedInstant(Instant lastModified) {
        this.lastModified = lastModified;

    }

    @Override
    public Instant getCreationInstant() {
        return creationDate;

    }

    @Override
    public void setCreationInstant(Instant creationDate) {
        this.creationDate = creationDate;

    }

    @XmlAttribute(name = "urn")
    abstract public String getUrn();
}
