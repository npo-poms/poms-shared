package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 */
@XmlEnum
@XmlType(name = "platformTypeEnum")
public enum Platform {
    /**
     * Visible on internet
     */
    INTERNETVOD(true),

    /**
     *
     */
    TVVOD(true),

    /**
     * NLZiet,platform "extra" in cooperation with dutch commercial broadcasters
     */
    PLUSVOD(true),

    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     */
    NPOPLUSVOD(false);


    @Getter
    private final boolean streamingPlatform;


    Platform(boolean streamingPlatform) {
        this.streamingPlatform = streamingPlatform;
    }
}
