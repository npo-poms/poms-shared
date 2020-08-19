package nl.vpro.domain.page;

import lombok.Getter;

/**
 * @author Michiel Meeuwissen
 * @since 5.15
 */

@Getter
public enum PageIdMatch {

     URL(true, false),
     CRID(false, true),
     BOTH(true, true);
     private final boolean matchUrl;
     private final boolean matchCrid;

     PageIdMatch(boolean matchUrl, boolean matchCrid) {
         this.matchUrl = matchUrl;
         this.matchCrid = matchCrid;
     }

}
