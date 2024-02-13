/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.parkpost.promo.bind;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.parkpost.ProductCode;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * See <a href="https://jira.vpro.nl/browse/MSE-1324">MSE-1324</a> and test case with binding example
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "NPO_gfxwrp")
@Getter
@Setter
public class PromoEvent {

    @XmlElement(name = "ProductCode")
    private String productCode;

    @XmlElement(name = "OrderCode")
    private String orderCode;

    @XmlElement(name = "PromotedProgramProductCode")
    private String promotedProgramProductCode;

    @XmlElement(name = "Referrer")
    private String referrer;

    @XmlElement(name = "MXF_Name")
    private String mxf_Name;

    @XmlElement(name = "ProgramTitle")
    private String programTitle;


    @XmlElement(name = "EpisodeTitle")
    private String episodeTitle;

    @XmlElement(name = "Net")
    private String net;

    @XmlElement(name = "PromoType")
    private ProductCode.Type promoType;

    @XmlElement(name = "Broadcaster")
    private String broadcaster;

    @XmlElement(name = "TrailerTitle")
    private String trailerTitle;

    @XmlElement(name = "SerieTitle")
    private String serieTitle;

    @XmlElement(name = "FrameCount")
    private Long frameCount;

    @XmlElement(name = "VideoFormat")
    private String videoFormat;

    @XmlElement(name = "FirstTransmissionDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant firstTransmissionDate;

    @XmlElement(name = "PlannedTransmissionDate")
    private String plannedTransmissionDate;

    @XmlElement(name = "PlacingWindowStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant placingWindowStart;

    @XmlElement(name = "PlacingWindowEnd")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    private Instant placingWindowEnd;


    @Deprecated
    @XmlElementWrapper(name = "Files")
    @XmlElement(name = "File")
    private List<File> files;

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getProductCode() + ":" + getProgramTitle();
    }

}
