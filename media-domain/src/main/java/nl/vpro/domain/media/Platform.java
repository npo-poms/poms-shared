package nl.vpro.domain.media;

import lombok.Getter;

import java.util.Set;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;

import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.media.AVType.*;


/**
 * @author Michiel Meeuwissen
 */
@Getter
@XmlEnum
@XmlType(name = "platformTypeEnum")
public enum Platform implements Displayable {
    /**
     * Visible on internet.
     * <p>
     * For audio see {@link #INTERNETAOD}
     */
    INTERNETVOD(true, "Internet/NPO Start", true, AUDIO, VIDEO) {
        @Override
        public boolean matches(Platform platform) {
            return platform == null || super.matches(platform);
        }
    },


    /**
     *
     */
    TVVOD(true, "Kabelaars", true, VIDEO),

    /**
     * NLZiet,platform "extra" in cooperation with Dutch commercial broadcasters
     */
    PLUSVOD(true, "Nlziet/NPO Plus", true, VIDEO),


    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     *  Zie <a href="https://jira.vpro.nl/browse/MSE-2742">MSE-2742</a> Blijkbaar nooit gebruikt.
     */

    @SuppressWarnings("DeprecatedIsStillUsed") @Deprecated
    NPOPLUSVOD(false, "NPO Plus", true, VIDEO) {
        @Override
        public boolean display() {
            return false;
        }
    },
    /**
     * @since 8.3
     */
    TVVOD_BE(true, "Distributeurs België", true, VIDEO),

    /**
     * @since 8.3
     */
    TEST(true, "TEST", true, VIDEO),
    ;

    /**
     * Audible on internet. For now this just is {@link #INTERNETVOD}, assuming that even if some object has both audio and video, it should make no difference for the availability on a certain platform.
     * <p>
     * Having this a proper enum would also require large amounts of republication, so for now we at least postpone that.
     * @since 7.7
     */
    @Beta
    //INTERNETAOD(true, "Internet/audio", false, AUDIO, MIXED),
    public static final Platform INTERNETAOD = INTERNETVOD;



    private final boolean streamingPlatform;

    @Getter
    private final String displayName;

    private final Set<AVType> avTypes;


    private final boolean encryptionIsPossible;


    Platform(boolean streamingPlatform, String displayName, boolean encryptionIsPossible, AVType... avTypes) {
        this.streamingPlatform = streamingPlatform;
        this.displayName = displayName;
        this.encryptionIsPossible = encryptionIsPossible;
        this.avTypes = Set.of(avTypes);
    }

    public static Platform valueOfOrNull(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return get(id);
    }


    public static Platform get(String id) {
        if ("INTERNETAOD".equals(id)) {
            return INTERNETAOD;
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
