/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.i18n.Displayable;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

/**
 * <p>The workflow status for publishable items.</p>
 * @author arne
 * @author roekoe
 */
@XmlEnum
@XmlType(name = "workflowEnumType", namespace = Xmlns.SHARED_NAMESPACE)
@JsonSerialize(using = BackwardsCompatibleJsonEnum.Serializer.class)
@JsonDeserialize(using = Workflow.Deserializer.class)
public enum Workflow implements Displayable, XmlValued {

    /**
     * Will be completely ignored by publishers. Will not be published, will not be revoked.
     * Handy for debugging, to mute all objects besides the one you're interested in.
     */
    IGNORE("Genegeerd", false),

    /**
     * The object is not yet published, but should be considered for publication. This probably is a new object.
     */
    @XmlEnumValue("FOR PUBLICATION")
    FOR_PUBLICATION("Voor publicatie", false),

    /**
     * The object is already published, but something has been changed, and it needs to be published again.
     */
    @XmlEnumValue("FOR REPUBLICATION")
    FOR_REPUBLICATION("Wordt gepubliceerd", false),

    PUBLISHED("Gepubliceerd", true),

    /**
     * The object is merged with another object. An object will get this status when it is published for the last time.
     *
     * Used only on {@link MediaObject}s.
     *
     * Normal users should not see these objects, but should be directed to the object {@link MediaObject#getMergedTo()}
     */
    MERGED("Samengevoegd", true),

    /**
     * Set when a publishStop date has expired on a parent. For example: a Segment obtains this workflow when its parent Program is revoked.
     */
    @XmlEnumValue("PARENT REVOKED")
    PARENT_REVOKED("Programma ingetrokken", true),

    /**
     * Set when a publishStop date has expired and an entity is revoked. This state is not set by the end-user.
     * Setting this state directly without an expired publishStop is useless, because an entity will be republished anyhow.
     */
    REVOKED("Ingetrokken", true),

    /**
     * The entity is scheduled for deletion.
     */
    @XmlEnumValue("FOR DELETION")
    FOR_DELETION("Wordt verwijderd", false),

    /**
     * If someone explicitly deleted an entity then it becomes 'deleted'. This implies revocation from publication.
     * Normal users should not see these entities.
     */
    DELETED("Verwijderd", true);

    public static final List<Workflow> WITH_MEDIA_ACTIVATION = unmodifiableList(asList(
        FOR_PUBLICATION,
        PARENT_REVOKED,
        REVOKED
    ));



    public static final List<Workflow> PUBLICATIONS = unmodifiableList(asList(
        PUBLISHED,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    ));

    public static final List<Workflow> DELETES = unmodifiableList(asList(
        FOR_DELETION,
        DELETED
    ));


    public static final List<Workflow> PUBLISHED_AS_DELETED = unmodifiableList(asList(
        FOR_DELETION,
        DELETED,
        MERGED,
        PARENT_REVOKED,
        REVOKED
    ));

    public static final Set<Workflow> API = unmodifiableSet(new HashSet<>(asList(
        DELETED,
        MERGED,
        PARENT_REVOKED,
        REVOKED,
        PUBLISHED
    )));

    public static final List<Workflow> AS_DELETED_IN_API = unmodifiableList(
        PUBLISHED_AS_DELETED.stream()
            .filter(Workflow::isPublishable)
            .collect(Collectors.toList())
    );

    public static final List<Workflow> REVOKES = unmodifiableList(asList(
        FOR_DELETION,
        DELETED,
        REVOKED,
        MERGED
    ));
    public static final List<Workflow> REVOKES_OR_IGNORE;
    static {
        List<Workflow> list = new ArrayList<>(REVOKES);
        list.add(Workflow.IGNORE);
        REVOKES_OR_IGNORE = unmodifiableList(list);
    }

    public static final List<Workflow> NEEDWORK = unmodifiableList(asList(
        FOR_DELETION,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    ));

    private final String description;

    /**
     * Wether this workflow can appear in the frontend api.
     */
    @Getter
    private final boolean publishable;

    Workflow(String description, boolean publishable) {
        this.description = description;
        this.publishable = publishable;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getDisplayName() {
        return getDescription();
    }


    public static class Deserializer extends BackwardsCompatibleJsonEnum.Deserializer<Workflow> {
        public Deserializer() {
            super(Workflow.class);
        }
    }
}
