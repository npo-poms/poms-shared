package nl.vpro.domain.media;

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
    INTERNETVOD,

    /**
     *
     */
    TVVOD,

    /**
     * NLZiet,platform "extra" in cooperation with dutch commercial broadcasters
     */
    PLUSVOD,

    /**
     *  NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
     */
    NPOPLUSVOD
}
