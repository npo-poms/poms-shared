package nl.vpro.domain.media.gtaa;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Displayable;
import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@XmlType(namespace = Xmlns.MEDIA_NAMESPACE, name = "gtaaStatusType")
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
