/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.zip.CRC32;

import javax.persistence.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.*;
import nl.vpro.domain.media.TrackableObject;
import nl.vpro.util.DateUtils;
import nl.vpro.validation.EmbargoValidation;
import nl.vpro.validation.PomsValidatorGroup;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.Changeables.instant;

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
    implements MutableEmbargoDeprecated<T>, TrackableObject {

    public static ThreadLocal<Boolean> PUBLISH_WORKFLOW = ThreadLocal.withInitial(() -> false);

    private static final long serialVersionUID = 7010308562857745023L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Workflow workflow = Workflow.FOR_PUBLICATION;

    @Column(nullable = true)
    private Long crc32;

    @SuppressWarnings("CopyConstructorMissesField")
    protected PublishableObject(PublishableObject<T> source) {
        super(source);
        this.workflow = source.workflow;
    }


    /**
     * Checks if this object is changed compared to the persistent version
     * by calculating the runtime CRC32 and compare it to the stored CRC
     */
    @Override
    public boolean hasChanges() {
        long currentState;

        if(this.id == null || this.crc32 == null) {
            return true; // we are always dirty if we don't have an id or crc
        }

        currentState = calcCRC32().getValue();

        return currentState != this.crc32;
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

    private byte[] serializeForCalcCRC32(byte[] data, Charset encoding) {
        String dataAsString = new String(data, encoding);
        // FOR_DELETION is rewritten to DELETED so that it IS detected as a change
        dataAsString = dataAsString.replaceAll("workflow=\"FOR DELETION", "workflow=\"DELETED\"");

        // filter stuff we DONT want in here
        dataAsString = dataAsString.replaceAll("workflow=\".+?(?!DELETED)\"", "");
        dataAsString = dataAsString.replaceAll("\\s+publishDate=\".+?\"\\s+", " ");   // neither is this
        dataAsString = dataAsString.replaceAll("\\s+lastModified=\".+?\"\\s+", " "); // this should not matter, but last modified may be set too late...

        // NOTE: all remaining fields we don't want are set to XMLTransient and therefore won't get copied into this XML to start with

        return dataAsString.getBytes(encoding);
    }

    protected byte[] serializeForCalcCRC32() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, UTF_8)
        ) {
            JAXB.marshal(this, writer);
            return serializeForCalcCRC32(baos.toByteArray(), UTF_8);
        } catch (IOException ioException) {
            log.warn("{}", ioException.getMessage(), ioException);
            return new byte[0];
        }
    }

    /**
     * We keep track of a CRC32 hash to determin if an object is 'changed', in the sense that
     * it would need republication.
     */
    protected CRC32 calcCRC32() {
        byte[] serialized = serializeForCalcCRC32();
        CRC32 crc32Local = new CRC32();
        crc32Local.reset();
        crc32Local.update(serialized);
        if(log.isDebugEnabled()) {
            log.debug(new String(serialized, UTF_8));
        }
        return crc32Local;
    }

    public PublishableObject() {
    }

    public PublishableObject(long id) {
        this.id = id;
    }

    public boolean isActivation(Instant now) {
        return isPublishable(now) && Workflow.WITH_MEDIA_ACTIVATION.contains(workflow);
    }

    public boolean isDeactivation(Instant now) {
        if(Workflow.PARENT_REVOKED == workflow
            || Workflow.FOR_DELETION == workflow) {
            return true;
        }

        return Workflow.PUBLISHED == workflow && isRevocable(now);
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

    @Deprecated
    public Date getLastPublished() {
        return DateUtils.toDate(lastPublished);
    }

    @Deprecated
    public void setLastPublished(Date lastPublished) {
        this.lastPublished = DateUtils.toInstant(lastPublished);
    }

    @XmlAttribute
    @Override
    public Workflow getWorkflow() {
        if (PUBLISH_WORKFLOW.get() && workflow != null) {
            Workflow as = workflow.getAs();
            if (as != null) {
                log.warn("Returning workflow {} as {}", workflow, as);
            }
            return as;
        }
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
        return inPublicationWindow(instant().plusMillis(millisFromNow));
    }

    @Override
    protected void beforeUnmarshal(Unmarshaller u, Object parent) {
        super.beforeUnmarshal(u, parent);
        // These things appear in XML, and if they don't, they are null (and not the default value in this class)
        workflow = null;
    }


}
