package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.i18n.Displayable;


/**
 * @author Michiel Meeuwissen
 */
@XmlEnum
@XmlType(name = "platformTypeEnum")
public enum Platform implements Displayable {
    /**
     * Visible on internet. May also be used for audio?
     */
    INTERNETVOD(true, "Internet") {
        @Override
        public boolean matches(Platform platform) {
            return platform == null || super.matches(platform);
        }
    },

    /**
     *
     */
    TVVOD(true, "Kabelaars"),

    /**
     * NLZiet,platform "extra" in cooperation with dutch commercial broadcasters
     */
    PLUSVOD(true, "Nlziet/NPO Start Plus"),

    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     *  Zie <a href="https://jira.vpro.nl/browse/MSE-2742">MSE-2742</a> Blijkbaar nooit gebruikt.
     */
    @Deprecated
    NPOPLUSVOD(false, "NPO Plus") {
        @Override
        public boolean display() {
            return false;
        }
    };


    @Getter
    private final boolean streamingPlatform;

    @Getter
    private final String displayName;


    Platform(boolean streamingPlatform, String displayName) {
        this.streamingPlatform = streamingPlatform;
        this.displayName = displayName;
    }

    public static Platform valueOfOrNull(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return valueOf(id);
    }

    /**
     * @since 5.6.2
     */
    public boolean matches(Platform platform) {
        return this == platform;
    }
}
