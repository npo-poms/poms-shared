package nl.vpro.domain.media;

import lombok.Getter;

import jakarta.xml.bind.annotation.XmlEnumValue;

import nl.vpro.i18n.Displayable;

/**
 * @since 8.6
 */
public enum ChapterType implements Displayable {

    @XmlEnumValue("not set")
    not_set("niet gezet"),

    ident("?"),

    recap("samenvatting"),

    intro("introductie"),

    main("hoofdgedeelte"),

    credits("aftiteling")
    ;

    @Getter
    private final String displayName;


    ChapterType(String displayName) {
        this.displayName = displayName;
    }
}
