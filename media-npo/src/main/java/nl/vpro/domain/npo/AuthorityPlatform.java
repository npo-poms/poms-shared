package nl.vpro.domain.npo;


import nl.vpro.domain.media.Platform;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public enum AuthorityPlatform {
    internetvod(Platform.INTERNETVOD),
    tvvod(Platform.TVVOD),
    webonly(Platform.INTERNETVOD),
    extra(Platform.PLUSVOD),
    hergebruik(null)
    ;

    private final Platform platform;

    AuthorityPlatform(Platform platform) {
        this.platform = platform;
    }

    public Platform getDomainPlatform() {
        return platform;
    }

}

