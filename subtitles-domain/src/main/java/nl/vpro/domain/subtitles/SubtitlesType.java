package nl.vpro.domain.subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "subtitlesTypeEnum")
public enum SubtitlesType {
    CAPTION,
    TRANSLATION
}
