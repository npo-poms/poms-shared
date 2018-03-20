package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 */
@XmlEnum
@XmlType(name = "platformTypeEnum")
public enum Platform implements Displayable {
    /**
     * Visible on internet
     */
    INTERNETVOD(true, "Beschikbaar op internet"),

    /**
     *
     */
    TVVOD(true, "Beschikbaar voor kabelaars"),

    /**
     * NLZiet,platform "extra" in cooperation with dutch commercial broadcasters
     */
    PLUSVOD(true, "NLZiet"),

    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     */
    NPOPLUSVOD(false, "NPOPlus");


    @Getter
    private final boolean streamingPlatform;

    @Getter
    private final String displayName;


    Platform(boolean streamingPlatform, String displayName) {
        this.streamingPlatform = streamingPlatform;
        this.displayName = displayName;
    }
}
