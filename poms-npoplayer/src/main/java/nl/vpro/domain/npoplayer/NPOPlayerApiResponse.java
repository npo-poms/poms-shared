/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author r.jansen
 * @since 5.10
 */
@XmlRootElement
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "token",
    "embedUrl",
    "embedCode"
})
public class NPOPlayerApiResponse {
    private String token;
    private String embedUrl;
    private String embedCode;
}
