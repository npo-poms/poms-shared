/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;
import java.util.zip.CRC32;

import javax.persistence.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Accountable;
import nl.vpro.domain.Embargo;
import nl.vpro.domain.EmbargoDeprecated;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.validation.PomsValidatorGroup;
import nl.vpro.validation.Publishable;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Publishable contains all items for Publishables, this is largely the abstract implemention of {@link Accountable} and {@link Embargo}.
 *
 * @author arne
 * @author roekoe
 * TODO duplicated in image-domain
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@Publishable(groups = {PomsValidatorGroup.class})
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publishableObjectType", namespace = Xmlns.SHARED_NAMESPACE)
//@XmlTransient
@Slf4j
public abstract class PublishableObject extends DomainObject implements Accountable, EmbargoDeprecated {

    public static final String DELETED_FILTER = "deletedFilter";
    public static final String INVERSE_DELETED_FILTER = "inverseDeletedFilter";
    public static final String PUBLICATION_FILTER = "publicationFilter";
    public static final String INVERSE_PUBLICATION_FILTER = "inversePublicationFilter";
    public static final String EMBARGO_FILTER = "embargoFilter";
    public static final String INVERSE_EMBARGO_FILTER = "inverseEmbargoFilter";

    @Column(nullable = false)
    protected Instant creationDate = Instant.now();

    @Column(nullable = false)
    protected Instant lastModified;

    @ManyToOne(optional = false)
    @JoinColumn(name = "createdby_principalid")
    protected Editor createdBy;


    @ManyToOne(optional = false)
    @JoinColumn(name = "lastmodifiedby_principalid")
    protected Editor lastModifiedBy;

    @Column(nullable = true)
    protected Instant publishStart;

    @Column(nullable = true)
    protected Instant  publishStop;

    @Column(nullable = true)
    protected Instant lastPublished;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Workflow workflow = Workflow.FOR_PUBLICATION;



    @Column(nullable = true)
    private Long crc32;

    protected PublishableObject(PublishableObject source) {
        this.creationDate = source.creationDate;
        this.createdBy = source.createdBy;
        this.lastModified = source.lastModified;
        this.lastModifiedBy = source.lastModifiedBy;
        this.publishStart = source.publishStart;
        this.publishStop = source.publishStop;
        this.lastPublished = source.lastPublished;
        this.workflow = source.workflow;
    }



    /**
     * Checks if this object is changed compared to the persistant version
     * by calculating the runtime CRC32 and compare it to the stored CRC
     */
    @Override
    public boolean hasChanges() {
        long currentState;

        if(this.id == null || this.crc32 == null) {
            return true; // we are always dirty if we don't have an id or crc
        }

        currentState = calcCRC32().getValue();

        if(currentState != this.crc32) {
            // we are dirty
            return true;
        }

        // we are unchanged
        return false;
    }

    /**
     * Accept the mutations on this object by recalculating crc32.
     * If hasChanges() is called after this, it always returns false
     */
    @Override
    public void acceptChanges() {
        this.crc32 = calcCRC32().getValue();
    }

    public Long getHash() {
        return this.crc32;
    }

    private byte[] prepareForCRCCalc(byte[] data, String encoding) {
        try {
            String dataAsString = new String(data, encoding);
            // FOR_DELETION is rewritten to DELETED so that it IS detected as a change
            dataAsString = dataAsString.replaceAll("workflow=\"FOR DELETION", "workflow=\"DELETED\"");

            // filter stuff we DONT want in here
            dataAsString = dataAsString.replaceAll("workflow=\".+?(?!DELETED)\"", "");
            dataAsString = dataAsString.replaceAll("\\s+publishDate=\".+?\"\\s+", " ");   // neither is this
            dataAsString = dataAsString.replaceAll("\\s+lastModified=\".+?\"\\s+", " "); // this should not matter, but last modified may be set too late...

            // NOTE: all remaining fields we don't want are set to XMLTransient and therefore won't get copied into this XML to start with

            return dataAsString.getBytes();

        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }


    protected CRC32 calcCRC32() {
        ByteArrayOutputStream baos = null;
        OutputStreamWriter writer = null;

        try {
            byte[] serialized;
            CRC32 crc32Local = new CRC32();
            baos = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(baos);

            JAXB.marshal(this, writer);
            serialized = prepareForCRCCalc(baos.toByteArray(), writer.getEncoding());

            crc32Local.reset();
            crc32Local.update(serialized);
            if(log.isDebugEnabled()) {
                log.debug(new String(serialized));
            }
            return crc32Local;

        } finally {

            if(baos != null) {
                try {
                    baos.close();
                } catch(Exception ignored) {
                }
            }
            if(writer != null) {
                try {
                    writer.close();
                } catch(Exception ignored) {
                }
            }
        }
    }

    public PublishableObject() {
    }

    public PublishableObject(long id) {
        this.id = id;
    }

    public boolean isActivation() {
        return isPublishable() && Workflow.WITH_MEDIA_ACTIVATION.contains(workflow);
    }

    public boolean isDeactivation() {
        if(Workflow.PARENT_REVOKED == workflow
            || Workflow.FOR_DELETION == workflow) {
            return true;
        }

        return Workflow.PUBLISHED == workflow && isRevocable();

    }

    public boolean isPublishable() {
        if(Workflow.MERGED.equals(workflow)) {
            return true;
        }

        if(Workflow.FOR_PUBLICATION.equals(workflow)
            || Workflow.FOR_REPUBLICATION.equals(workflow)
            || Workflow.PUBLISHED.equals(workflow)
            || Workflow.PARENT_REVOKED.equals(workflow)
            || Workflow.REVOKED.equals(workflow)) {

            return inPublicationWindow(Instant.now());
        }

        return false;
    }

    public boolean isRevocable() {
        if(Workflow.FOR_DELETION == workflow
            || Workflow.PARENT_REVOKED == workflow
            || Workflow.DELETED == workflow
            ) {
            return true;
        } else if(Workflow.PUBLISHED == workflow
            || Workflow.FOR_REPUBLICATION == workflow
            || Workflow.FOR_PUBLICATION == workflow
            || Workflow.REVOKED == workflow) {

            return !inPublicationWindow(Instant.now());
        }

        return false;
    }

    @XmlAttribute
    @Deprecated
    public Date getLastModified() {
        return DateUtils.toDate(lastModified);
    }

    @Deprecated
    public void setLastModified(Date lastModified) {
        this.lastModified = DateUtils.toInstant(lastModified);
    }

    @Deprecated
    public final Date getCreationDate() {
        return DateUtils.toDate(getCreationInstant());
    }

    @Deprecated
    public final void setCreationDate(Date creationDate) {
        setCreationInstant(DateUtils.toInstant(creationDate));
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
    @XmlAttribute(name = "creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getCreationInstant() {
        return creationDate;
    }

    @Override
    public void setCreationInstant(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Editor getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Editor createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(Editor lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    @XmlAttribute(name = "publishStart")
    @JsonProperty("publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @Override
    public PublishableObject setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    @XmlAttribute(name = "publishStop")
    @JsonProperty("publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @Override
    public PublishableObject setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }

    protected abstract String getUrnPrefix();

    // can be resolved if indeed no need to override any more
    private void setUnrecognizedUrn(String urn) {
        throw new IllegalArgumentException("The urn " + urn + " is not valid for objects with urns " + getUrnPrefix());
    }

    @XmlAttribute(name = "urn")
    @JsonProperty("urn")
    public final String getUrn() {
        return getId() == null ? null : (getUrnPrefix() + getId());
    }

    public void setUrn(String urn) {
        if(urn == null) {
            id = null;
            return;
        }
        int i = urn.lastIndexOf(':') + 1;
        if(!getUrnPrefix().equals(urn.substring(0, i))) {
            log.debug("Specified prefix '" + urn.substring(0, i) + "' is not equal to" +
                " required prefix " + getUrnPrefix());
            setUnrecognizedUrn(urn);
            return;
        }
        String id = urn.substring(i, urn.length());
        if("null".equals(id)) {
            log.debug("Urn was unset");
            setId(null);
        } else {
            setId(Long.parseLong(id));
        }
    }
    @XmlAttribute(name = "publishDate")
    @JsonProperty("publishDate")
    @Deprecated
    public Date getLastPublished() {
        return DateUtils.toDate(lastPublished);
    }

    @Deprecated
    public void setLastPublished(Date lastPublished) {
        this.lastPublished = DateUtils.toInstant(lastPublished);
    }

    public Instant getLastPublishedInstant() {
        return lastPublished;
    }

    public void setLastPublishedInstant(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }

    @XmlAttribute
    public Workflow getWorkflow() {
        return workflow;
    }

    protected void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public boolean isDeleted() {
        return Workflow.DELETES.contains(workflow);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("creationDate", creationDate)
            .append("lastModified", lastModified)
            .append("createdBy", createdBy)
            .append("lastModifiedBy", lastModifiedBy)
            .append("publishStart", publishStart)
            .append("publishStop", publishStop)
            .append("lastPublished", lastPublished)
            .append("workflow", workflow)
            .toString();
    }


    @Deprecated
    public boolean isInAllowedPublicationWindow(long millisFromNow) {
        return inPublicationWindow(Instant.now().plusMillis(millisFromNow));
    }


    void beforeUnmarshal(Unmarshaller u, Object parent) {
        // These things appear in XML, and if they don't, they are null (and not the default value in this class)
        workflow = null;
        creationDate = null;
    }


}
