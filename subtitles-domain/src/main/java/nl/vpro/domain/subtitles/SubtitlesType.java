package nl.vpro.domain.subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "subtitlesTypeEnum")
public enum SubtitlesType {
    CAPTION,
    TRANSLATION
}
