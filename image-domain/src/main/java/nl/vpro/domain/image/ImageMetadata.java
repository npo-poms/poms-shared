/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import nl.vpro.domain.Xmlns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URL;

/**
 * @author rico
 * @date 24/08/2016
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "imageMetadata", namespace = Xmlns.IMAGE_NAMESPACE)
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
        "etag" }
)
public class ImageMetadata {
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

    private URL downloadUrl;

    private String etag;


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
        metaData.width = image.getHeight();
        metaData.heightInMm = image.getHeightInMm();
        metaData.widthInMm = image.getWidthInMm();
        metaData.size = image.getSize();
        metaData.downloadUrl = image.getDownloadUrl();
        metaData.etag = image.getEtag();
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
        image.setDownloadUrl(downloadUrl);
        image.setEtag(etag);
        return image;
    }

    public String getUrn() {
        return urn;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Float getHeightInMm() {
        return heightInMm;
    }

    public Float getWidthInMm() {
        return widthInMm;
    }

    public Long getSize() {
        return size;
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    public String getEtag() {
        return etag;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
    }

    public void setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public String toString() {
        return "ImageMetaData{" +
            "urn='" + urn + '\'' +
            ", imageType=" + imageType +
            ", mimeType='" + mimeType + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", height=" + height +
            ", width=" + width +
            ", heightInMm=" + heightInMm +
            ", widthInMm=" + widthInMm +
            ", size=" + size +
            ", downloadUrl=" + downloadUrl +
            ", etag='" + etag + '\'' +
            '}';
    }
}
