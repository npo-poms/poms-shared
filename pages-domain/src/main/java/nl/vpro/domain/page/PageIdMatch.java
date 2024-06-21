package nl.vpro.domain.page;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.15
 */

@Getter
public enum PageIdMatch  {

    URL(true, false),
    CRID(false, true),
    BOTH(true, true),

    /**
     * Indicates that effective page id matching should be determined using {@link #forRef(String)}
     */
    AUTOMATIC(true, true)
    ;
    private final boolean matchUrl;
    private final boolean matchCrid;

    PageIdMatch(boolean matchUrl, boolean matchCrid) {
        this.matchUrl = matchUrl;
        this.matchCrid = matchCrid;
    }

    public static PageIdMatch forRef(String pageRef) {
        if (pageRef.startsWith("crid")) {
            return CRID;
        } else {
            return URL;
        }
    }

}
