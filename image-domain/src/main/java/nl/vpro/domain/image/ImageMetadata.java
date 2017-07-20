/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.support.License;

/**
 * @author rico
 */
@XmlRootElement(name = "imageMetadata")
@XmlAccessorType(XmlAccessType.PROPERTY)
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
        "sourceName"
    }
)
@Data
@ToString
public class ImageMetadata implements Serializable {

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

        return metaData;
    }

    public Image copyTo(Image image) {
        // Can't do urn because it is a derivative field.
        image.setImageType(imageType);
        image.setMimeType(mimeType);
        image.setTitle(title);
        image.setDescription(description);
        image.setHeight(height);
        image.setWidth(width);
        image.setHeightInMm(heightInMm);
        image.setWidthInMm(widthInMm);
        image.setSize(size);
        image.setDownloadUrl(downloadUrl.toString());
        image.setEtag(etag);
        image.setSource(source);
        image.setSourceName(sourceName);
        image.setLicense(license);
        return image;
    }


}
