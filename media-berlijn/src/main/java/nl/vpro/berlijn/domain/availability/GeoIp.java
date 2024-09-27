package nl.vpro.berlijn.domain.availability;

import lombok.Getter;

import nl.vpro.domain.media.Region;

public enum GeoIp {
    EU(Region.EU),
    NLAll(Region.NLALL),
    NLBES(Region.NLBES),
    NL(Region.NL),
    /**
     * @since 8.2
     */
    Europa(Region.EUROPE),

    /**
     *
     * @since 8.2
     */
    WR(Region.WR);

    @Getter
    private final Region pomsRegion;

    GeoIp(Region pomsRegion) {
        this.pomsRegion = pomsRegion;

    }
}
