/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */

package nl.vpro.domain.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.support.PublishableObject;

@Entity
@XmlRootElement(name = "image", namespace = Xmlns.IMAGE_NAMESPACE)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "imageType",
    propOrder = {
        "title",
        "description",
        "height",
        "width",
        "heightInMm",
        "widthInMm",
        "mimeType",
        "size",
        "downloadUrl",
        "etag",
        "data"})
public class Image extends PublishableObject<Image> implements Resource<Image>, Identifiable {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Enumerated(EnumType.STRING)
    private ImageFormat imageFormat;

    @Column(nullable = false)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    private String title;

    @Column
    private String description;

    private Integer height;

    private Integer width;

    private Float heightInMm;

    private Float widthInMm;

    private Long size;

    @Column(columnDefinition = "TEXT", unique = true, length = 1024)
    private String downloadUrl;

    private String etag;

    private byte[] hash;

    @Lob
    private Blob data;

    @Transient
    private InputStream cachedInputStream;

    public Image() {
    }

    public Image(String title) {
        setTitle(title);
    }

    @XmlAttribute
    public ImageType getImageType() {
        return imageType;
    }

    public Image setImageType(ImageType imageType) {
        this.imageType = imageType;
        return this;
    }

    @XmlTransient
    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public Image setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    @Override
    @XmlElement(name = "title", namespace = Xmlns.IMAGE_NAMESPACE)
    public String getTitle() {
        return title;
    }

    @Override
    public Image setTitle(String title) {
        if(title == null || title.length() < 255) {
            this.title = title;
        } else {
            this.title = title.substring(255);
        }
        return this;
    }

    @XmlElement(name = "description", namespace = Xmlns.IMAGE_NAMESPACE)
    public String getDescription() {
        return description;
    }

    public Image setDescription(String description) {
        if(description == null || description.length() < 255) {
            this.description = description;
        } else {
            this.description = description.substring(255);
        }
        return this;
    }

    @XmlElement(name = "mimeType", namespace = Xmlns.IMAGE_NAMESPACE)
    public String getMimeType() {
        if(imageFormat == null) {
            return null;
        }
        return imageFormat.getMimeType();
    }

    public Image setMimeType(String mimeType) throws UnsupportedImageFormatException {
        imageFormat = ImageFormat.forMimeType(mimeType);
        return this;
    }

    @XmlElement(name = "height", namespace = Xmlns.IMAGE_NAMESPACE)
    public Integer getHeight() {
        return height;
    }

    public Image setHeight(Integer height) {
        this.height = height;
        return this;
    }

    @XmlElement(name = "width", namespace = Xmlns.IMAGE_NAMESPACE)
    public Integer getWidth() {
        return width;
    }

    public Image setWidth(Integer width) {
        this.width = width;
        return this;
    }

    @XmlElement(name = "heightMm", namespace = Xmlns.IMAGE_NAMESPACE)
    public Float getHeightInMm() {
        return heightInMm;
    }

    public Image setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }

    @XmlElement(name = "widthMm", namespace = Xmlns.IMAGE_NAMESPACE)
    public Float getWidthInMm() {
        return widthInMm;
    }

    public Image setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }

    @XmlElement(name = "size", namespace = Xmlns.IMAGE_NAMESPACE)
    public Long getSize() {
        return size;
    }

    public Image setSize(Long size) {
        this.size = size;
        return this;
    }

    @XmlTransient
    public String getSizeFormatted() {
        float result;
        String unit;

        // MM: BTW, these units are incorrect.
        // In S.I. the prefixes T, M and k are powers of 10.
        // Correct would be useage of prefixes Ti, Mi and Ki.
        if(size > 1024 * 1024 * 1024) {
            result = size / 1024 * 1024 * 1024;
            unit = "TB";
        } else if(size > 1024 * 1024) {
            result = size / 1024 * 1024;
            unit = "MB";
        } else {
            result = size / 1024;
            unit = "kB";
        }

        return String.format("%1$.1f %2$s", result, unit);
    }

    @XmlTransient
    public Blob getBlob() {
        return data;
    }

    public Image setBlob(Blob data) {
        this.data = data;
        return this;
    }

    @XmlElement(name = "data", namespace = Xmlns.IMAGE_NAMESPACE)
    @XmlMimeType("application/octet-stream")
    public DataHandler getData() {
        return new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                if(cachedInputStream == null) {
                    try {
                        return data.getBinaryStream();
                    } catch(SQLException e) {
                        throw new IOException(e);
                    }
                }

                return cachedInputStream;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                try {
                    return data.setBinaryStream(1);
                } catch(SQLException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public String getContentType() {
                return getMimeType();
            }

            @Override
            public String getName() {
                return title;
            }
        });
    }

    @XmlTransient
    void setCachedInputStream(InputStream cachedInputStream) {
        this.cachedInputStream = cachedInputStream;
    }

    @Override
    public String getUrn() {
        return getId() == null ? null : "urn:vpro:image:" + getId();
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @XmlTransient
    public byte[] getHash() {
        return hash;
    }

    protected void setHash(byte[] hash) {
        this.hash = hash;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
