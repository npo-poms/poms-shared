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

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

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
        "imageType",
        "mimeType",
        "size",
        "downloadUrl",
        "etag",
        "license",
        "source",
        "sourceName",
        "imageFormat",
        "credits"

    }
)
@Data
@ToString
public class ImageMetadata implements Serializable, Embargo<ImageMetadata> {

    private static final long serialVersionUID = 0L;

    private String urn;

    private ImageType imageType;

    private String mimeType;

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
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant date;

    private ImageFormat imageFormat;


    public ImageMetadata() {
    }



    public static ImageMetadata of(Image image) {
        ImageMetadata metaData = new ImageMetadata();
        metaData.urn = image.getUrn();
        metaData.imageType = image.getImageType();
        metaData.mimeType = image.getMimeType();
        metaData.title = image.getTitle();
        metaData.description = image.getDescription();
        metaData.height = image.getHeight();
        metaData.width = image.getWidth();
        metaData.heightInMm = image.getHeightInMm();
        metaData.widthInMm = image.getWidthInMm();
        metaData.size = image.getSize();
        metaData.downloadUrl = image.getDownloadUrl() == null ? null : URI.create(image.getDownloadUrl());
        metaData.etag = image.getEtag();
        metaData.license = image.getLicense();
        metaData.source = image.getSource();
        metaData.sourceName = image.getSourceName();
        metaData.imageFormat = image.getImageFormat();
        Embargos.copy(image, metaData);
        return metaData;
    }

    public void copyToIfUnset(Image image) {
        // Can't do urn because it is a derivative field.
        if (image.getImageType() == null) {
            image.setImageType(imageType);
        }
        if (StringUtils.isEmpty(image.getMimeType())) {
            image.setMimeType(mimeType);
        }
        if (StringUtils.isEmpty(image.getTitle())) {
            image.setTitle(title);
        }
        if (StringUtils.isEmpty(image.getDescription())) {
            image.setDescription(description);
        }
        if (image.getHeight() == null || image.getHeight() < 0) {
            image.setHeight(height);
        }
        if (image.getWidth() == null || image.getWidth() < 0) {
            image.setWidth(width);
        }
        if (image.getHeightInMm() == null || image.getHeightInMm() < 0f) {
            image.setHeightInMm(heightInMm);
        }
        if (image.getWidthInMm() == null || image.getWidthInMm() < 0f) {
            image.setWidthInMm(widthInMm);
        }
        if (image.getSize() == null || image.getSize() < 0) {
            image.setSize(size);
        }
        if (image.getDownloadUrl() == null) {
            image.setDownloadUrl(downloadUrl == null ? null : downloadUrl.toString());
        }
        if (image.getEtag() == null) {
            image.setEtag(etag);
        }
        if (image.getSource() == null) {
            image.setSource(source);
        }
        if (image.getSourceName() == null) {
            image.setSourceName(sourceName);
        }
        if (image.getLicense() == null) {
            image.setLicense(license);
        }
        if (image.getImageFormat() == null) {
            image.setImageFormat(imageFormat);
        }
        Embargos.copyIfTargetUnset(this, image);
    }




    @Override
    public ImageMetadata setPublishStartInstant(Instant publishStart) {
        this.publishStartInstant = publishStart;
        return this;

    }

    @Override
    public ImageMetadata setPublishStopInstant(Instant publishStop) {
        this.publishStopInstant = publishStop;
        return this;

    }
}
