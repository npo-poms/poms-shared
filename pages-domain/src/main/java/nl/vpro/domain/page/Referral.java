/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

/**
 * A referral is an incoming {@link Association}
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "referralType")
@ToString
public class Referral implements  Association {

    public static Referral of(String url, String title, LinkType type) {
        Referral referral = new Referral();
        referral.setLinkText(title);
        referral.setReferrer(url);
        referral.setType(type);
        return  referral;
    }

    @XmlAttribute
    @URI
    @NotNull
    @Getter
    @Setter
    private String referrer;


    @XmlAttribute
    @Getter
    @Setter
    private LinkType type;

    @XmlValue
    @NoHtml
    @JsonProperty("title")
    @Getter
    @Setter
    private String linkText;

    protected Referral() {
    }

    public Referral(String referrer, String linkText) {
        this(referrer, linkText, null);
    }

    public Referral(String referrer, String linkText, LinkType type) {
        this.referrer = referrer;
        this.linkText = linkText;
        this.type = type;
    }


    @Override
    public String getText() {
        return getLinkText();
    }

    @Override
    public String getPageRef() {
        return getReferrer();

    }

}
