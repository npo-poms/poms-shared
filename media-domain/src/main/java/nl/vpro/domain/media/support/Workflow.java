/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain.media.support;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.Xmlns;
import nl.vpro.jackson2.BackwardsCompatibleJsonEnum;

/**
 * <p>The workflow status for publishable items.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="workflowEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DRAFT"/>
 *     &lt;enumeration value="FOR_APPROVAL"/>
 *     &lt;enumeration value="PUBLISHED"/>
 *     &lt;enumeration value="REFUSED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="MERGED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author arne
 * @author roekoe
 * @version $Id$
 */
@XmlEnum
@XmlType(name = "workflowEnumType", namespace = Xmlns.SHARED_NAMESPACE)
@JsonSerialize(using = BackwardsCompatibleJsonEnum.Serializer.class)
@JsonDeserialize(using = Workflow.Deserializer.class)
public enum Workflow implements Displayable {

    @Deprecated // not used
    DRAFT("Voorstel"),

    @XmlEnumValue("FOR APPROVAL")
    @Deprecated // not used
    FOR_APPROVAL("Wacht op goedkeuring"),

    @Deprecated // not used
    REFUSED("Afgewezen"),

    @XmlEnumValue("FOR PUBLICATION")
    FOR_PUBLICATION("Voor publicatie"),

    @XmlEnumValue("FOR REPUBLICATION")
    FOR_REPUBLICATION("Wordt gepubliceerd"),

    PUBLISHED("Gepubliceerd"),

    MERGED("Samengevoegd"),

    /**
     * Set when a publishStop date has expired on a parent. For example: a Segment is obtains this workflow when it's
     * parent Program is revoked.
     */
    @XmlEnumValue("PARENT REVOKED")
    PARENT_REVOKED("Programma ingetrokken"),

    /**
     * Set when a publishStop date has expired and an entity is revoked. This state is nether set by the end-user.
     * Setting this state directly without an expired publishStop is useless, because an entity will be republished
     * anyhow.
     */
    REVOKED("Ingetrokken"),

    /**
     * Schedule an entity for deletion.
     */
    @XmlEnumValue("FOR DELETION")
    FOR_DELETION("Wordt verwijderd"),

    /**
     * If someone explicitly deleted an entity than it becomes 'deleted'. This implies revocation from publication.
     * Normal users should not see these entities.
     */
    DELETED("Verwijderd");

    public static final List<Workflow> WITH_MEDIA_ACTIVATION =
        Arrays.asList(
            Workflow.FOR_PUBLICATION,
            Workflow.PARENT_REVOKED,
            Workflow.REVOKED);



    public static final List<Workflow> PUBLICATIONS =
        Arrays.asList(Workflow.PUBLISHED, Workflow.FOR_PUBLICATION, Workflow.FOR_REPUBLICATION);

    public static final List<Workflow> DELETES =
        Arrays.asList(Workflow.FOR_DELETION, Workflow.DELETED);


    public static final List<Workflow> PUBLISHED_AS_DELETED =
        Arrays.asList(Workflow.FOR_DELETION, Workflow.DELETED, Workflow.MERGED, Workflow.PARENT_REVOKED);

    public static final List<Workflow> REVOKES =
        Arrays.asList(Workflow.FOR_DELETION, Workflow.DELETED, Workflow.REVOKED, Workflow.MERGED);

    public static final List<Workflow> NEEDWORK =
        Arrays.asList(Workflow.FOR_DELETION, Workflow.FOR_PUBLICATION, Workflow.FOR_REPUBLICATION);

    public static final List<Workflow> DEPRECATED =
        Arrays.asList(Workflow.DRAFT, Workflow.REFUSED);

    private final String description;

    Workflow(String description) {
        this.description = description;
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
