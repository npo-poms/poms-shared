/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Date;
import java.util.zip.CRC32;

import javax.persistence.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.*;
import nl.vpro.util.DateUtils;
import nl.vpro.validation.PomsValidatorGroup;
import nl.vpro.validation.EmbargoValidation;

/**
 * A publishable object implements {@link Accountable} and {@link MutableEmbargo}, but furthermore also has {@link #workflow}.
 *
 * @author arne
 * @author roekoe
 */
@MappedSuperclass
@EmbargoValidation(groups = {PomsValidatorGroup.class})
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publishableObjectType", namespace = Xmlns.SHARED_NAMESPACE)
//@XmlTransient
@Slf4j
public abstract class PublishableObject<T extends PublishableObject<T>>
    extends AbstractPublishableObject<T>
    implements MutableEmbargoDeprecated<T> {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Workflow workflow = Workflow.FOR_PUBLICATION;

    @Column(nullable = true)
    private Long crc32;

    protected PublishableObject(PublishableObject<T> source) {
        super(source);
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
    //@Override
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

    protected byte[] prepareForCRCCalc() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos)
        ) {
            JAXB.marshal(this, writer);
            return prepareForCRCCalc(baos.toByteArray(), writer.getEncoding());
        } catch (IOException ignored) {
            return new byte[0];
        }
    }


    protected CRC32 calcCRC32() {
        byte[] serialized = prepareForCRCCalc();
        CRC32 crc32Local = new CRC32();
        crc32Local.reset();
        crc32Local.update(serialized);
        if(log.isDebugEnabled()) {
            log.debug(new String(serialized));
        }
        return crc32Local;
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

    /**
     * Wether this object could be  publicly visible in the API.
     *
     * This returns <code>false</code> if the workflow explictely indicates that it is not (like 'DELETED', 'MERGED')
     * and otherwise it depends on {@link #inPublicationWindow(Instant)}
     */
    public boolean isPublishable() {
        if(isMerged() ||
            Workflow.FOR_DELETION == workflow ||
            Workflow.PARENT_REVOKED == workflow || // The parent is revoked so this object itself is not publishable either
            Workflow.DELETED == workflow) {
            // These kind of objects are explicely not publishable.
            return false;
        }

        if(Workflow.FOR_PUBLICATION.equals(workflow)
            || Workflow.FOR_REPUBLICATION.equals(workflow)
            || Workflow.PUBLISHED.equals(workflow)
            || Workflow.REVOKED.equals(workflow)
            || Workflow.MERGED.equals(workflow)
            || workflow == null /* may happen when property filtering active NPA-493 */) {

            return inPublicationWindow(Instant.now());
        }
        log.error("Unexpected state of {}. Workflow: {}. Supposing this is not publishable for now", this, workflow);
        return false;

    }

    public boolean isRevocable() {
        return ! isPublishable();
    }

    /**
     * If the sub class supports being merged, this can be overriden.
     *
     */
    public  boolean isMerged() {
        return false;
    }

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


    // can be resolved if indeed no need to override any more
    private void setUnrecognizedUrn(String urn) {
        throw new IllegalArgumentException("The urn " + urn + " is not valid for objects with urns " + getUrnPrefix());
    }

    @Deprecated
    public Date getLastPublished() {
        return DateUtils.toDate(lastPublished);
    }

    @Deprecated
    public void setLastPublished(Date lastPublished) {
        this.lastPublished = DateUtils.toInstant(lastPublished);
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
            .append("workflow", workflow)
            .toString();
    }


    @Deprecated
    public boolean isInAllowedPublicationWindow(long millisFromNow) {
        return inPublicationWindow(Instant.now().plusMillis(millisFromNow));
    }


    @Override
    protected void beforeUnmarshal(Unmarshaller u, Object parent) {
        super.beforeUnmarshal(u, parent);
        // These things appear in XML, and if they don't, they are null (and not the default value in this class)
        workflow = null;
    }


}
