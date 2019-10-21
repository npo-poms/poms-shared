package nl.vpro.domain.subtitles;

import java.util.*;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@XmlEnum
@XmlType(name = "subtitlesWorkflowEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum SubtitlesWorkflow {
    IGNORE,

    REVOKED,

    FOR_DELETION,
    DELETED,

    FOR_PUBLICATION,
    FOR_REPUBLICATION,
    PUBLISHED,

    /**
     * Could not be published because of some error
     */
    PUBLISH_ERROR
    ;


    public static final  Set<SubtitlesWorkflow> NEEDS_WORK = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FOR_DELETION, FOR_PUBLICATION, FOR_REPUBLICATION)));

}
