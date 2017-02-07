package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.media.bind.PlatformAdapter;

/**
 * @author Michiel Meeuwissen
 */
@XmlEnum
@XmlType(name = "platformTypeEnum")
@XmlJavaTypeAdapter(PlatformAdapter.class)
public enum Platform {
    INTERNETVOD,
    TVVOD,
    PLUSVOD, // NLZiet, platform "extra" in cooperation with dutch commercial broadcasters
    NPOPLUSVOD // NPOPlus, platform "plusx" is the NPO only offspring/splitoff from NLZiet
}
