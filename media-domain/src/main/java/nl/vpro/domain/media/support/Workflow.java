/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.TrackableObject;
import nl.vpro.i18n.Displayable;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;
import nl.vpro.util.XmlValued;

import static java.util.Collections.unmodifiableSet;
import static nl.vpro.domain.media.CollectionUtils.nullSafeSet;

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
    IGNORE("Genegeerd", null),

    PUBLISHED("Gepubliceerd"),
    /**
     * The object is not yet published, but should be considered for publication. This probably is a new object.
     */
    @XmlEnumValue("FOR PUBLICATION")
    FOR_PUBLICATION("Voor publicatie", PUBLISHED),

    /**
     * The object is already published, but something has been changed, and it needs to be published again.
     */
    @XmlEnumValue("FOR REPUBLICATION")
    FOR_REPUBLICATION("Wordt gepubliceerd", PUBLISHED),



    /**
     * The object is merged with another object. An object will get this status when it is published for the last time.
     * <p>
     * Used only on {@link MediaObject}s.
     * <p>
     * Normal users should not see these objects, but should be directed to the object {@link MediaObject#getMergedTo()}
     * Objects are published though to ES, so the redirect list can be dynamically built.
     */
    MERGED("Samengevoegd"),

    /**
     * Set when a publishStop date has expired on a parent. For example: a Segment obtains this workflow when its parent Program is revoked.
     */
    @XmlEnumValue("PARENT REVOKED")
    PARENT_REVOKED("Programma ingetrokken"),

    /**
     * Set when a publishStop date has expired and an entity is revoked. This state is not set by the end-user.
     * Setting this state directly without an expired publishStop is useless, because an entity will be republished anyhow.
     */
    REVOKED("Ingetrokken"),

    /**
     * If someone explicitly deleted an entity then it becomes 'deleted'. This implies revocation from publication.
     * Normal users should not see these entities.
     * <p>
     * This is also used if an authoritative system sends or refers to an object that is not in our system. It is then 'temporarily' created in this state.
     * If at a later time metadata comes in, it will be implicitly undeleted, and the until then invisible data of it will become available (e.g. just the streaming status, a schedule event or member). See als {@link OwnerType#TEMPORARY}.
     */
    DELETED("Verwijderd"),
    /**
     * The entity is scheduled for deletion.
     */
    @XmlEnumValue("FOR DELETION")
    FOR_DELETION("Wordt verwijderd", DELETED),


    /**
     * The complete object is temporary. It is not published, and probably does not yet contain useful metadata. It is expected that that will follow
     * in which case the workflow probably will change to {@link #FOR_PUBLICATION}. This may e.g. be a group with members, but which does not itself already
     * have metadata (like a title). It may e.g. also be a program for which a NEP notify was received but no futer meta data yet.
     * @since 7.11
     */
    @XmlEnumValue("TEMPORARY")
    TEMPORARY("Tijdelijk")

    ;



    public static final Set<Workflow> WITH_MEDIA_ACTIVATION = nullSafeSet(Set.of(
        FOR_PUBLICATION,
        PARENT_REVOKED,
        REVOKED
    ));



    public static final Set<Workflow> PUBLICATIONS = nullSafeSet(Set.of(
        PUBLISHED,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    ));

    public static final Set<Workflow> PUBLICATIONS_OR_NULL = nullSafeSet(Set.of(
        PUBLISHED,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    ), true);

    /**
     * The workflows that are considered 'deleted'. I.e. {@link #DELETED} and {@link #FOR_DELETION}
     * Not {@link #REVOKED}
     */
    public static final Set<Workflow> DELETES = nullSafeSet(Set.of(
        FOR_DELETION,
        DELETED,
        TEMPORARY
    ));


    /**
     * The workflows that are considered 'deleted' in the frontend. I.e. {@link #DELETED} and {@link #FOR_DELETION},
     * but also {@link #REVOKED}, {@link #PARENT_REVOKED} and {@link #MERGED}.
     */
    public static final Set<Workflow> PUBLISHED_AS_DELETED = nullSafeSet(Set.of(
        FOR_DELETION,
        DELETED,
        MERGED,
        PARENT_REVOKED,
        REVOKED,
        TEMPORARY
    ));

    /**
     * The workflows that are allowable in ES. So not the 'FOR_' workflows.
     */
    public static final Set<Workflow> API = nullSafeSet(Set.of(
        DELETED,
        MERGED,
        PARENT_REVOKED,
        REVOKED,
        PUBLISHED
    ), true);

    public static final Set<Workflow> AS_DELETED_IN_API =
        nullSafeSet(PUBLISHED_AS_DELETED.stream()
            .filter(Workflow::isPublishable)
            .collect(Collectors.toUnmodifiableSet())
        );


    public static final Set<Workflow> REVOKES = nullSafeSet(Set.of(
        FOR_DELETION,
        DELETED,
        REVOKED,
        PARENT_REVOKED,
        MERGED
    ));

    public static final Set<Workflow> REVOKES_OR_IGNORE;
    static {
        Set<Workflow> list = new TreeSet<>(REVOKES);
        list.add(Workflow.IGNORE);
        REVOKES_OR_IGNORE = nullSafeSet(unmodifiableSet(list));
    }

    public static final Set<Workflow> NEEDWORK = nullSafeSet(Set.of(
        FOR_DELETION,
        FOR_PUBLICATION,
        FOR_REPUBLICATION
    ));

    @Getter
    private final String description;

    /**
     * Whether this workflow can appear in the frontend api.
     *
     * @see #getPublishedAs()
     */
    @Getter
    private final boolean publishable;

    private final Workflow publishedAs;

    Workflow(String description, Workflow publishedAs) {
        this.description = description;
        this.publishable = false;
        this.publishedAs = publishedAs;
    }

    Workflow(String description) {
        this.description = description;
        this.publishable = true;
        this.publishedAs = this;
    }

    /**
     * Some workflows are 'temporary' (see {@link #isPublishable()}  and only used for administration purposes. This returns the
     * workflow as it would appear when all administrative work is done.
     */
    @NonNull
    public Workflow getPublishedAs() {
        return publishedAs;
    }

    @Override
    public String getDisplayName() {
        return getDescription();
    }

    /**
     * E.g. {@code #PUBLISHED::predicate} is a predicate on a {@link TrackableObject}.
     */

    public boolean predicate(TrackableObject publishableObject) {
        return publishableObject.getWorkflow() == Workflow.this;
    }


    public static class Deserializer extends BackwardsCompatibleJsonEnum.Deserializer<Workflow> {
        public Deserializer() {
            super(Workflow.class);
        }
    }
}
