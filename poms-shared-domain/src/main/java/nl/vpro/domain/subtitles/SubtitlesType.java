package nl.vpro.domain.subtitles;


import lombok.Getter;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */

@XmlEnum
@XmlType(name = "subtitlesTypeEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum SubtitlesType implements Displayable {

    /**
     * The subtitles represent a textual version of what is spoken or wat is happening. They are expected to be in the same language as the video itself. Teletekst 888 subtitles are captions.
     */
    CAPTION("close caption"),

    /**
     * The subtitles represent a translation. They are expected to be in a different language than the main language that can be heard
     */
    TRANSLATION("vertaling"),

    /**
     * The subtitles represent a precise or automatic version of what is being said. TT 889, 'spoken' subtitles.
     * TODO I think it is a bit unclear what this means. TT 889?
     */
    TRANSCRIPT("transscript");

    @Getter
    private final String displayName;

    SubtitlesType(String displayName) {
        this.displayName = displayName;
    }
}
