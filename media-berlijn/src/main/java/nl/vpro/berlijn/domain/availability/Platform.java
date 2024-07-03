package nl.vpro.berlijn.domain.availability;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO: values need documentation?
 */
public enum Platform {

    @JsonProperty("npo-svod")
    npo_svod,

    @JsonProperty("npo-fvod")
    npo_fvod,

    npoplusx,

    @JsonProperty("distri-nl")
    distri_nl,

    @JsonProperty("distri-be")
    distri_be,

    hergebruik,


    /**
     * wtf?
     * @since 8.2
     */
    @JsonProperty("revoke-xml-integration-test-platform")
    revoke_xml_integration_test_platform
}
