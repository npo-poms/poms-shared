package nl.vpro.domain.media;

import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * @since 8.6
 */
public enum ChapterType {

    @XmlEnumValue("not set")
    not_set,

    ident,

    recap,

    intro,

    main,

    credits
}
