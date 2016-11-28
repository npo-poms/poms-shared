/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import javax.persistence.*;
import javax.xml.bind.JAXB;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.internal.FilterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.user.Editor;
import nl.vpro.util.DateUtils;
import nl.vpro.validation.Publishable;

/**
 * Publishable contains all items for Publishables.
 *
 * @author arne
 * @author roekoe
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@Publishable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publishableObjectType", namespace = Xmlns.SHARED_NAMESPACE)
//@XmlTransient
public abstract class PublishableObject extends DomainObject {

    public static final String DELETED_FILTER = "deletedFilter";
    public static final String INVERSE_DELETED_FILTER = "inverseDeletedFilter";
    public static final String PUBLICATION_FILTER = "publicationFilter";
    public static final String INVERSE_PUBLICATION_FILTER = "inversePublicationFilter";
    public static final String EMBARGO_FILTER = "embargoFilter";
    public static final String INVERSE_EMBARGO_FILTER = "inverseEmbargoFilter";

    public static final String[] FILTERS = {
        DELETED_FILTER,
        INVERSE_DELETED_FILTER,
        PUBLICATION_FILTER,
        INVERSE_PUBLICATION_FILTER,
        EMBARGO_FILTER,
        INVERSE_EMBARGO_FILTER
    };

    public static Map<String, Filter> getEnabledFilters(Session session) {
        Map<String, Filter> enabledFilters = new HashMap<>();
        for (String filter : FILTERS) {
            Filter f = session.getEnabledFilter(filter);
            if (f != null) {
                enabledFilters.put(filter, f);
            }
        }
        return enabledFilters;
    }

    public static void restoreFilters(Session session, Map<String, Filter> enabledFilters) {
        for (String filter : FILTERS) {
            if (enabledFilters.containsKey(filter)) {
                FilterImpl originalFilter = (FilterImpl) enabledFilters.get(filter);
                Filter newFilter = session.enableFilter(filter);
                for (Map.Entry<String, ?> entry : originalFilter.getParameters().entrySet()) {
                    Object value = entry.getValue();
                    String name = entry.getKey();
                    if (value instanceof Collection) {
                        newFilter.setParameterList(name, (Collection) value);
                    } else {
                        newFilter.setParameter(name, value);
                    }
                }
            } else {
                session.disableFilter(filter);
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(PublishableObject.class);

    @Column(nullable = false)
        protected Date creationDate = new Date();

    @Column(nullable = false)
    protected Date lastModified;

    @ManyToOne(optional = false)
    @JoinColumn(name = "createdby_principalid")
    protected Editor createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lastmodifiedby_principalid")
    protected Editor lastModifiedBy;

    protected Date publishStart;
    protected Date publishStop;

    protected Date lastPublished;

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
    public boolean hasChanges() {
        long currentState;

        if(this.id == null || this.crc32 == null) {
            return true; // we are always dirty if we don't have an id or crc
        }

        currentState = calcCRC32();

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
    public void acceptChanges() {
        this.crc32 = calcCRC32();
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


    protected long calcCRC32() {
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
            if(LOG.isDebugEnabled()) {
                LOG.debug(new String(serialized));
            }
            return crc32Local.getValue();

        } finally {

            if(baos != null) {
                try {
                    baos.close();
                } catch(Exception e) {
                }
            }
            if(writer != null) {
                try {
                    writer.close();
                } catch(Exception e) {
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

        if(Workflow.PUBLISHED == workflow && isRevocable()) {
            return true;
        }

        return false;
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

            return isInAllowedPublicationWindow();
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

            return !isInAllowedPublicationWindow();
        }

        return false;
    }

    @XmlAttribute
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @XmlAttribute
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public Instant getLastModifiedInstant() {
        return DateUtils.toInstant(getLastModified());
    }

    public void setLastModifiedInstant(Instant lastModified) {
        setLastModified(DateUtils.toDate(lastModified));
    }


    public Instant getCreationInstant() {
        return DateUtils.toInstant(getCreationDate());
    }

    public void setCreationInstant(Instant creationDate) {
        setCreationDate(DateUtils.toDate(creationDate));
    }

    public Editor getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Editor createdBy) {
        this.createdBy = createdBy;
    }

    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Editor lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @XmlAttribute
    @JsonProperty("publishStart")
    public Date getPublishStart() {
        return publishStart;
    }


    public PublishableObject setPublishStart(Date publishStart) {
        if (ObjectUtils.notEqual(publishStart, this.publishStart)) {
            this.publishStart = publishStart;
        }
        return this;
    }

    @XmlAttribute
    @JsonProperty("publishStop")
    public Date getPublishStop() {
        return publishStop;
    }

    public PublishableObject setPublishStop(Date publishStop) {
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
            LOG.debug("Specified prefix '" + urn.substring(0, i) + "' is not equal to" +
                " required prefix " + getUrnPrefix());
            setUnrecognizedUrn(urn);
            return;
        }
        String id = urn.substring(i, urn.length());
        if("null".equals(id)) {
            LOG.debug("Urn was unset");
            setId(null);
        } else {
            setId(Long.parseLong(id));
        }
    }
    @XmlAttribute(name = "publishDate")
    @JsonProperty("publishDate")
    public Date getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Date lastPublished) {
        this.lastPublished = lastPublished;
    }

    @XmlAttribute
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
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

    public boolean isInAllowedPublicationWindow() {
        return isInAllowedPublicationWindow(0);
    }

    public boolean isInAllowedPublicationWindow(java.time.Duration millisFromNow) {
        return isInAllowedPublicationWindow(millisFromNow.toMillis());
    }

    public boolean isInAllowedPublicationWindow(long millisFromNow) {
        Date stop = getPublishStop();
        if(stop != null
            && stop.getTime() < System.currentTimeMillis() + millisFromNow) {

            return false;
        }
        Date start = getPublishStart();
        if(start != null
            && start.getTime() > System.currentTimeMillis() + millisFromNow) {

            return false;
        }

        return true;
    }


    void beforeUnmarshal(Unmarshaller u, Object parent) {
        // These things appear in XML, and if they don't, they are null (and not the default value in this class)
        workflow = null;
        creationDate = null;
    }


}
