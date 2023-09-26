/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.DurationToSecondsFloatTimestamp;

/**
 * This wraps {@link NPOPlayerApiRequest}. It is a bit simpler and support nicer java types.
 * @author r.jansen
 * @since 5.10
 */
@XmlRootElement
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlType(propOrder = {
    "mid",
    "id",
    "stylesheet",
    "autoplay",
    "startAt",
    "endAt",
    "noAds",
    "subtitleLanguage",
    "atInternetSiteId",
    "share"
})
@JsonTypeName("request")
@Deprecated
public class PlayerRequest {
    String mid;
    String id;
    String stylesheet;

    Boolean autoplay;

    @JsonSerialize(using= DurationToSecondsFloatTimestamp.Serializer.class)
    @JsonDeserialize(using= DurationToSecondsFloatTimestamp.Deserializer.class)
    Duration startAt;

    @JsonSerialize(using= DurationToSecondsFloatTimestamp.Serializer.class)
    @JsonDeserialize(using= DurationToSecondsFloatTimestamp.Deserializer.class)
    Duration endAt;

    Boolean noAds;
    String subtitleLanguage;
    String sterReferralUrl;
    String sterSiteId;
    String sterIdentifier;
    Boolean hasAdConsent;
    String pageUrl;
    String atInternetSiteId;
    Boolean share;
    Encryption encryption;
}
