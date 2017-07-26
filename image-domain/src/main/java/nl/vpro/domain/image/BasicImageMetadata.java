/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author rico
 */
@XmlRootElement(name = "imageMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "imageMetadataType",
    propOrder = {
        "urn",
        "title",
        "description",
        "height",
        "width",
        "heightInMm",
        "widthInMm",
        "type",
        "size",
        "downloadUrl",
        "etag",
        "license",
        "source",
        "sourceName",
        "imageFormat",
        "credits",
        "uploadId"
    }
)
@Data
@ToString
public class BasicImageMetadata implements Serializable, Embargo<BasicImageMetadata>, ImageMetadata<BasicImageMetadata> {

    private static final long serialVersionUID = 0L;

    private String urn;

    private ImageType type;


    private String title;

    private String description;

    private Integer height;

    private Integer width;

    private Float heightInMm;

    private Float widthInMm;

    private Long size;

    private URI downloadUrl;

    private String etag;

    private License license;

    private String source;

    private String sourceName;

    private String credits;

    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStartInstant;

    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStopInstant;

    @XmlAttribute
    private String date;

    private ImageFormat imageFormat;

    private final UUID uploadId;


    public BasicImageMetadata() {
        this.uploadId = null;
    }


    public BasicImageMetadata(UUID uploadId) {
        this.uploadId = uploadId;
    }


    public static BasicImageMetadata of(ImageMetadata<?> image) {
        BasicImageMetadata metaData = new BasicImageMetadata();
        metaData.copyFrom(image);
        metaData.urn = image.getUrn();
        Embargos.copy(image, metaData);
        return metaData;
    }


    @Override
    public BasicImageMetadata setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;

    }

    @Override
    public BasicImageMetadata setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;
    }


    @Override
    public BasicImageMetadata setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }

    @Override
    public BasicImageMetadata setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }

    @Override
    public BasicImageMetadata setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public BasicImageMetadata setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    @Override
    public BasicImageMetadata setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    @Override
    public BasicImageMetadata setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }
}
