package nl.npo.wonvpp.domain;

import com.fasterxml.jackson.annotation.*;

public enum PlatformEnum {
    @JsonProperty("npo-svod") npo_svod,
    @JsonProperty("npo-vod") npo_vod;

}
