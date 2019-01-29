/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.npoplayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.util.TimeUtils;

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
    "id",
    "stylesheet",
    "autoplay",
    "startAt",
    "endAt",
    "noAds",
    "subtitleLanguage"
})
public class NPOPlayerApiRequest {
    String id;
    String elementId;
    String stylesheet;
    NPOPlayerStyling styling;
    String color;
    Boolean autoplay;
    String overlay;
    Integer startAt;
    Integer endAt;
    NPOPlayerComscore comscore;
    Integer progress;
    Integer trackProgress;
    Integer skipCatalog;
    Boolean noAds;
    String subtitleLanguage;
    String shareUrl;
    String placeholder;
    String sterReferralUrl;
    String sterSiteId;
    String sterIdentifier;
    Boolean hasAdConsent;
    String pageUrl;
    NPOPlayerAtinternet smarttag;
    NPOPlayerTopSpin topspin;

    public static class Builder {
        public Builder from(PlayerRequest request) {
            return
                autoplay(request.getAutoplay())
                    .startAt(TimeUtils.toSeconds(request.getStartAt()).orElse(null))
                    .endAt(TimeUtils.toSeconds(request.getEndAt()).orElse(null))
                    .id(request.getId())
                    .noAds(request.getNoAds())
                    .stylesheet(request.getStylesheet())
                    .subtitleLanguage(request.getSubtitleLanguage())
                    .sterReferralUrl(request.getSterReferralUrl())
                    .sterSiteId(request.getSterSiteId())
                    .sterIdentifier(request.getSterIdentifier())
                    .hasAdConsent(request.getHasAdConsent())
                    .pageUrl(request.getPageUrl())
                    .smarttag(NPOPlayerAtinternet.builder().siteId(request.getAtInternetSiteId()).build())
                ;
        }
    }
}

