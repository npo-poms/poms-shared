package nl.vpro.domain.media.gtaa;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public enum GTAAStatus implements Displayable {
    candidate("Kandidaat"),
    approved("Goedgekeurd"),
    redirected("Doorverwijzing"),
    not_compliant("Voldoet niet"),
    rejected("Afgewezen"),
    obsolete("Achterhaald"),
    deleted("Verwijderd");

    private String displayName;

    GTAAStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
