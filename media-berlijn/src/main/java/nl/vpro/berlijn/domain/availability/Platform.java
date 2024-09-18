package nl.vpro.berlijn.domain.availability;

import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * See <a href="https://publiekeomroep.atlassian.net/wiki/spaces/MS/pages/2964521011/Media+Availability+service#Platforms">wiki</a>
 */
public enum Platform {

    @JsonProperty("npo-svod")
    npo_svod(nl.vpro.domain.media.Platform.PLUSVOD),

    @JsonProperty("npo-fvod")
    npo_fvod(nl.vpro.domain.media.Platform.INTERNETVOD),

    npoplusx(null),

    @JsonProperty("distri-nl")
    distri_nl(nl.vpro.domain.media.Platform.TVVOD),

    @JsonProperty("distri-be")
    distri_be(nl.vpro.domain.media.Platform.TVVOD_BE),

    hergebruik(null),


    /**
     * @since 8.2
     */
    @JsonProperty("revoke-xml-integration-test-platform")
    revoke_xml_integration_test_platform(nl.vpro.domain.media.Platform.TEST);

    @Getter
    private final nl.vpro.domain.media.Platform pomsPlatform;


    Platform(nl.vpro.domain.media.Platform pomsPlatform) {
        this.pomsPlatform = pomsPlatform;

    }
}
