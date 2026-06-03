/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author e.kuijt
 * @since 5.10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "partyId",
    "profileId"
})
@JsonTypeName("comscore")
@Deprecated
public class NPOPlayerTopSpin {
    String partyId;
    String profileId;
}
