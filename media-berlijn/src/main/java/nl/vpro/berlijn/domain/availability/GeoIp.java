package nl.vpro.berlijn.domain.availability;

import lombok.Getter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    @NonNull
    public static GeoIp ofNullable(@Nullable GeoIp geoIp) {
        if (geoIp == null) {
            return WR;
        }
        return geoIp;
    }
}
