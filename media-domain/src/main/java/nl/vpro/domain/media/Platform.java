package nl.vpro.domain.media;

import lombok.Getter;

import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.media.AVType.AUDIO;
import static nl.vpro.domain.media.AVType.VIDEO;


/**
 * @author Michiel Meeuwissen
 */
@Getter
@XmlEnum
@XmlType(name = "platformTypeEnum")
public enum Platform implements Displayable {
    /**
     * Visible on internet. May also be used for audio?
     */
    INTERNETVOD(true, "Internet", AUDIO, VIDEO) {
        @Override
        public boolean matches(Platform platform) {
            return platform == null || super.matches(platform);
        }
    },

    /**
     *
     */
    TVVOD(true, "Kabelaars", VIDEO),

    /**
     * NLZiet,platform "extra" in cooperation with Dutch commercial broadcasters
     */
    PLUSVOD(true, "Nlziet/NPO Plus", VIDEO),

    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     *  Zie <a href="https://jira.vpro.nl/browse/MSE-2742">MSE-2742</a> Blijkbaar nooit gebruikt.
     */

    @SuppressWarnings("DeprecatedIsStillUsed") @Deprecated
    NPOPLUSVOD(false, "NPO Plus", VIDEO) {
        @Override
        public boolean display() {
            return false;
        }
    };


    private final boolean streamingPlatform;

    @Getter
    private final String displayName;

    private final Set<AVType> avTypes;


    Platform(boolean streamingPlatform, String displayName, AVType... avTypes) {
        this.streamingPlatform = streamingPlatform;
        this.displayName = displayName;
        this.avTypes = Set.of(avTypes);
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
