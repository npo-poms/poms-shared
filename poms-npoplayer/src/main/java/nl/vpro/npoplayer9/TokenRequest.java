/*
 * Copyright (C) 2022 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.npoplayer9;

import lombok.Data;

import com.fasterxml.jackson.annotation.*;

/**
 * Token request for NPO Player 9 endpoint. Just does jwt signing
 * @author r.jansen
 * @since 7.8
 */
@Data
@JsonTypeName("tokenRequest")
public class TokenRequest {
    private final String mid;

    @JsonCreator
    public TokenRequest(@JsonProperty("mid") String mid) {
        this.mid = mid;
    }
}
