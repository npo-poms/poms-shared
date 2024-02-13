package nl.vpro.domain.media.gtaa;

import lombok.Getter;

import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;

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

    @Getter
    private final String displayName;

    GTAAStatus(String displayName) {
        this.displayName = displayName;
    }
}
