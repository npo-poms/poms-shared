/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.internal.FilterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.user.Editor;
import nl.vpro.validation.Publishable;

/**
 * Publishable contains all items for Publishables.
 *
 * @author arne
 * @author roekoe
 */
@MappedSuperclass
@Publishable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publishableObjectType", namespace = Xmlns.SHARED_NAMESPACE)
//@XmlTransient
public interface PublishableObjectInterface extends DomainObject {

    Logger LOG = LoggerFactory.getLogger(PublishableObject.class);


    String DELETED_FILTER = "deletedFilter";
    String INVERSE_DELETED_FILTER = "inverseDeletedFilter";
    String PUBLICATION_FILTER = "publicationFilter";
    String INVERSE_PUBLICATION_FILTER = "inversePublicationFilter";
    String EMBARGO_FILTER = "embargoFilter";
    String INVERSE_EMBARGO_FILTER = "inverseEmbargoFilter";

    String[] FILTERS = {
        DELETED_FILTER,
        INVERSE_DELETED_FILTER,
        PUBLICATION_FILTER,
        INVERSE_PUBLICATION_FILTER,
        EMBARGO_FILTER,
        INVERSE_EMBARGO_FILTER
    };

    static Map<String, Filter> getEnabledFilters(Session session) {
        Map<String, Filter> enabledFilters = new HashMap<>();
        for (String filter : FILTERS) {
            Filter f = session.getEnabledFilter(filter);
            if (f != null) {
                enabledFilters.put(filter, f);
            }
        }
        return enabledFilters;
    }

    static void restoreFilters(Session session, Map<String, Filter> enabledFilters) {
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

    default long calcCRC32() {
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
            if (LOG.isDebugEnabled()) {
                LOG.debug(new String(serialized));
            }
            return crc32Local.getValue();

        } finally {

            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Checks if this object is changed compared to the persistant version
     * by calculating the runtime CRC32 and compare it to the stored CRC
     *
     * @return
     */
    default boolean hasChanges() {
        long currentState;

        if(this.getId() == null || this.getHash() == null) {
            return true; // we are always dirty if we don't have an id or crc
        }

        currentState = calcCRC32();

        if(currentState != this.getHash()) {
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
    void acceptChanges();
    Long getHash();


    static byte[] prepareForCRCCalc(byte[] data, String encoding) {
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
    default boolean isActivation() {
        return isPublishable() && Workflow.WITH_MEDIA_ACTIVATION.contains(getWorkflow());
    }

    default boolean isDeactivation() {
        Workflow workflow = getWorkflow();
        if(Workflow.PARENT_REVOKED == workflow
            || Workflow.FOR_DELETION == workflow) {
            return true;
        }

        if(Workflow.PUBLISHED == workflow && isRevocable()) {
            return true;
        }

        return false;
    }

    default boolean isPublishable() {
        Workflow workflow = getWorkflow();
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

    default boolean isRevocable() {
        Workflow workflow = getWorkflow();
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
    Date getLastModified();
    void setLastModified(Date lastModified);
    @XmlAttribute
    Date getCreationDate();
    void setCreationDate(Date creationDate);
    Editor getCreatedBy();
    void setCreatedBy(Editor createdBy);
    Editor getLastModifiedBy();
    void setLastModifiedBy(Editor lastModifiedBy);
    @XmlAttribute
    @JsonProperty("publishStart")
    Date getPublishStart();
    PublishableObject setPublishStart(Date publishStart);
    @XmlAttribute
    @JsonProperty("publishStop")
    Date getPublishStop();
    PublishableObject setPublishStop(Date publishStop);
    String getUrnPrefix();

    // can be resolved if indeed no need to override any more
    default void setUnrecognizedUrn(String urn) {
        throw new IllegalArgumentException("The urn " + urn + " is not valid for objects with urns " + getUrnPrefix());
    }

    @XmlAttribute(name = "urn")
    @JsonProperty("urn")
    default String getUrn() {
        return getId() == null ? null : (getUrnPrefix() + getId());
    }

    default void setUrn(String urn) {
        if(urn == null) {
            setId(null);
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
    Date getLastPublished();

    void setLastPublished(Date lastPublished);
    @XmlAttribute
    Workflow getWorkflow();
    void setWorkflow(Workflow workflow);
    default boolean isDeleted() {
        return Workflow.DELETES.contains(getWorkflow());
    }

    default boolean isInAllowedPublicationWindow() {
        return isInAllowedPublicationWindow(0);
    }

    default boolean isInAllowedPublicationWindow(java.time.Duration millisFromNow) {
        return isInAllowedPublicationWindow(millisFromNow.toMillis());
    }

    default boolean isInAllowedPublicationWindow(long millisFromNow) {
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

}
