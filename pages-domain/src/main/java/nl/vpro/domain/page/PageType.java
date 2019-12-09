package nl.vpro.domain.page;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "pageTypeEnum")
@JsonDeserialize(using = PageTypeDeserializer.class)
public enum PageType implements Displayable {
    ARTICLE("Artikel"),
    SPECIAL("Special"),
    HOME("Thuis"),
    OVERVIEW("Overzicht"),
    PRODUCT("Product"),
    PLAYER("Speler"),
    AUDIO("Audio"),
    VIDEO("Video"),
    MIXED("Mixed"),
    PLAYLIST("Speellijst"),
    MOVIE("Film"),
    SERIES("Serie"),
    PERSON("Persoon"),
    SEARCH("Zoekpagina");



    private final String displayName;

    PageType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static PageType valueOfDisplayName(String displayName) {
        for(PageType type : PageType.values()) {
            if(type.getDisplayName().equals(displayName)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown PageType for displayName " + displayName);
    }
}
