/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
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
import nl.vpro.domain.image.ImageFormat;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * The implementation of {@link BackendImageMetadata}
 *
 * This is used to (temporary) represent an image on the image backend server for use in poms gui.
 *
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
public class BasicBackendImageMetadata implements Serializable, MutableEmbargo<BasicBackendImageMetadata>, BackendImageMetadata<BasicBackendImageMetadata> {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * The public 'urn' of the image, which can be used to construct URLs. This is actually the urn of the image on the image-server.
     * The media server wraps it with its own image object with its own urn, and stores the reference to the image-server in 'imageUri'.
     * <p>
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


    public BasicBackendImageMetadata() {
        this.uploadId = null;
    }


    public BasicBackendImageMetadata(UUID uploadId) {
        this.uploadId = uploadId;
    }


    public static BasicBackendImageMetadata of(BackendImageMetadata<?> image) {
        BasicBackendImageMetadata metaData = new BasicBackendImageMetadata();
        metaData.copyFrom(image);
        metaData.imageUri= image.getImageUri();
        Embargos.copy(image, metaData);
        return metaData;
    }


    @NonNull
    @Override
    public BasicBackendImageMetadata setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;

    }

    @NonNull
    @Override
    public BasicBackendImageMetadata setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;
    }


    @Override
    public BasicBackendImageMetadata setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setUrlLastModified(Instant urlLastModified) {
        this.urlLastModified = urlLastModified;
        return this;
    }

    @Override
    public BasicBackendImageMetadata setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }


    @Override
    public Instant getLastModifiedInstant() {
        return getLastModified();
    }

    @Override
    public void setLastModifiedInstant(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Instant getCreationInstant() {
        return getLastModifiedInstant();
    }

    @Override
    public BasicBackendImageMetadata setImageUri(String imageUri) {
        this.imageUri = imageUri;
        return this;
    }

}
