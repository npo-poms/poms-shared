/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.*;

import java.time.Duration;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.DurationToSecondsFloatTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;

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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "mid",
    "id",
    "stylesheet",
    "autoplay",
    "startAt",
    "endAt",
    "noAds",
    "hasAdConsent",
    "subtitleLanguage",
    "sterReferralUrl",
    "sterSiteId",
    "sterIdentifier",
    "pageUrl",
    "atInternetSiteId",
    "share",
    "encryption"
})
@JsonTypeName("request")
public class PlayerRequest {
    String mid;
    String id;
    String stylesheet;

    Boolean autoplay;

    @JsonSerialize(using= DurationToSecondsFloatTimestamp.Serializer.class)
    @JsonDeserialize(using= DurationToSecondsFloatTimestamp.Deserializer.class)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    Duration startAt;

    @JsonSerialize(using= DurationToSecondsFloatTimestamp.Serializer.class)
    @JsonDeserialize(using= DurationToSecondsFloatTimestamp.Deserializer.class)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
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
