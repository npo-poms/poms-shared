/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import nl.vpro.i18n.Displayable;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
// I can't get open api enum working satisfactory
@Schema(implementation = String.class, type="string")
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
