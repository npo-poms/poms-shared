package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

/**
 * @author Michiel Meeuwissen
 */
@XmlEnum
public enum Platform {
    INTERNETVOD,
    TVVOD,
    PLUSVOD, // NLZiet, platform "extra" in cooperation with dutch commercial broadcasters
    NPOPLUSVOD // NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
}
