package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.i18n.Displayable;

/**
 * See <a href="https://publiekeomroep.atlassian.net/browse/P0MS-236">jira</a>
 * @since 8.6
 */
public enum ChapterType implements Displayable {

    /**
     * See <a href="https://nl.wikipedia.org/wiki/Ident">Wikipedia</a>
     */
    IDENT("Ident"),

    RECAP("samenvatting"),

    INTRO("introductie"),

    MAIN("hoofdgedeelte"),

    CREDITS("aftiteling")
    ;

    @Getter
    private final String displayName;


    ChapterType(String displayName) {
        this.displayName = displayName;
    }
}
