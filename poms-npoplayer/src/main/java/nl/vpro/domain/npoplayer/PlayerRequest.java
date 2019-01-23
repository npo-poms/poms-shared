/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonTypeName;

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
    "mid",
    "id",
    "stylesheet",
    "autoplay",
    "startAt",
    "endAt",
    "noAds",
    "subtitleLanguage"
})
@JsonTypeName("request")
public class PlayerRequest {
    String mid;
    String id;
    String stylesheet;
    Boolean autoplay;
    Integer startAt;
    Integer endAt;
    Boolean noAds;
    String subtitleLanguage;
    String sterReferralUrl;
    String sterSiteId;
    String sterIdentifier;
    Boolean hasAdConsent;
    String pageUrl;
    String atInternetSiteId;
}
