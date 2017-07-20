/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */

package nl.vpro.domain.image;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.support.License;
import nl.vpro.domain.image.support.PublishableObject;
import nl.vpro.validation.WarningValidatorGroup;

@Entity
@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
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
        "source",
        "sourceName",
        "license",
        "data"})
public class Image extends PublishableObject<Image> implements Resource<Image>, Identifiable {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute
    private ImageType imageType;

    @Enumerated(EnumType.STRING)
    @XmlTransient
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

    @XmlElement(name = "heightMm")
    private Float heightInMm;

    @XmlElement(name = "widthMm")
    private Float widthInMm;

    private Long size;

    @Column(columnDefinition = "TEXT", unique = true, length = 1024)
    private String downloadUrl;

    private String etag;

    @XmlTransient
    private byte[] hash;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Getter
    @Setter
    private License license;

    @Getter
    @Setter
    private String source;

    @Getter
    @Setter
    private String sourceName;

    @Lob
    @XmlTransient
    private Blob data;

    @Transient
    @XmlTransient
    private InputStream cachedInputStream;

    public Image() {
    }

    public Image(String title) {
        setTitle(title);
    }


    public ImageType getImageType() {
        return imageType;
    }

    public Image setImageType(ImageType imageType) {
        this.imageType = imageType;
        return this;
    }


    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public Image setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    @Override
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

    @XmlElement
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

    public Integer getHeight() {
        return height;
    }

    public Image setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public Image setWidth(Integer width) {
        this.width = width;
        return this;
    }


    public Float getHeightInMm() {
        return heightInMm;
    }

    public Image setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }


    public Float getWidthInMm() {
        return widthInMm;
    }

    public Image setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }
    public Long getSize() {
        return size;
    }

    public Image setSize(Long size) {
        this.size = size;
        return this;
    }

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

    @XmlElement(name = "data")
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
