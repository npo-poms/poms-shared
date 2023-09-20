/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
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
 * See https://wiki.vpro.nl/display/npoplayer/Web+player+documentatie
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
    @Deprecated
    NPOPlayerComscore comscore;
    Integer progress;
    Integer trackProgress;
    Integer skipCatalog;
    Boolean noAds;
    String subtitleLanguage;
    String shareUrl;
    Boolean share;
    String placeholder;
    Boolean endscreen;
    Boolean recommendations;
    Boolean moreButton;
    Boolean nextEpisode;
    Boolean skipCountdown;
    Boolean errorButton;
    Boolean hasSettings;
    String sterReferralUrl;
    String sterSiteId;
    String sterIdentifier;
    Boolean hasAdConsent;
    String pageUrl;
    NPOPlayerAtinternet smarttag;
    NPOPlayerTopSpin topspin;
    Encryption encryption;

    public static class Builder {
        public Builder from(PlayerRequest request) {
            return
                autoplay(request.getAutoplay())
                    .startAt(TimeUtils.toSecondsInteger(request.getStartAt()).orElse(null))
                    .endAt(TimeUtils.toSecondsInteger(request.getEndAt()).orElse(null))
                    .id(request.getId())
                    .noAds(request.getNoAds())
                    .stylesheet(request.getStylesheet())
                    .subtitleLanguage(request.getSubtitleLanguage())
                    .sterReferralUrl(request.getSterReferralUrl())
                    .sterSiteId(request.getSterSiteId())
                    .sterIdentifier(request.getSterIdentifier())
                    .hasAdConsent(request.getHasAdConsent())
                    .pageUrl(request.getPageUrl())
                    .share(request.getShare())
                    .hasSettings(hasSettings)
                    .encryption(encryption)
                    .smarttag(NPOPlayerAtinternet.builder().siteId(request.getAtInternetSiteId()).build())
                ;
        }
    }
}

