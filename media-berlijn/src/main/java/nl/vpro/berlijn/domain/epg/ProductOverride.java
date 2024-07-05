package nl.vpro.berlijn.domain.epg;

import nl.vpro.berlijn.domain.productmetadata.Synopsis;

public record ProductOverride(
    Synopsis synopsis,
    String title
) {
}
