/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

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
    private String referrer;


    @XmlAttribute
    private LinkType type;

    @XmlValue
    @NoHtml
    @JsonProperty("title")
    private String title;

    protected Referral() {
    }

    public Referral(String referrer, String linkText) {
        this(referrer, linkText, null);
    }

    public Referral(String referrer, String linkText, LinkType type) {
        this.referrer = referrer;
        this.title = linkText;
        this.type = type;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getLinkText() {
        return title;
    }

    public void setLinkText(String linkText) {
        this.title = linkText;
    }



    @Override
    public String getText() {
        return getLinkText();

    }

    @Override
    public String getPageRef() {
        return getReferrer();

    }

    @Override
    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }
}
