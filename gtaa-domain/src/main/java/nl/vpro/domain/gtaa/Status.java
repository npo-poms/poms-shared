/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.Getter;

import nl.vpro.domain.Displayable;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
public enum Status implements Displayable {
    candidate("Kandidaat"),
    approved("Goedgekeurd"),
    redirected("Doorverwijzing"),
    not_compliant("Voldoet niet"),
    rejected("Afgewezen"),
    obsolete("Achterhaald"),
    deleted("Verwijderd");

    @Getter
    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
