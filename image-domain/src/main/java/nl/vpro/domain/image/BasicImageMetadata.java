/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import lombok.*;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Embargos;
import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author rico
 */
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "imageMetadataType",
    propOrder = {
        "imageUri",
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
        "urlLastModified",
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
public class BasicImageMetadata implements Serializable, MutableEmbargo<BasicImageMetadata>, ImageMetadata<BasicImageMetadata> {

    private static final long serialVersionUID = 0L;

    /**
     * The public 'urn' of the image, which can be used to construct URLs. This is actually the urn of the image on the image-server.
     * The media server wraps it with its own image object with its own urn, and stores the reference to the image-server in 'imageUri'.
     *
     * So imageUri is an identifier for the actual image itself, the rest is metadata which may vary between different object which
     * still have the same imageUri.
     */
    private String imageUri;

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


    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant urlLastModified;

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


    @XmlAttribute(name = "lastModified")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant lastModified;


    public BasicImageMetadata() {
        this.uploadId = null;
    }


    public BasicImageMetadata(UUID uploadId) {
        this.uploadId = uploadId;
    }


    public static BasicImageMetadata of(ImageMetadata<?> image) {
        BasicImageMetadata metaData = new BasicImageMetadata();
        metaData.copyFrom(image);
        metaData.imageUri= image.getImageUri();
        Embargos.copy(image, metaData);
        return metaData;
    }


    @NonNull
    @Override
    public BasicImageMetadata setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;

    }

    @NonNull
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
    public BasicImageMetadata setUrlLastModified(Instant urlLastModified) {
        this.urlLastModified = urlLastModified;
        return this;
    }

    @Override
    public BasicImageMetadata setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }


    @Override
    public Instant getLastModifiedInstant() {
        return getLastModified();
    }

    public void setLastModifiedInstant(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Instant getCreationInstant() {
        return getLastModifiedInstant();
    }
}
