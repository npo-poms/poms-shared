package nl.vpro.domain.subtitles;


import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */

@XmlEnum
@XmlType(name = "subtitlesTypeEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum SubtitlesType {
    CAPTION,
    TRANSLATION,
    TRANSCRIPT
}
