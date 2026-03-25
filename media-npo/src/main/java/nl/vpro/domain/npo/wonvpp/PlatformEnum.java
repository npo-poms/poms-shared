package nl.vpro.domain.npo.wonvpp;

import com.fasterxml.jackson.annotation.*;

public enum PlatformEnum {
    @JsonProperty("npo-svod") npo_svod,
    @JsonProperty("npo-vod") npo_vod;

}
