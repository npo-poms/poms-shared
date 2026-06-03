/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author r.jansen
 * @since 5.10
 */
@XmlRootElement
@Data
@lombok.Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "mid",
    "token",
    "embedUrl",
    "embedCode"
})
@JsonTypeName("response")
@Deprecated
public class PlayerResponse {
    private String mid;
    private String token;
    private String embedUrl;
    private String embedCode;

    public static class Builder {
        public Builder from(NPOPlayerApiResponse response) {
            return
                token(response.getToken())
                    .embedUrl(response.getEmbedUrl())
                    .embedCode(response.getEmbedCode());
        }
    }
}
